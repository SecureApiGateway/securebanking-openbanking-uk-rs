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

import java.util.Objects;
import java.util.function.Supplier;

import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.ValidationResult;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.BaseOBValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.BasePaymentRequestValidator.PaymentRequestValidationContext;

import uk.org.openbanking.datamodel.common.OBRisk1;
import uk.org.openbanking.datamodel.error.OBError1;

/**
 * Base Validator which provides common validation for Payment Requests.
 *
 * This can be extended to provide specific validation for individual payment types.
 *
 * Common functionality provided:
 * - checking consent risk matches payment request risk
 * - checking consent initiation data matches payment initiation data
 *
 */
public abstract class BasePaymentRequestValidator<C extends PaymentRequestValidationContext<T, I>, T, I> extends BaseOBValidator<C> {

    /**
     * Validation context provides all the data required to validate a payment request.
     *
     * The use of Suppliers enables validation of risk and initiation between the consent and payment request to be
     * handled generically
     *
     * @param <T> type of Payment request obj
     * @param <I> type of Initiation Data obj contained in the Payment request and Consent
     */
    public static class PaymentRequestValidationContext<T, I> {
        /**
         * The payment request to validate
         */
        private final T paymentRequest;

        /**
         * Supplier of the Initiation data from the paymentRequest
         */
        private final Supplier<I> paymentRequestInitiationSupplier;
        /**
         * Supplier of the Risk data from the paymentRequest
         */
        private final Supplier<OBRisk1> paymentRequestRiskSupplier;
        /**
         * Supplier of the Initiation data from the conset
         */
        private final Supplier<I> consentInitiationSupplier;
        /**
         * Supplier of the Risk data from the consent
         */
        private final Supplier<OBRisk1> consentRiskSupplier;

        public PaymentRequestValidationContext(T paymentRequest, Supplier<I> paymentRequestInitiationSupplier, Supplier<OBRisk1> paymentRequestRiskSupplier,
                                              Supplier<I> consentInitiationSupplier, Supplier<OBRisk1> consentRiskSupplier) {

            this.paymentRequest = Objects.requireNonNull(paymentRequest, "paymentRequest must be supplied");
            this.paymentRequestInitiationSupplier = Objects.requireNonNull(paymentRequestInitiationSupplier, "paymentRequestInitiationSupplier must be supplied");
            this.paymentRequestRiskSupplier = Objects.requireNonNull(paymentRequestRiskSupplier, "paymentRequestRiskSupplier must be supplied");
            this.consentInitiationSupplier = Objects.requireNonNull(consentInitiationSupplier, "consentInitiationSupplier must be supplied");
            this.consentRiskSupplier = Objects.requireNonNull(consentRiskSupplier, "consentRiskSupplier must be supplied");
        }

        public I getPaymentRequestInitiation() {
            return paymentRequestInitiationSupplier.get();
        }

        public I getConsentInitiation() {
            return consentInitiationSupplier.get();
        }

        public OBRisk1 getConsentRisk() {
            return consentRiskSupplier.get();
        }

        public OBRisk1 getPaymentRequestRisk() {
            return paymentRequestRiskSupplier.get();
        }

        public T getPaymentRequest() {
            return paymentRequest;
        }
    }

    @Override
    protected void validate(C paymentReqValidationCtxt, ValidationResult<OBError1> validationResult) {
        validateRiskMatchesConsent(paymentReqValidationCtxt, validationResult);
        validateInitiationMatchesConsent(paymentReqValidationCtxt, validationResult);

        doPaymentSpecificValidation(paymentReqValidationCtxt, validationResult);
    }

    private void validateRiskMatchesConsent(C paymentReqValidationCtxt, ValidationResult<OBError1> validationResult) {
        if (!Objects.equals(paymentReqValidationCtxt.getConsentRisk(), paymentReqValidationCtxt.getPaymentRequestRisk())) {
            validationResult.addError(OBRIErrorType.PAYMENT_INVALID_RISK.toOBError1("")); // FIXME need to an arg otherwise get an exception, fix PAYMENT_INVALID_RISK format
        }
    }

    private void validateInitiationMatchesConsent(C paymentReqValidationCtxt, ValidationResult<OBError1> validationResult) {
        if (!Objects.equals(paymentReqValidationCtxt.getConsentInitiation(), paymentReqValidationCtxt.getPaymentRequestInitiation())) {
            validationResult.addError(OBRIErrorType.PAYMENT_INVALID_INITIATION.toOBError1("")); // FIXME need to an arg otherwise get an exception, fix PAYMENT_INVALID_INITIATION format
        }
    }

    protected abstract void doPaymentSpecificValidation(C paymentReqValidationCtxt, ValidationResult<OBError1> validationResult);

}
