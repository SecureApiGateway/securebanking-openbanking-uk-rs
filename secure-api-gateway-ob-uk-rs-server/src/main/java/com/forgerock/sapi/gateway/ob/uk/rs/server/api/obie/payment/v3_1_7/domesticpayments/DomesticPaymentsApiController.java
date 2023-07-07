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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_7.domesticpayments;

import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_7.domesticpayments.DomesticPaymentsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.factories.OBWriteDomesticConsentResponse5Factory;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.accounts.accounts.FRAccountRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.payments.DomesticPaymentSubmissionRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteDomestic2Validator.OBWriteDomestic2ValidationContext;
import com.forgerock.sapi.gateway.rcs.conent.store.client.payment.domestic.v3_1_10.DomesticPaymentConsentStoreClient;

import lombok.extern.slf4j.Slf4j;

@Controller("DomesticPaymentsApiV3.1.7")
@Slf4j
public class DomesticPaymentsApiController
        extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_6.domesticpayments.DomesticPaymentsApiController
        implements DomesticPaymentsApi {

    public DomesticPaymentsApiController(
            DomesticPaymentSubmissionRepository paymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            OBValidationService<OBWriteDomestic2ValidationContext> paymentValidator,
            DomesticPaymentConsentStoreClient consentApiClient,
            OBWriteDomesticConsentResponse5Factory consentResponseFactory,
            FRAccountRepository frAccountRepository) {
        super(paymentSubmissionRepository, paymentSubmissionValidator, paymentValidator, consentApiClient, consentResponseFactory, frAccountRepository);
    }
}
