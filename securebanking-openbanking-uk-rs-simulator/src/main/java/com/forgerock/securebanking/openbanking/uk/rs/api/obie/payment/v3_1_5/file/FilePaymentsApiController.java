/**
 * Copyright © 2020 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.v3_1_5.file;

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDataFile;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteFile;
import com.forgerock.securebanking.openbanking.uk.error.OBErrorResponseException;
import com.forgerock.securebanking.openbanking.uk.error.OBRIErrorResponseCategory;
import com.forgerock.securebanking.openbanking.uk.error.OBRIErrorType;
import com.forgerock.securebanking.openbanking.uk.rs.common.util.VersionPathExtractor;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.payment.FRFilePaymentSubmission;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.IdempotentRepositoryAdapter;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.payments.FilePaymentSubmissionRepository;
import com.forgerock.securebanking.openbanking.uk.rs.validator.PaymentSubmissionValidator;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.account.Meta;
import uk.org.openbanking.datamodel.payment.OBWriteFile2;
import uk.org.openbanking.datamodel.payment.OBWriteFileResponse3;
import uk.org.openbanking.datamodel.payment.OBWriteFileResponse3Data;
import uk.org.openbanking.datamodel.payment.OBWritePaymentDetailsResponse1;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.LinksHelper.createFilePaymentsLink;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.FRAccountIdentifierConverter.toOBDebtorIdentification1;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.payment.FRSubmissionStatusConverter.toOBWriteFileResponse3DataStatus;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.payment.FRWriteFileConsentConverter.toOBWriteFile2DataInitiation;
import static com.forgerock.securebanking.openbanking.uk.rs.converter.payment.FRWriteFileConverter.toFRWriteFile;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Controller("FilePaymentsApiV3.1.5")
@Slf4j
public class FilePaymentsApiController implements FilePaymentsApi {

    private final FilePaymentSubmissionRepository filePaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;

    public FilePaymentsApiController(FilePaymentSubmissionRepository filePaymentSubmissionRepository,
                                     PaymentSubmissionValidator paymentSubmissionValidator) {
        this.filePaymentSubmissionRepository = filePaymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
    }

    public ResponseEntity<OBWriteFileResponse3> createFilePayments(
            OBWriteFile2 obWriteFile2,
            String authorization,
            String xIdempotencyKey,
            String xJwsSignature,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        log.debug("Received file payment submission: '{}'", obWriteFile2);

        paymentSubmissionValidator.validateIdempotencyKey(xIdempotencyKey);

        FRWriteFile frWriteFile = toFRWriteFile(obWriteFile2);
        log.trace("Converted to: '{}'", frWriteFile);

        FRFilePaymentSubmission frPaymentSubmission = FRFilePaymentSubmission.builder()
                .id(UUID.randomUUID().toString())
                .filePayment(frWriteFile)
                .created(new DateTime())
                .updated(new DateTime())
                .idempotencyKey(xIdempotencyKey)
                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                .build();

        // Save the file payment(s)
        frPaymentSubmission = new IdempotentRepositoryAdapter<>(filePaymentSubmissionRepository)
                .idempotentSave(frPaymentSubmission);
        return ResponseEntity.status(CREATED).body(responseEntity(frPaymentSubmission));
    }

    public ResponseEntity<OBWriteFileResponse3> getFilePaymentsFilePaymentId(
            String filePaymentId,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        Optional<FRFilePaymentSubmission> isPaymentSubmission = filePaymentSubmissionRepository.findById(filePaymentId);
        if (!isPaymentSubmission.isPresent()) {
            throw new OBErrorResponseException(
                    HttpStatus.BAD_REQUEST,
                    OBRIErrorResponseCategory.REQUEST_INVALID,
                    OBRIErrorType.PAYMENT_SUBMISSION_NOT_FOUND
                            .toOBError1(filePaymentId));
        }

        return ResponseEntity.ok(responseEntity(isPaymentSubmission.get()));
    }

    public ResponseEntity<OBWritePaymentDetailsResponse1> getFilePaymentsFilePaymentIdPaymentDetails(
            String filePaymentId,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) {
        // Optional endpoint - not implemented
        return new ResponseEntity<>(NOT_IMPLEMENTED);
    }

    public ResponseEntity getFilePaymentsFilePaymentIdReportFile(
            String filePaymentId,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        return new ResponseEntity<>(NOT_IMPLEMENTED);
//        FRFilePaymentSubmission filePayment = filePaymentSubmissionRepository.findById(filePaymentId)
//                .orElseThrow(() ->
//                        new OBErrorResponseException(
//                                HttpStatus.BAD_REQUEST,
//                                OBRIErrorResponseCategory.REQUEST_INVALID,
//                                OBRIErrorType.PAYMENT_ID_NOT_FOUND
//                                        .toOBError1(filePaymentId))
//                );
//        log.debug("Payment File '{}' exists with status: {} so generating a report file for type: '{}'",
//                filePayment.getId(),
//                filePayment.getStatus(),
//                filePayment.getFilePayment().getData().getInitiation().getFileType());
//        String reportFile = paymentReportFileService.createPaymentReport(filePayment);
//        log.debug("Generated report file for Payment File: '{}'", filePayment.getId());
//        return ResponseEntity.ok(reportFile);
    }

    private OBWriteFileResponse3 responseEntity(FRFilePaymentSubmission frPaymentSubmission) {
        FRWriteDataFile data = frPaymentSubmission.getFilePayment().getData();
        return new OBWriteFileResponse3()
                .data(new OBWriteFileResponse3Data()
                        .filePaymentId(frPaymentSubmission.getId())
                        .initiation(toOBWriteFile2DataInitiation(data.getInitiation()))
                        .creationDateTime(frPaymentSubmission.getCreated())
                        .statusUpdateDateTime(frPaymentSubmission.getUpdated())
                        .status(toOBWriteFileResponse3DataStatus(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId())
                        .debtor(toOBDebtorIdentification1(data.getInitiation().getDebtorAccount())))
                .links(createFilePaymentsLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }

}
