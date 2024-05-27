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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_11.file;

import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_11.file.FilePaymentsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.payment.OBWriteFile2Validator.OBWriteFile2ValidationContext;
import com.forgerock.sapi.gateway.rcs.consent.store.client.payment.file.v3_1_10.FilePaymentConsentStoreClient;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.payments.FilePaymentSubmissionRepository;

@Controller("FilePaymentsApiV3.1.11")
public class FilePaymentsApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_10.file.FilePaymentsApiController implements FilePaymentsApi {
    public FilePaymentsApiController(
            FilePaymentSubmissionRepository filePaymentSubmissionRepository,
            PaymentSubmissionValidator paymentSubmissionValidator,
            FilePaymentConsentStoreClient consentStoreClient,
            OBValidationService<OBWriteFile2ValidationContext> filePaymentRequestValidator
    ) {
        super(filePaymentSubmissionRepository, paymentSubmissionValidator, consentStoreClient, filePaymentRequestValidator);
    }
}
