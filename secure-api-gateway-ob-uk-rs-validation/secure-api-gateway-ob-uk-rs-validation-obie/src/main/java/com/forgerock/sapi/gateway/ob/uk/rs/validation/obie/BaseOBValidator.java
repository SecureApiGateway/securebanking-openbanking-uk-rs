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
package com.forgerock.sapi.gateway.ob.uk.rs.validation.obie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;

import com.forgerock.sapi.gateway.ob.uk.rs.validation.ValidationResult;
import uk.org.openbanking.datamodel.error.OBError1;

/**
 * Base implementation of {@link OBValidator}, this class can be extended to provide Open Banking specific validation.
 *
 * Common functionality provided by this class:
 * - converting unexpected RuntimeExceptions to OBError1 objects
 * - logging of failures
 *
 * @param <T>
 */
public abstract class BaseOBValidator<T> implements OBValidator<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final ValidationResult<OBError1> validate(T obj) {
        final ValidationResult<OBError1> validationResult = new ValidationResult<>();
        try {
            validate(obj, validationResult);
        } catch (RuntimeException rte) {
            logger.error("Unexpected exception thrown by validator: {}", getClass(), rte);
            validationResult.addError(OBRIErrorType.SERVER_ERROR.toOBError1("Unexpected error validating request"));
        }
        if (!validationResult.isValid()) {
            logger.debug("Validation failed for object: {}, errors: {}", obj, validationResult.getErrors());
        }
        return validationResult;
    }

    /**
     * Method to apply implementation specific validation rules
     *
     * @param obj T the object to validate
     * @param validationResult the validationResult to add errors to if the implementation detects that the obj param
     *                         does not meet the validation requirements.
     */
    protected abstract void validate(T obj, ValidationResult<OBError1> validationResult);

}
