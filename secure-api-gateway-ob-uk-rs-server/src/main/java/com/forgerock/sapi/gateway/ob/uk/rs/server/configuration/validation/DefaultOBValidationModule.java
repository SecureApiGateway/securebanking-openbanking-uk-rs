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
package com.forgerock.sapi.gateway.ob.uk.rs.server.configuration.validation;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.forgerock.sapi.gateway.ob.uk.rs.server.common.Currencies;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.BaseOBValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBDomesticVRPRequestValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBDomesticVRPRequestValidator.OBDomesticVRPRequestValidationContext;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteDomestic2DataInitiationInstructedAmountValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteDomestic2Validator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteDomestic2Validator.OBWriteDomestic2ValidationContext;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteDomesticScheduled2Validator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteDomesticScheduled2Validator.OBWriteDomesticScheduled2ValidationContext;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.consent.OBDomesticVRPConsentRequestValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.consent.OBWriteDomesticConsent4Validator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.consent.OBWriteDomesticScheduledConsent4Validator;

import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiationInstructedAmount;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsent4;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticScheduledConsent4;
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPConsentRequest;

/**
 * Spring Boot configuration for the Open Banking validation module with the default set of rules.
 *
 * Creates Beans that can be used by services and controllers in this simulator for validation purposes.
 *
 * This can be enabled by setting the following property:
 * rs.obie.validation.module: default
 *
 * Different validation rules may be implemented by creating a new module that creates the same set of beans, such a
 * module should include the @ConditionalOnProperty annotation as below but with a different havingValue.
 */
@Configuration
@ConditionalOnProperty(prefix = "rs.obie.validation", name = "module", havingValue = "default")
public class DefaultOBValidationModule {

    /**
     * Bean to validate OBWriteDomestic2 objects (Domestic Payment Requests)
     */
    @Bean
    public OBValidationService<OBWriteDomestic2ValidationContext> domesticPaymentValidator() {
        return new OBValidationService<>(new OBWriteDomestic2Validator());
    }

    @Bean
    public OBValidationService<OBWriteDomesticConsent4> domesticPaymentConsentValidator(BaseOBValidator<OBWriteDomestic2DataInitiationInstructedAmount> instructedAmountValidator) {
        return new OBValidationService<>(new OBWriteDomesticConsent4Validator(instructedAmountValidator));
    }

    @Bean
    public BaseOBValidator<OBWriteDomestic2DataInitiationInstructedAmount> instructedAmountValidator() {
        return new OBWriteDomestic2DataInitiationInstructedAmountValidator(Arrays.stream(Currencies.values()).map(Currencies::getCode).collect(Collectors.toSet()));
    }

    @Bean
    public OBValidationService<OBWriteDomesticScheduledConsent4> domesticScheduledPaymentConsentValidator(BaseOBValidator<OBWriteDomestic2DataInitiationInstructedAmount> instructedAmountValidator) {
        return new OBValidationService<>(new OBWriteDomesticScheduledConsent4Validator(instructedAmountValidator));
    }

    @Bean
    public OBValidationService<OBWriteDomesticScheduled2ValidationContext> domesticScheduledPaymentValidator() {
        return new OBValidationService<>(new OBWriteDomesticScheduled2Validator());
    }

    @Bean
    public OBValidationService<OBDomesticVRPConsentRequest> domesticVRPConsentValidator() {
        return new OBValidationService<>(new OBDomesticVRPConsentRequestValidator());
    }

    @Bean
    public OBValidationService<OBDomesticVRPRequestValidationContext> domesticVRPPaymentRequestValidator() {
        return new OBValidationService<>(new OBDomesticVRPRequestValidator());
    }
}
