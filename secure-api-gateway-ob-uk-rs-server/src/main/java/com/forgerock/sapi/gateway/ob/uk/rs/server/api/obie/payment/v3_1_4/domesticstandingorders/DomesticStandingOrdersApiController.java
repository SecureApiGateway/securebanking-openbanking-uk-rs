/*
 * Copyright © 2020-2022 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_4.domesticstandingorders;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRStandingOrderData;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRReadRefundAccount;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRResponseDataRefund;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRResponseDataRefundConverter;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteDataDomesticStandingOrder;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.payment.FRWriteDomesticStandingOrder;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorResponseCategory;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_4.domesticstandingorders.DomesticStandingOrdersApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.factories.FRStandingOrderDataFactory;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.services.ConsentService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.services.validation.RiskValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.refund.FRReadRefundAccountFactory;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.refund.FRResponseDataRefundFactory;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.PaymentApiResponseUtil;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.PaymentsUtils;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.VersionPathExtractor;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.link.LinksHelper;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.document.payment.FRDomesticStandingOrderPaymentSubmission;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.IdempotentRepositoryAdapter;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.payments.DomesticStandingOrderPaymentSubmissionRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.standingorder.StandingOrderService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.ResourceVersionValidator;
import com.forgerock.sapi.gateway.uk.common.shared.api.meta.obie.OBVersion;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.common.Meta;
import uk.org.openbanking.datamodel.payment.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.common.FRSubmissionStatus.INITIATIONPENDING;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRSubmissionStatusConverter.toOBWriteDomesticStandingOrderResponse5DataStatus;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRWriteDomesticStandingOrderConsentConverter.toOBWriteDomesticStandingOrder3DataInitiation;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRWriteDomesticStandingOrderConverter.toFRWriteDomesticStandingOrder;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@Controller("DomesticStandingOrdersApiV3.1.4")
@Slf4j
public class DomesticStandingOrdersApiController implements DomesticStandingOrdersApi {

    private final DomesticStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;
    private final StandingOrderService standingOrderService;
    private final RiskValidationService riskValidationService;

    private final ConsentService consentService;

    public DomesticStandingOrdersApiController(
            DomesticStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            StandingOrderService standingOrderService,
            ConsentService consentService,
            RiskValidationService riskValidationService
    ) {
        this.standingOrderPaymentSubmissionRepository = standingOrderPaymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
        this.standingOrderService = standingOrderService;
        this.consentService = consentService;
        this.riskValidationService = riskValidationService;
    }

    @Override
    public ResponseEntity<OBWriteDomesticStandingOrderResponse5> createDomesticStandingOrders(
            @Valid OBWriteDomesticStandingOrder3 obWriteDomesticStandingOrder3,
            String authorization,
            String xIdempotencyKey,
            String xJwsSignature,
            String xAccountId,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String xReadRefundAccount,
            HttpServletRequest request,
            Principal principal) throws OBErrorResponseException {
        log.debug("Received payment submission: '{}'", obWriteDomesticStandingOrder3);

        paymentSubmissionValidator.validateIdempotencyKeyAndRisk(xIdempotencyKey, obWriteDomesticStandingOrder3.getRisk());

        String consentId = obWriteDomesticStandingOrder3.getData().getConsentId();
        //get the consent
        JsonObject intent = consentService.getIDMIntent(authorization, consentId);
        log.debug("Retrieved consent from IDM");

        //deserialize the intent to ob response object
        OBWriteDomesticStandingOrderConsentResponse5 consent = consentService.deserialize(
                OBWriteDomesticStandingOrderConsentResponse5.class,
                intent.getAsJsonObject("OBIntentObject"),
                consentId
        );
        log.debug("Deserialized consent from IDM");

        FRWriteDomesticStandingOrder frStandingOrder = toFRWriteDomesticStandingOrder(obWriteDomesticStandingOrder3);
        log.trace("Converted to: '{}'", frStandingOrder);

        // validate the consent against the request
        log.debug("Validating Domestic Standing Order submission");
        try {
            // validates the initiation
            if (!obWriteDomesticStandingOrder3.getData().getInitiation().equals(consent.getData().getInitiation())) {
                throw new OBErrorException(OBRIErrorType.PAYMENT_INVALID_INITIATION,
                        "The initiation field from payment submitted does not match with the initiation field submitted for the consent"
                );
            }
            riskValidationService.validate(consent.getRisk(), obWriteDomesticStandingOrder3.getRisk());
        } catch (OBErrorException e) {
            throw new OBErrorResponseException(
                    e.getObriErrorType().getHttpStatus(),
                    OBRIErrorResponseCategory.REQUEST_INVALID,
                    e.getOBError());
        }
        log.debug("Domestic Standing Order validation successful");

        FRDomesticStandingOrderPaymentSubmission frPaymentSubmission = FRDomesticStandingOrderPaymentSubmission.builder()
                .id(obWriteDomesticStandingOrder3.getData().getConsentId())
                .standingOrder(frStandingOrder)
                .status(INITIATIONPENDING)
                .created(new DateTime())
                .updated(new DateTime())
                .idempotencyKey(xIdempotencyKey)
                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                .build();

        // Save the standing order
        frPaymentSubmission = new IdempotentRepositoryAdapter<>(standingOrderPaymentSubmissionRepository)
                .idempotentSave(frPaymentSubmission);

        // Save the standing order data for the Accounts API
        FRStandingOrderData standingOrderData = FRStandingOrderDataFactory.createFRStandingOrderData(frStandingOrder, xAccountId);
        standingOrderService.createStandingOrder(standingOrderData);
        // Get the consent to update the response
        OBWriteDomesticStandingOrderConsentResponse5 obConsent = consentService.getOBIntentObject(
                OBWriteDomesticStandingOrderConsentResponse5.class,
                authorization,
                obWriteDomesticStandingOrder3.getData().getConsentId()
        );
        return ResponseEntity.status(CREATED).body(
                responseEntity(frPaymentSubmission, FRReadRefundAccountFactory.frReadRefundAccount(xReadRefundAccount), obConsent)
        );
    }

    @Override
    public ResponseEntity getDomesticStandingOrdersDomesticStandingOrderId(
            String domesticStandingOrderId,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String xReadRefundAccount,
            HttpServletRequest request,
            Principal principal) {
        Optional<FRDomesticStandingOrderPaymentSubmission> isPaymentSubmission = standingOrderPaymentSubmissionRepository.findById(domesticStandingOrderId);
        if (!isPaymentSubmission.isPresent()) {
            return ResponseEntity.status(BAD_REQUEST).body("Payment submission '" + domesticStandingOrderId + "' can't be found");
        }

        FRDomesticStandingOrderPaymentSubmission frPaymentSubmission = isPaymentSubmission.get();
        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frPaymentSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frPaymentSubmission, apiVersion);
        }
        // Get the consent to update the response
        OBWriteDomesticStandingOrderConsentResponse5 obConsent = consentService.getOBIntentObject(
                OBWriteDomesticStandingOrderConsentResponse5.class,
                authorization,
                domesticStandingOrderId
        );
        return ResponseEntity.ok(
                responseEntity(frPaymentSubmission, FRReadRefundAccountFactory.frReadRefundAccount(xReadRefundAccount), obConsent)
        );
    }

    @Override
    public ResponseEntity getDomesticStandingOrdersDomesticStandingOrderIdPaymentDetails(
            String domesticStandingOrderId,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) {
        Optional<FRDomesticStandingOrderPaymentSubmission> isStandingOrderSubmission = standingOrderPaymentSubmissionRepository.findById(domesticStandingOrderId);
        if (!isStandingOrderSubmission.isPresent()) {
            return ResponseEntity.status(BAD_REQUEST).body("Standing order submission '" + domesticStandingOrderId + "' can't be found");
        }

        FRDomesticStandingOrderPaymentSubmission frStandingOrderSubmission = isStandingOrderSubmission.get();
        log.debug("Found The Domestic Standing Order '{}' to get details.", domesticStandingOrderId);
        OBVersion apiVersion = VersionPathExtractor.getVersionFromPath(request);
        if (!ResourceVersionValidator.isAccessToResourceAllowed(apiVersion, frStandingOrderSubmission.getObVersion())) {
            return PaymentApiResponseUtil.resourceConflictResponse(frStandingOrderSubmission, apiVersion);
        }
        return ResponseEntity.ok(responseEntityDetails(frStandingOrderSubmission));
    }

    private OBWriteDomesticStandingOrderResponse5 responseEntity(
            FRDomesticStandingOrderPaymentSubmission frPaymentSubmission,
            FRReadRefundAccount readRefundAccount,
            OBWriteDomesticStandingOrderConsentResponse5 obConsent
    ) {
        FRWriteDataDomesticStandingOrder data = frPaymentSubmission.getStandingOrder().getData();
        Optional<FRResponseDataRefund> refund = FRResponseDataRefundFactory.frDomesticResponseDataRefund(readRefundAccount, data.getInitiation());
        return new OBWriteDomesticStandingOrderResponse5()
                .data(new OBWriteDomesticStandingOrderResponse5Data()
                        .charges(obConsent.getData().getCharges())
                        .domesticStandingOrderId(frPaymentSubmission.getId())
                        .initiation(toOBWriteDomesticStandingOrder3DataInitiation(data.getInitiation()))
                        .creationDateTime(frPaymentSubmission.getCreated())
                        .statusUpdateDateTime(frPaymentSubmission.getUpdated())
                        .status(toOBWriteDomesticStandingOrderResponse5DataStatus(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId())
                        .refund(refund.map(FRResponseDataRefundConverter::toOBWriteDomesticResponse4DataRefund).orElse(null)))
                .links(LinksHelper.createDomesticStandingOrderPaymentLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }

    private OBWritePaymentDetailsResponse1 responseEntityDetails(FRDomesticStandingOrderPaymentSubmission frStandingOrderSubmission) {
        OBWritePaymentDetailsResponse1DataPaymentStatus.StatusEnum status = OBWritePaymentDetailsResponse1DataPaymentStatus.StatusEnum.fromValue(
                PaymentsUtils.statusLinkingMap.get(frStandingOrderSubmission.getStatus().getValue())
        );

        // Build the response object with data to meet the expected data defined by the spec
        OBWritePaymentDetailsResponse1DataStatusDetail.StatusReasonEnum statusReasonEnum = OBWritePaymentDetailsResponse1DataStatusDetail.StatusReasonEnum.PENDINGSETTLEMENT;
        return new OBWritePaymentDetailsResponse1()
                .data(
                        new OBWritePaymentDetailsResponse1Data()
                                .addPaymentStatusItem(
                                        new OBWritePaymentDetailsResponse1DataPaymentStatus()
                                                .status(status)
                                                .paymentTransactionId(UUID.randomUUID().toString())
                                                .statusUpdateDateTime(new DateTime(frStandingOrderSubmission.getUpdated()))
                                                .statusDetail(
                                                        new OBWritePaymentDetailsResponse1DataStatusDetail()
                                                                .status(status.getValue())
                                                                .statusReason(statusReasonEnum)
                                                                .statusReasonDescription(statusReasonEnum.getValue())
                                                )
                                )

                )
                .links(LinksHelper.createDomesticStandingOrderPaymentDetailsLink(this.getClass(), frStandingOrderSubmission.getId()))
                .meta(new Meta());
    }
}
