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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_10.internationalstandingorders;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRSubmissionStatus.INITIATIONPENDING;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRAccountIdentifierConverter.toOBCashAccountDebtor4;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRChargeConverter.toOBWriteDomesticConsentResponse5DataCharges;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRSubmissionStatusConverter.toOBWriteInternationalStandingOrderResponse7DataStatus;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRWriteInternationalStandingOrderConsentConverter.toOBWriteInternationalStandingOrderConsent6;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRWriteInternationalStandingOrderConsentConverter.toOBWriteInternationalStandingOrderConsentResponse7DataInitiation;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRWriteInternationalStandingOrderConverter.toFRWriteInternationalStandingOrder;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRResponseDataRefundConverter;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRInternationalResponseDataRefund;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteInternationalStandingOrder;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteInternationalStandingOrderData;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_10.internationalstandingorders.InternationalStandingOrdersApi;
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
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteInternationalStandingOrder4Validator.OBWriteInternationalStandingOrder4ValidationContext;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.internationalstandingorder.v3_1_10.InternationalStandingOrderConsentStoreClient;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.payment.ConsumePaymentConsentRequest;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.payment.internationalstandingorder.v3_1_10.InternationalStandingOrderConsent;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.payment.FRInternationalStandingOrderPaymentSubmission;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.payments.InternationalStandingOrderPaymentSubmissionRepository;
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import uk.org.openbanking.datamodel.v3.common.Meta;
import uk.org.openbanking.datamodel.v3.payment.OBWriteInternationalStandingOrder4;
import uk.org.openbanking.datamodel.v3.payment.OBWriteInternationalStandingOrderResponse7;
import uk.org.openbanking.datamodel.v3.payment.OBWriteInternationalStandingOrderResponse7Data;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1Data;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInner;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatus;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetail;
import uk.org.openbanking.datamodel.v3.payment.OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetailStatusReason;

@Controller("InternationalStandingOrdersApiV3.1.10")
public class InternationalStandingOrdersApiController implements InternationalStandingOrdersApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final InternationalStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;
    private final InternationalStandingOrderConsentStoreClient consentStoreClient;
    private final OBValidationService<OBWriteInternationalStandingOrder4ValidationContext> paymentValidator;
    private final RefundAccountService refundAccountService;
    private final IdempotentPaymentService<FRInternationalStandingOrderPaymentSubmission, FRWriteInternationalStandingOrder> idempotentPaymentService;

    public InternationalStandingOrdersApiController(
            InternationalStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            InternationalStandingOrderConsentStoreClient consentStoreClient,
            OBValidationService<OBWriteInternationalStandingOrder4ValidationContext> paymentValidator,
            RefundAccountService refundAccountService) {
        this.standingOrderPaymentSubmissionRepository = standingOrderPaymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
        this.consentStoreClient = consentStoreClient;
        this.paymentValidator = paymentValidator;
        this.refundAccountService = refundAccountService;
        this.idempotentPaymentService = new SinglePaymentForConsentIdempotentPaymentService<>(standingOrderPaymentSubmissionRepository);
    }

    @Override
    public ResponseEntity<OBWriteInternationalStandingOrderResponse7> createInternationalStandingOrders(
            @Valid OBWriteInternationalStandingOrder4 obWriteInternationalStandingOrder4,
            String authorization,
            String xIdempotencyKey,
            String xJwsSignature,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException, OBErrorException {
        logger.debug("Received payment submission: '{}'", obWriteInternationalStandingOrder4);

        paymentSubmissionValidator.validateIdempotencyKey(xIdempotencyKey);

        String consentId = obWriteInternationalStandingOrder4.getData().getConsentId();
        logger.debug("Attempting to get consent: {}, clientId: {}", consentId, apiClientId);
        final InternationalStandingOrderConsent consent = consentStoreClient.getConsent(consentId, apiClientId);
        logger.debug("Got consent from store: {}", consent);

        FRWriteInternationalStandingOrder frStandingOrder = toFRWriteInternationalStandingOrder(obWriteInternationalStandingOrder4);
        logger.trace("Converted to: '{}'", frStandingOrder);

        final Optional<FRInternationalStandingOrderPaymentSubmission> existingPayment =
                idempotentPaymentService.findExistingPayment(frStandingOrder, consentId, apiClientId, xIdempotencyKey);
        if (existingPayment.isPresent()) {
            logger.info("Payment submission is a replay of a previous payment, returning previously created payment for x-idempotencyKey: {}, consentId: {}",
                    xIdempotencyKey, consentId);
            return ResponseEntity.status(CREATED).body(responseEntity(consent, existingPayment.get()));
        }

        // validate the consent against the request
        logger.debug("Validating International Standing Order submission");
        paymentValidator.validate(new OBWriteInternationalStandingOrder4ValidationContext(obWriteInternationalStandingOrder4,
                toOBWriteInternationalStandingOrderConsent6(consent.getRequestObj()), consent.getStatus()));
        logger.debug("International Standing Order validation successful");

        FRInternationalStandingOrderPaymentSubmission frPaymentSubmission = FRInternationalStandingOrderPaymentSubmission.builder()
                .id(obWriteInternationalStandingOrder4.getData().getConsentId())
                .standingOrder(frStandingOrder)
                .status(INITIATIONPENDING)
                .created(new Date())
                .updated(new Date())
                .idempotencyKey(xIdempotencyKey)
                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                .build();

        // Save the international standing order
        frPaymentSubmission = idempotentPaymentService.savePayment(frPaymentSubmission);

        final ConsumePaymentConsentRequest consumePaymentRequest = new ConsumePaymentConsentRequest();
        consumePaymentRequest.setConsentId(consentId);
        consumePaymentRequest.setApiClientId(apiClientId);
        consentStoreClient.consumeConsent(consumePaymentRequest);

        return ResponseEntity.status(CREATED).body(responseEntity(consent, frPaymentSubmission));
    }

    @Override
    public ResponseEntity getInternationalStandingOrdersInternationalStandingOrderPaymentId(
            String internationalStandingOrderPaymentId,
            String authorization,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal
    ) {
        Optional<FRInternationalStandingOrderPaymentSubmission> isPaymentSubmission = standingOrderPaymentSubmissionRepository.findById(internationalStandingOrderPaymentId);
        if (!isPaymentSubmission.isPresent()) {
            return ResponseEntity.status(BAD_REQUEST).body("Payment submission '" + internationalStandingOrderPaymentId + "' can't be found");
        }

        FRInternationalStandingOrderPaymentSubmission frPaymentSubmission = isPaymentSubmission.get();
        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frPaymentSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frPaymentSubmission, apiVersion);
        }

        //get the consent
        final String consentId = frPaymentSubmission.getConsentId();
        logger.debug("Attempting to get consent: {}, clientId: {}", consentId, apiClientId);
        final InternationalStandingOrderConsent consent = consentStoreClient.getConsent(consentId, apiClientId);
        logger.debug("Got consent from store: {}", consent);

        return ResponseEntity.ok(responseEntity(consent, frPaymentSubmission));
    }

    @Override
    public ResponseEntity getInternationalStandingOrdersInternationalStandingOrderPaymentIdPaymentDetails(
            String internationalStandingOrderPaymentId,
            String authorization,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal
    ) {
        Optional<FRInternationalStandingOrderPaymentSubmission> isInternationalStandingOrderSubmission = standingOrderPaymentSubmissionRepository.findById(internationalStandingOrderPaymentId);
        if (!isInternationalStandingOrderSubmission.isPresent()) {
            return ResponseEntity.status(BAD_REQUEST).body("International standing order submission '" + internationalStandingOrderPaymentId + "' can't be found");
        }

        FRInternationalStandingOrderPaymentSubmission frStandingOrderSubmission = isInternationalStandingOrderSubmission.get();
        logger.debug("Found The International Standing Order '{}' to get details.", internationalStandingOrderPaymentId);
        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frStandingOrderSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frStandingOrderSubmission, apiVersion);
        }
        return ResponseEntity.ok(responseEntityDetails(frStandingOrderSubmission));
    }

    private OBWriteInternationalStandingOrderResponse7 responseEntity(
            InternationalStandingOrderConsent consent,
            FRInternationalStandingOrderPaymentSubmission frPaymentSubmission
    ) {
        FRWriteInternationalStandingOrderData data = frPaymentSubmission.getStandingOrder().getData();

        final Optional<FRInternationalResponseDataRefund> refundAccountData = refundAccountService.getInternationalPaymentRefundData(
                consent.getRequestObj().getData().getReadRefundAccount(),
                consent.getRequestObj().getData().getInitiation().getCreditor(),
                consent.getRequestObj().getData().getInitiation().getCreditorAgent(),
                consent);

        return new OBWriteInternationalStandingOrderResponse7()
                .data(new OBWriteInternationalStandingOrderResponse7Data()
                        .charges(toOBWriteDomesticConsentResponse5DataCharges(consent.getCharges()))
                        .internationalStandingOrderId(frPaymentSubmission.getId())
                        .initiation(toOBWriteInternationalStandingOrderConsentResponse7DataInitiation(data.getInitiation()))
                        .creationDateTime(new DateTime(frPaymentSubmission.getCreated().getTime()))
                        .statusUpdateDateTime(new DateTime(frPaymentSubmission.getUpdated().getTime()))
                        .status(toOBWriteInternationalStandingOrderResponse7DataStatus(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId())
                        .debtor(toOBCashAccountDebtor4(data.getInitiation().getDebtorAccount()))
                        .refund(refundAccountData.map(FRResponseDataRefundConverter::toOBWriteInternationalStandingOrderResponse7DataRefund).orElse(null))
                )
                .links(LinksHelper.createInternationalStandingOrderPaymentLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }

    private OBWritePaymentDetailsResponse1 responseEntityDetails(FRInternationalStandingOrderPaymentSubmission frStandingOrderSubmission) {
        OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatus status = OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatus.fromValue(
                PaymentsUtils.statusLinkingMap.get(frStandingOrderSubmission.getStatus().getValue())
        );

        // Build the response object with data to meet the expected data defined by the spec
        OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetailStatusReason statusReasonEnum = OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetailStatusReason.PENDINGSETTLEMENT;
        return new OBWritePaymentDetailsResponse1()
                .data(
                        new OBWritePaymentDetailsResponse1Data()
                                .addPaymentStatusItem(
                                        new OBWritePaymentDetailsResponse1DataPaymentStatusInner()
                                                .status(status)
                                                .paymentTransactionId(UUID.randomUUID().toString())
                                                .statusUpdateDateTime(new DateTime(frStandingOrderSubmission.getUpdated()))
                                                .statusDetail(
                                                        new OBWritePaymentDetailsResponse1DataPaymentStatusInnerStatusDetail()
                                                                .status(status.getValue())
                                                                .statusReason(statusReasonEnum)
                                                                .statusReasonDescription(statusReasonEnum.getValue())
                                                )
                                )

                )
                .links(LinksHelper.createInternationalStandingOrderPaymentDetailsLink(this.getClass(), frStandingOrderSubmission.getId()))
                .meta(new Meta());
    }

}
