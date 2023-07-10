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
package com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.ValidationResult;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.BaseOBValidator;

import uk.org.openbanking.datamodel.error.OBError1;
import uk.org.openbanking.datamodel.payment.OBWriteDomestic2DataInitiationInstructedAmount;

public class OBWriteDomestic2DataInitiationInstructedAmountValidator extends BaseOBValidator<OBWriteDomestic2DataInitiationInstructedAmount> {

    private final Set<String> validPaymentCurrencies;

    public OBWriteDomestic2DataInitiationInstructedAmountValidator(Set<String> validPaymentCurrencies) {
        this.validPaymentCurrencies = Objects.requireNonNull(validPaymentCurrencies, "validPaymentCurrencies must be supplied");
    }

    @Override
    protected void validate(OBWriteDomestic2DataInitiationInstructedAmount instructedAmount, ValidationResult<OBError1> validationResult) {
        final String amount = instructedAmount.getAmount();
        if (new BigDecimal(amount).compareTo(ZERO) <= 0) {
            validationResult.addError(OBRIErrorType.DATA_INVALID_REQUEST.toOBError1(
                    String.format("The amount %s provided must be greater than 0", amount)));
        }
        final String currency = instructedAmount.getCurrency();
        if (!validPaymentCurrencies.contains(currency)) {
            validationResult.addError(OBRIErrorType.DATA_INVALID_REQUEST.toOBError1(
                    String.format("The currency %s provided is not supported", currency)));
        }
    }
}
