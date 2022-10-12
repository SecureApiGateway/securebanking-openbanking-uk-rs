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
package com.forgerock.securebanking.openbanking.uk.rs.api.backoffice.payment.calculation;

import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion;
import uk.org.openbanking.datamodel.common.OBActiveOrHistoricCurrencyAndAmount;
import uk.org.openbanking.datamodel.error.OBError1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class PaymentConsentResponseCalculation {

    protected List<OBError1> errors = new ArrayList<>();

    protected static final String DEFAULT_CHARGE_AMOUNT = "1.5";
    protected static final String DEFAULT_CHARGE_CURRENCY = "GBP";

    public static final BigDecimal EXCHANGE_RATE = BigDecimal.valueOf(1.25);


    /**
     * @param version {@link OBVersion} is the api version to identify the response object to be built
     * @return the request consent class by version
     */
    public abstract Class getResponseClass(OBVersion version);


    /**
     * @param consentRequest  consent request object
     * @param consentResponse consent response object
     * @param <T>             dealing generic type
     * @param <R>             dealing generic type
     * @return the consent response object with calculated elements
     */
    public abstract <T, R> R calculate(T consentRequest, R consentResponse);

    /**
     * Get defaults amount of charges
     *
     * @return {@link OBActiveOrHistoricCurrencyAndAmount}
     */
    protected OBActiveOrHistoricCurrencyAndAmount getDefaultAmount() {
        return new OBActiveOrHistoricCurrencyAndAmount().amount(DEFAULT_CHARGE_AMOUNT).currency(DEFAULT_CHARGE_CURRENCY);
    }

    /**
     * Clear error event list
     * @return this
     */
    public PaymentConsentResponseCalculation clearErrors(){
        this.errors.clear();
        return this;
    }

    /**
     * Get the error event list to build the error response
     *
     * @return list of {@link OBError1}
     */
    public List<OBError1> getErrors() {
        return this.errors;
    }
}
