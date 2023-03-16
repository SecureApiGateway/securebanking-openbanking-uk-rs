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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.backoffice.payment.v3_1_4.internationalscheduledpayments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorResponseCategory;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.backoffice.api.payment.calculate.elements.v3_1_4.internationalscheduledpayments.CalculateResponseElements;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.backoffice.payment.utils.PaymentConsentGeneral;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsent5;
import uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse5;

import javax.servlet.http.HttpServletRequest;

import static uk.org.openbanking.datamodel.payment.OBWriteInternationalScheduledConsentResponse5Data.StatusEnum.AWAITINGAUTHORISATION;


@RestController("CalculateInternationalScheduledPaymentsResponseElements_v3.1.4")
@Slf4j
public class CalculateResponseElementsController implements CalculateResponseElements {


    @Override
    public ResponseEntity<OBWriteInternationalScheduledConsentResponse5> calculateElements(
            OBWriteInternationalScheduledConsent5 body,
            String intent,
            String xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            HttpServletRequest request) throws OBErrorResponseException {
        try {
            OBWriteInternationalScheduledConsentResponse5 response = PaymentConsentGeneral.calculate(
                    body, intent, request
            );
            response.getData().setStatus(AWAITINGAUTHORISATION);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UnsupportedOperationException | JsonProcessingException e) {
            String message = String.format("%s", e.getMessage());
            log.error(message);
            throw badRequestResponseException(message);
        }
    }

    private OBErrorResponseException badRequestResponseException(String message) {
        return new OBErrorResponseException(
                HttpStatus.BAD_REQUEST,
                OBRIErrorResponseCategory.REQUEST_INVALID,
                OBRIErrorType.DATA_INVALID_REQUEST
                        .toOBError1(message)
        );
    }
}
