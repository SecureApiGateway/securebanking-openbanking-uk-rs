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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_2.file;

import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.payments.FilePaymentSubmissionRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.validator.PaymentSubmissionValidator;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.payment.v3_1_2.file.FilePaymentsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller("FilePaymentsApiV3.1.2")
@Slf4j
public class FilePaymentsApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.payment.v3_1_1.file.FilePaymentsApiController implements FilePaymentsApi {

    public FilePaymentsApiController(FilePaymentSubmissionRepository filePaymentSubmissionRepository,
                                     PaymentSubmissionValidator paymentSubmissionValidator) {
        super(filePaymentSubmissionRepository, paymentSubmissionValidator);
    }
}