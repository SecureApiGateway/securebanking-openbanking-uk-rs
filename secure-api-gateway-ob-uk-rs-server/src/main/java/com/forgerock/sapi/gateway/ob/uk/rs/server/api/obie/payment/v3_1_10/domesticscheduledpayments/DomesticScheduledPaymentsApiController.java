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
/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_10.domesticscheduledpayments;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRSubmissionStatus.INITIATIONPENDING;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.common.FRAccountIdentifierConverter.toOBCashAccountDebtor4;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.common.FRChargeConverter.toOBWriteDomesticConsentResponse5DataCharges;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.common.FRSubmissionStatusConverter.toOBWriteDomesticScheduledResponse5DataStatus;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.payment.FRWriteDomesticScheduledConsentConverter.toOBWriteDomesticScheduled2DataInitiation;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.payment.FRWriteDomesticScheduledConverter.toFRWriteDomesticScheduled;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRResponseDataRefund;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.common.FRResponseDataRefundConverter;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.v3.payment.FRWriteDomesticScheduledConsentConverter;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteDataDomesticScheduled;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteDomesticScheduled;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_10.domesticscheduledpayments.DomesticScheduledPaymentsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.services.RefundAccountService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.PaymentApiResponseUtil;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.PaymentsUtils;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.VersionPathExtractor;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.link.LinksHelper;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.idempotency.IdempotentPaymentService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.idempotency.SinglePaymentForConsentIdempotentPaymentService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.ResourceVersionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.v3.payment.OBWriteDomesticScheduled2Validator.OBWriteDomesticScheduled2ValidationContext;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.domesticscheduled.DomesticScheduledPaymentConsentStoreClient;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.payment.ConsumePaymentConsentRequest;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.payment.domesticscheduled.v3_1_10.DomesticScheduledPaymentConsent;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.payment.FRDomesticScheduledPaymentSubmission;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.payments.DomesticScheduledPaymentSubmissionRepository;
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import uk.org.openbanking.datamodel.v3.common.Meta;
import uk.org.openbanking.datamodel.v3.payment.OBWriteDomesticScheduled2;
import uk.org.openbanking.datamodel.v3.payment.OBWriteDomesticScheduledResponse5;
import uk.org.openbanking.datamodel.v3.payment.OBWriteDomesticScheduledResponse5Data;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1Data;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInner;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatus;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetail;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetailStatusReason;

@Controller("DomesticScheduledPaymentsApiV3.1.10")
public class DomesticScheduledPaymentsApiController implements DomesticScheduledPaymentsApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DomesticScheduledPaymentSubmissionRepository scheduledPaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;
    private final DomesticScheduledPaymentConsentStoreClient consentStoreClient;
    private final OBValidationService<OBWriteDomesticScheduled2ValidationContext> paymentValidator;
    private final RefundAccountService refundAccountService;
    private final IdempotentPaymentService<FRDomesticScheduledPaymentSubmission, FRWriteDomesticScheduled> idempotentPaymentService;

    public DomesticScheduledPaymentsApiController(
            DomesticScheduledPaymentSubmissionRepository paymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            @Qualifier("v3.1.10RestDomesticScheduledPaymentConsentStoreClient") DomesticScheduledPaymentConsentStoreClient consentStoreClient,
            RefundAccountService refundAccountService,
            OBValidationService<OBWriteDomesticScheduled2ValidationContext> paymentValidator
    ) {
        this.scheduledPaymentSubmissionRepository = paymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
        this.consentStoreClient = consentStoreClient;
        this.refundAccountService = refundAccountService;
        this.paymentValidator = paymentValidator;
        this.idempotentPaymentService = new SinglePaymentForConsentIdempotentPaymentService<>(paymentSubmissionRepository);
    }

    @Override
    public ResponseEntity<OBWriteDomesticScheduledResponse5> createDomesticScheduledPayments(
            @Valid OBWriteDomesticScheduled2 obWriteDomesticScheduled2,
            String authorization,
            String xIdempotencyKey,
            String xJwsSignature,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal) throws OBErrorResponseException, OBErrorException {
        logger.debug("Received payment submission: '{}'", obWriteDomesticScheduled2);

        paymentSubmissionValidator.validateIdempotencyKey(xIdempotencyKey);

        String consentId = obWriteDomesticScheduled2.getData().getConsentId();
        logger.debug("Attempting to get consent: {}, clientId: {}", consentId, apiClientId);
        final DomesticScheduledPaymentConsent consent = consentStoreClient.getConsent(consentId, apiClientId);
        logger.debug("Got consent from store: {}", consent);

        FRWriteDomesticScheduled frScheduledPayment = toFRWriteDomesticScheduled(obWriteDomesticScheduled2);
        logger.trace("Converted to: '{}'", frScheduledPayment);

        final Optional<FRDomesticScheduledPaymentSubmission> existingPayment =
                idempotentPaymentService.findExistingPayment(frScheduledPayment, consentId, apiClientId, xIdempotencyKey);
        if (existingPayment.isPresent()) {
            logger.info("Payment submission is a replay of a previous payment, returning previously created payment for x-idempotencyKey: {}, consentId: {}",
                    xIdempotencyKey, consentId);
            return ResponseEntity.status(CREATED).body(responseEntity(consent, existingPayment.get()));
        }

        // validate the consent against the request
        logger.debug("Validating Domestic Scheduled Payment submission");
        final OBWriteDomesticScheduled2ValidationContext validationCtxt = new OBWriteDomesticScheduled2ValidationContext(obWriteDomesticScheduled2,
                FRWriteDomesticScheduledConsentConverter.toOBWriteDomesticScheduledConsent4(consent.getRequestObj()), consent.getStatus());
        paymentValidator.validate(validationCtxt);
        logger.debug("Domestic Scheduled Payment validation successful");

        FRDomesticScheduledPaymentSubmission frPaymentSubmission = FRDomesticScheduledPaymentSubmission.builder()
                .id(obWriteDomesticScheduled2.getData().getConsentId())
                .scheduledPayment(frScheduledPayment)
                .status(INITIATIONPENDING)
                .created(new Date())
                .updated(new Date())
                .idempotencyKey(xIdempotencyKey)
                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                .build();

        // Save the scheduled payment
        frPaymentSubmission = idempotentPaymentService.savePayment(frPaymentSubmission);

        final ConsumePaymentConsentRequest consumePaymentRequest = new ConsumePaymentConsentRequest();
        consumePaymentRequest.setConsentId(consentId);
        consumePaymentRequest.setApiClientId(apiClientId);
        consentStoreClient.consumeConsent(consumePaymentRequest);

        return ResponseEntity.status(CREATED).body(responseEntity(consent, frPaymentSubmission));
    }

    @Override
    public ResponseEntity getDomesticScheduledPaymentsDomesticScheduledPaymentId(
            String domesticScheduledPaymentId,
            String authorization,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal
    ) {
        Optional<FRDomesticScheduledPaymentSubmission> isPaymentSubmission = scheduledPaymentSubmissionRepository.findById(domesticScheduledPaymentId);
        if (!isPaymentSubmission.isPresent()) {
            // OB specifies a 400 when the id does not match an existing consent
            return ResponseEntity.status(BAD_REQUEST).body("Payment submission '" + domesticScheduledPaymentId + "' can't be found");
        }

        FRDomesticScheduledPaymentSubmission frPaymentSubmission = isPaymentSubmission.get();
        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frPaymentSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frPaymentSubmission, apiVersion);
        }
        // Get the consent to update the response
        final DomesticScheduledPaymentConsent consent = consentStoreClient.getConsent(frPaymentSubmission.getConsentId(), apiClientId);
        logger.debug("Got consent from store: {}", consent);

        return ResponseEntity.ok(responseEntity(consent, frPaymentSubmission));
    }

    @Override
    public ResponseEntity getDomesticScheduledPaymentsDomesticScheduledPaymentIdPaymentDetails(
            String domesticScheduledPaymentId,
            String authorization,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal) {

        Optional<FRDomesticScheduledPaymentSubmission> isPaymentSubmission = scheduledPaymentSubmissionRepository.findById(domesticScheduledPaymentId);
        if (!isPaymentSubmission.isPresent()) {
            return ResponseEntity.status(BAD_REQUEST).body("Payment submission '" + domesticScheduledPaymentId + "' can't be found");
        }

        FRDomesticScheduledPaymentSubmission frPaymentSubmission = isPaymentSubmission.get();
        logger.debug("Found The Domestic Scheduled Payment '{}' to get details.", domesticScheduledPaymentId);

        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frPaymentSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frPaymentSubmission, apiVersion);
        }

        return ResponseEntity.ok(responseEntityDetails(frPaymentSubmission));
    }

    private OBWriteDomesticScheduledResponse5 responseEntity(
            DomesticScheduledPaymentConsent consent,
            FRDomesticScheduledPaymentSubmission frPaymentSubmission
    ) {

        final Optional<FRResponseDataRefund> refundAccountData = refundAccountService.getDomesticPaymentRefundData(
                consent.getRequestObj().getData().getReadRefundAccount(), consent);

        FRWriteDataDomesticScheduled data = frPaymentSubmission.getScheduledPayment().getData();
        return new OBWriteDomesticScheduledResponse5()
                .data(new OBWriteDomesticScheduledResponse5Data()
                        .charges(toOBWriteDomesticConsentResponse5DataCharges(consent.getCharges()))
                        .domesticScheduledPaymentId(frPaymentSubmission.getId())
                        .initiation(toOBWriteDomesticScheduled2DataInitiation(data.getInitiation()))
                        .creationDateTime(new DateTime(frPaymentSubmission.getCreated().getTime()))
                        .statusUpdateDateTime(new DateTime(frPaymentSubmission.getUpdated().getTime()))
                        .status(toOBWriteDomesticScheduledResponse5DataStatus(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId())
                        .debtor(toOBCashAccountDebtor4(data.getInitiation().getDebtorAccount()))
                        .refund(refundAccountData.map(FRResponseDataRefundConverter::toOBWriteDomesticResponse5DataRefund).orElse(null))
                )
                .links(LinksHelper.createDomesticScheduledPaymentLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }

    private OBWritePaymentDetailsResponse1 responseEntityDetails(FRDomesticScheduledPaymentSubmission frPaymentSubmission) {
        OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatus status = OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatus.fromValue(
                PaymentsUtils.statusLinkingMap.get(frPaymentSubmission.getStatus().getValue())
        );
        String localInstrument = frPaymentSubmission.getScheduledPayment().getData().getInitiation().getLocalInstrument();

        // Build the response object with data to meet the expected data defined by the spec
        OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetailStatusReason statusReasonEnum = OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetailStatusReason.PENDINGSETTLEMENT;
        return new OBWritePaymentDetailsResponse1()
                .data(
                        new OBWritePaymentDetailsResponse1Data()
                                .addPaymentStatusItem(
                                        new OBWritePaymentDetailsResponse1DataPaymentStatusInner()
                                                .status(status)
                                                .paymentTransactionId(UUID.randomUUID().toString())
                                                .statusUpdateDateTime(new DateTime(frPaymentSubmission.getUpdated()))
                                                .statusDetail(
                                                        new OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetail()
                                                                .localInstrument(localInstrument)
                                                                .status(status.getValue())
                                                                .statusReason(statusReasonEnum)
                                                                .statusReasonDescription(statusReasonEnum.getValue())
                                                )
                                )

                )
                .links(LinksHelper.createDomesticScheduledPaymentDetailsLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }
}
