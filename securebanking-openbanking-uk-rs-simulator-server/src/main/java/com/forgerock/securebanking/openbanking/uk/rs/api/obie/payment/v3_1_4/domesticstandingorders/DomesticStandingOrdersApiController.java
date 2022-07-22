/**
 * Copyright © 2020-2021 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.v3_1_4.domesticstandingorders;

import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.account.FRStandingOrderData;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.common.FRResponseDataRefund;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.common.FRReadRefundAccount;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDataDomesticStandingOrder;
import com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.payment.FRWriteDomesticStandingOrder;
import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion;
import com.forgerock.securebanking.openbanking.uk.error.OBErrorResponseException;
import com.forgerock.securebanking.openbanking.uk.rs.common.util.PaymentStatusUtils;
import com.forgerock.securebanking.openbanking.uk.rs.common.util.VersionPathExtractor;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.document.payment.FRDomesticStandingOrderPaymentSubmission;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.IdempotentRepositoryAdapter;
import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.payments.DomesticStandingOrderPaymentSubmissionRepository;
import com.forgerock.securebanking.openbanking.uk.rs.service.standingorder.StandingOrderService;
import com.forgerock.securebanking.openbanking.uk.rs.validator.PaymentSubmissionValidator;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.common.Meta;
import uk.org.openbanking.datamodel.payment.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.converter.common.FRResponseDataRefundConverter.toOBWriteDomesticResponse4DataRefund;
import static com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.converter.common.FRSubmissionStatusConverter.toOBWriteDomesticStandingOrderResponse5DataStatus;
import static com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.converter.payment.FRWriteDomesticStandingOrderConsentConverter.toOBWriteDomesticStandingOrder3DataInitiation;
import static com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.converter.payment.FRWriteDomesticStandingOrderConverter.toFRWriteDomesticStandingOrder;
import static com.forgerock.securebanking.common.openbanking.uk.forgerock.datamodel.common.FRSubmissionStatus.INITIATIONPENDING;
import static com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.factories.FRStandingOrderDataFactory.createFRStandingOrderData;
import static com.forgerock.securebanking.openbanking.uk.rs.common.util.link.LinksHelper.createDomesticStandingOrderPaymentDetailsLink;
import static com.forgerock.securebanking.openbanking.uk.rs.common.util.link.LinksHelper.createDomesticStandingOrderPaymentLink;
import static com.forgerock.securebanking.openbanking.uk.rs.common.refund.FRReadRefundAccountFactory.frReadRefundAccount;
import static com.forgerock.securebanking.openbanking.uk.rs.common.refund.FRResponseDataRefundFactory.frDomesticResponseDataRefund;
import static com.forgerock.securebanking.openbanking.uk.rs.common.util.PaymentApiResponseUtil.resourceConflictResponse;
import static com.forgerock.securebanking.openbanking.uk.rs.validator.ResourceVersionValidator.isAccessToResourceAllowed;
import static org.springframework.http.HttpStatus.*;

@Controller("DomesticStandingOrdersApiV3.1.4")
@Slf4j
public class DomesticStandingOrdersApiController implements DomesticStandingOrdersApi {

    private final DomesticStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository;
    private final PaymentSubmissionValidator paymentSubmissionValidator;
    private final StandingOrderService standingOrderService;

    public DomesticStandingOrdersApiController(
            DomesticStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            StandingOrderService standingOrderService) {
        this.standingOrderPaymentSubmissionRepository = standingOrderPaymentSubmissionRepository;
        this.paymentSubmissionValidator = paymentSubmissionValidator;
        this.standingOrderService = standingOrderService;
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

        FRWriteDomesticStandingOrder frStandingOrder = toFRWriteDomesticStandingOrder(obWriteDomesticStandingOrder3);
        log.trace("Converted to: '{}'", frStandingOrder);

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
        FRStandingOrderData standingOrderData = createFRStandingOrderData(frStandingOrder, xAccountId);
        standingOrderService.createStandingOrder(standingOrderData);

        return ResponseEntity.status(CREATED).body(responseEntity(frPaymentSubmission, frReadRefundAccount(xReadRefundAccount)));
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
        if (!isAccessToResourceAllowed(apiVersion, frPaymentSubmission.getObVersion())) {
            return resourceConflictResponse(frPaymentSubmission, apiVersion);
        }
        return ResponseEntity.ok(responseEntity(frPaymentSubmission, frReadRefundAccount(xReadRefundAccount)));
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
        if (!isAccessToResourceAllowed(apiVersion, frStandingOrderSubmission.getObVersion())) {
            return resourceConflictResponse(frStandingOrderSubmission, apiVersion);
        }
        return ResponseEntity.ok(responseEntityDetails(frStandingOrderSubmission));
    }

    private OBWriteDomesticStandingOrderResponse5 responseEntity(FRDomesticStandingOrderPaymentSubmission frPaymentSubmission,
                                                                 FRReadRefundAccount readRefundAccount) {
        FRWriteDataDomesticStandingOrder data = frPaymentSubmission.getStandingOrder().getData();
        Optional<FRResponseDataRefund> refund = frDomesticResponseDataRefund(readRefundAccount, data.getInitiation());
        return new OBWriteDomesticStandingOrderResponse5()
                .data(new OBWriteDomesticStandingOrderResponse5Data()
                        .domesticStandingOrderId(frPaymentSubmission.getId())
                        .initiation(toOBWriteDomesticStandingOrder3DataInitiation(data.getInitiation()))
                        .creationDateTime(frPaymentSubmission.getCreated())
                        .statusUpdateDateTime(frPaymentSubmission.getUpdated())
                        .status(toOBWriteDomesticStandingOrderResponse5DataStatus(frPaymentSubmission.getStatus()))
                        .consentId(data.getConsentId())
                        .refund(refund.isPresent() ? toOBWriteDomesticResponse4DataRefund(refund.get()) : null))
                .links(createDomesticStandingOrderPaymentLink(this.getClass(), frPaymentSubmission.getId()))
                .meta(new Meta());
    }

    private OBWritePaymentDetailsResponse1 responseEntityDetails(FRDomesticStandingOrderPaymentSubmission frStandingOrderSubmission) {
        OBWritePaymentDetailsResponse1DataPaymentStatus.StatusEnum status = OBWritePaymentDetailsResponse1DataPaymentStatus.StatusEnum.fromValue(
                PaymentStatusUtils.statusLinkingMap.get(frStandingOrderSubmission.getStatus().getValue())
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
                .links(createDomesticStandingOrderPaymentDetailsLink(this.getClass(), frStandingOrderSubmission.getId()))
                .meta(new Meta());
    }
}
