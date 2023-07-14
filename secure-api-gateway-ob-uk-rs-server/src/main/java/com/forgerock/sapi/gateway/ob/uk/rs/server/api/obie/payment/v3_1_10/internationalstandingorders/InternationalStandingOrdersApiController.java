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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_10.internationalstandingorders;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_10.internationalstandingorders.InternationalStandingOrdersApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.services.ConsentService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.services.validation.RiskValidationService;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.accounts.FRAccountRepository;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.payments.InternationalStandingOrderPaymentSubmissionRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.standingorder.StandingOrderService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import org.springframework.stereotype.Controller;

@Controller("InternationalStandingOrdersApiV3.1.10")
public class InternationalStandingOrdersApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_9.internationalstandingorders.InternationalStandingOrdersApiController implements InternationalStandingOrdersApi {

    public InternationalStandingOrdersApiController(
            InternationalStandingOrderPaymentSubmissionRepository standingOrderPaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            StandingOrderService standingOrderService,
            ConsentService consentService,
            RiskValidationService riskValidationService,
            FRAccountRepository frAccountRepository
    ) {
        super(
                standingOrderPaymentSubmissionRepository,
                paymentSubmissionValidator,
                standingOrderService,
                consentService,
                riskValidationService,
                frAccountRepository
        );
    }
}
