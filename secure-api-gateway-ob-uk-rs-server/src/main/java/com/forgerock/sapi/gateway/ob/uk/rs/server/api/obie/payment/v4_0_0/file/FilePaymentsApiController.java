/*
 * Copyright © 2020-2024 ForgeRock AS (obst@forgerock.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v4_0_0.file;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRSubmissionStatus.INITIATIONPENDING;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v4.common.FRAccountIdentifierConverter.toOBCashAccountDebtor4;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v4.common.FRSubmissionStatusConverter.toOBWriteFileResponse3DataStatus;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v4.payment.FRWriteFileConsentConverter.toOBWriteFile2DataInitiation;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v4.payment.FRWriteFileConverter.toFRWriteFile;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.mapper.FRModelMapper;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v4.common.FRChargeConverter;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v4.payment.FRWriteFileConsentConverter;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteDataFile;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteFile;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorResponseCategory;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v4_0_0.file.FilePaymentsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.PaymentApiResponseUtil;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.VersionPathExtractor;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.idempotency.IdempotentPaymentService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.idempotency.SinglePaymentForConsentIdempotentPaymentService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.v4.common.util.link.LinksHelper;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.ResourceVersionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.v4.payment.OBWriteFile2Validator.OBWriteFile2ValidationContext;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.file.FilePaymentConsentStoreClient;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.payment.ConsumePaymentConsentRequest;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.payment.file.v3_1_10.FilePaymentConsent;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.payment.FRFilePaymentSubmission;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.payments.FilePaymentSubmissionRepository;
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion;

import jakarta.servlet.http.HttpServletRequest;
import uk.org.openbanking.datamodel.v4.common.Meta;
import uk.org.openbanking.datamodel.v4.common.OBStatusReason;
import uk.org.openbanking.datamodel.v4.payment.OBWriteFile2;
import uk.org.openbanking.datamodel.v4.payment.OBWriteFileConsent3;
import uk.org.openbanking.datamodel.v4.payment.OBWriteFileResponse3;
import uk.org.openbanking.datamodel.v4.payment.OBWriteFileResponse3Data;
import uk.org.openbanking.datamodel.v4.payment.OBWritePaymentDetailsResponse1;

@Controller("FilePaymentsApiV4.0.0")
public class FilePaymentsApiController implements FilePaymentsApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FilePaymentSubmissionRepository filePaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;
    private final FilePaymentConsentStoreClient consentStoreClient;
    private final OBValidationService<OBWriteFile2ValidationContext> filePaymentRequestValidator;
    private final IdempotentPaymentService<FRFilePaymentSubmission, FRWriteFile> idempotentPaymentService;

    public FilePaymentsApiController(
            FilePaymentSubmissionRepository filePaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            @Qualifier("v4.0.0RestFilePaymentConsentStoreClient") FilePaymentConsentStoreClient consentStoreClient,
            OBValidationService<OBWriteFile2ValidationContext> filePaymentRequestValidator) {
        this.filePaymentSubmissionRepository = filePaymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
        this.consentStoreClient = consentStoreClient;
        this.filePaymentRequestValidator = filePaymentRequestValidator;
        this.idempotentPaymentService = new SinglePaymentForConsentIdempotentPaymentService<>(filePaymentSubmissionRepository);
    }

    private OBWriteFileResponse3 responseEntity(FilePaymentConsent filePaymentConsent,
            FRFilePaymentSubmission frPaymentSubmission) {
        FRWriteDataFile data = frPaymentSubmission.getFilePayment().getData();
        OBWriteFileResponse3Data responseData = new OBWriteFileResponse3Data();
        return new OBWriteFileResponse3()
                .data(new OBWriteFileResponse3Data()
                        .charges(FRChargeConverter.toOBWriteDomesticConsentResponse5DataCharges(filePaymentConsent.getCharges()))
                        .filePaymentId(frPaymentSubmission.getId())
                        .initiation(toOBWriteFile2DataInitiation(data.getInitiation()))
                        .creationDateTime(new DateTime(frPaymentSubmission.getCreated().getTime()))
                        .statusUpdateDateTime(new DateTime(frPaymentSubmission.getUpdated().getTime()))
                        .status(toOBWriteFileResponse3DataStatus(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId())
                        .debtor(toOBCashAccountDebtor4(data.getInitiation().getDebtorAccount()))
                        .statusReason(Collections.singletonList(FRModelMapper.map(responseData.getStatusReason(), OBStatusReason.class))))
                .links(LinksHelper.createFilePaymentsLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }

    @Override
    public ResponseEntity<OBWriteFileResponse3> createFilePayments(String authorization, String xIdempotencyKey, String xJwsSignature, OBWriteFile2 obWriteFile2, String xFapiAuthDate, String xFapiCustomerIpAddress, String xFapiInteractionId, String xCustomerUserAgent, String apiClientId, HttpServletRequest request) throws OBErrorResponseException, OBErrorException {
        logger.debug("Received file payment submission: '{}', apiClientId: {}", obWriteFile2, apiClientId);

        paymentSubmissionValidator.validateIdempotencyKey(xIdempotencyKey);

        final String consentId = obWriteFile2.getData().getConsentId();
        final FilePaymentConsent consent = consentStoreClient.getConsent(consentId, apiClientId);

        FRWriteFile frWriteFile = toFRWriteFile(obWriteFile2);
        logger.trace("Converted to: '{}'", frWriteFile);

        final Optional<FRFilePaymentSubmission> existingPayment =
                idempotentPaymentService.findExistingPayment(frWriteFile, consentId, apiClientId, xIdempotencyKey);
        if (existingPayment.isPresent()) {
            logger.info("Payment submission is a replay of a previous payment, returning previously created payment for x-idempotencyKey: {}, consentId: {}",
                    xIdempotencyKey, consentId);
            return ResponseEntity.status(CREATED).body(responseEntity(consent, existingPayment.get()));
        }

        final OBWriteFileConsent3 obConsent = FRWriteFileConsentConverter.toOBWriteFileConsent3(consent.getRequestObj());
        filePaymentRequestValidator.validate(new OBWriteFile2ValidationContext(obWriteFile2, obConsent, consent.getStatus()));

        FRFilePaymentSubmission frPaymentSubmission = FRFilePaymentSubmission.builder()
                .id(consentId)
                .filePayment(frWriteFile)
                .created(new Date())
                .updated(new Date())
                .status(INITIATIONPENDING)
                .idempotencyKey(xIdempotencyKey)
                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                .build();

        // Save the file payment(s)
        frPaymentSubmission = idempotentPaymentService.savePayment(frPaymentSubmission);

        final ConsumePaymentConsentRequest consumePaymentRequest = new ConsumePaymentConsentRequest();
        consumePaymentRequest.setConsentId(consentId);
        consumePaymentRequest.setApiClientId(apiClientId);
        consentStoreClient.consumeConsent(consumePaymentRequest);

        return ResponseEntity.status(CREATED).body(responseEntity(consent, frPaymentSubmission));
    }

    @Override
    public ResponseEntity getFilePaymentsFilePaymentId(String filePaymentId, String authorization, String xFapiAuthDate, String xFapiCustomerIpAddress, String xFapiInteractionId, String xCustomerUserAgent, String apiClientId, HttpServletRequest request) throws OBErrorResponseException {
        Optional<FRFilePaymentSubmission> isPaymentSubmission = filePaymentSubmissionRepository.findById(filePaymentId);
        if (!isPaymentSubmission.isPresent()) {
            throw new OBErrorResponseException(
                    HttpStatus.BAD_REQUEST,
                    OBRIErrorResponseCategory.REQUEST_INVALID,
                    OBRIErrorType.PAYMENT_SUBMISSION_NOT_FOUND
                            .toOBError1(filePaymentId));
        }

        FRFilePaymentSubmission frPaymentSubmission = isPaymentSubmission.get();
        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frPaymentSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frPaymentSubmission, apiVersion);
        }
        final FilePaymentConsent consent = consentStoreClient.getConsent(frPaymentSubmission.getConsentId(), apiClientId);
        return ResponseEntity.ok(responseEntity(consent, frPaymentSubmission));
    }

    @Override
    public ResponseEntity<OBWritePaymentDetailsResponse1> getFilePaymentsFilePaymentIdPaymentDetails(String filePaymentId, String authorization, String xFapiAuthDate, String xFapiCustomerIpAddress, String xFapiInteractionId, String xCustomerUserAgent, String apiClientId) {
        // Optional endpoint - not implemented
        return new ResponseEntity<>(NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Object> getFilePaymentsFilePaymentIdReportFile(String filePaymentId, String authorization, String xFapiAuthDate, String xFapiCustomerIpAddress, String xFapiInteractionId, String xCustomerUserAgent, String apiClientId) {
        return new ResponseEntity<>(NOT_IMPLEMENTED);
    }
}
