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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_11.vrp;

import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_11.vrp.DomesticVrpsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.services.RefundAccountService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.simulations.vrp.PeriodicLimitBreachResponseSimulatorService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBDomesticVRPRequestValidator.OBDomesticVRPRequestValidationContext;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.vrp.v3_1_10.DomesticVRPConsentStoreClient;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.payments.DomesticVrpPaymentSubmissionRepository;

@Controller("DomesticVrpsApiV3.1.11")
public class DomesticVrpsApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_10.vrp.DomesticVrpsApiController implements DomesticVrpsApi {
    public DomesticVrpsApiController(
            DomesticVrpPaymentSubmissionRepository paymentSubmissionRepository,
            OBValidationService<OBDomesticVRPRequestValidationContext> paymentRequestValidator,
            DomesticVRPConsentStoreClient consentStoreClient,
            PeriodicLimitBreachResponseSimulatorService limitBreachResponseSimulatorService,
            PaymentSubmissionValidator paymentSubmissionValidator,
            RefundAccountService refundAccountService
    ) {
        super(
                paymentSubmissionRepository,
                paymentRequestValidator,
                consentStoreClient,
                limitBreachResponseSimulatorService,
                paymentSubmissionValidator,
                refundAccountService
        );
    }
}