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
package com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.consent;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.ValidationResult;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.BaseOBValidator;

import uk.org.openbanking.datamodel.error.OBError1;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrder3DataInitiation;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrder3DataInitiationFinalPaymentAmount;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrder3DataInitiationFirstPaymentAmount;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrder3DataInitiationRecurringPaymentAmount;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticStandingOrderConsent5;

public class OBWriteDomesticStandingOrderConsent5Validator extends BaseOBValidator<OBWriteDomesticStandingOrderConsent5> {

    private static final String FIRST_PAYMENT_AMOUNT = "firstPaymentAmount";
    private static final String RECURRING_PAYMENT_AMOUNT = "recurringPaymentAmount";
    private static final String FINAL_PAYMENT_AMOUNT = "finalPaymentAmount";
    private final Set<String> validPaymentCurrencies;

    public OBWriteDomesticStandingOrderConsent5Validator(Set<String> validPaymentCurrencies) {
        this.validPaymentCurrencies = Objects.requireNonNull(validPaymentCurrencies, "validPaymentCurrencies must be supplied");
    }

    @Override
    protected void validate(OBWriteDomesticStandingOrderConsent5 consent, ValidationResult<OBError1> validationResult) {
        final OBWriteDomesticStandingOrder3DataInitiation initiation = consent.getData().getInitiation();

        final OBWriteDomesticStandingOrder3DataInitiationFirstPaymentAmount firstPaymentAmount = initiation.getFirstPaymentAmount();
        validateAmount(firstPaymentAmount.getAmount(), firstPaymentAmount.getCurrency(), FIRST_PAYMENT_AMOUNT, validationResult);

        final OBWriteDomesticStandingOrder3DataInitiationRecurringPaymentAmount recurringPaymentAmount = initiation.getRecurringPaymentAmount();
        if (recurringPaymentAmount != null) {
            validateAmount(recurringPaymentAmount.getAmount(), recurringPaymentAmount.getCurrency(), RECURRING_PAYMENT_AMOUNT, validationResult);
        }

        final OBWriteDomesticStandingOrder3DataInitiationFinalPaymentAmount finalPaymentAmount = initiation.getFinalPaymentAmount();
        if (finalPaymentAmount != null) {
            validateAmount(finalPaymentAmount.getAmount(), finalPaymentAmount.getCurrency(), FINAL_PAYMENT_AMOUNT, validationResult);
        }
    }

    private void validateAmount(String amount, String currency, String fieldName, ValidationResult<OBError1> validationResult) {
        if (new BigDecimal(amount).compareTo(ZERO) <= 0) {
            validationResult.addError(OBRIErrorType.DATA_INVALID_REQUEST.toOBError1(
                    String.format("Field: %s - the amount %s provided must be greater than 0", fieldName, amount)));
        }
        if (!validPaymentCurrencies.contains(currency)) {
            validationResult.addError(OBRIErrorType.DATA_INVALID_REQUEST.toOBError1(
                    String.format("Field: %s - the currency %s provided is not supported", fieldName, currency)));
        }
    }
}