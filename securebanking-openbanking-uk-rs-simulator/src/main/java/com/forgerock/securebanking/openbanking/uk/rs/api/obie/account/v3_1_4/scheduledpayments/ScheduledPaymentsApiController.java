/**
 * Copyright © 2020 ForgeRock AS (obst@forgerock.com)
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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_4.scheduledpayments;

import com.forgerock.securebanking.openbanking.uk.rs.persistence.repository.accounts.scheduledpayments.FRScheduledPaymentRepository;
import com.forgerock.securebanking.openbanking.uk.rs.common.util.AccountDataInternalIdFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller("ScheduledPaymentsApiV3.1.4")
@Slf4j
public class ScheduledPaymentsApiController extends com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_3.scheduledpayments.ScheduledPaymentsApiController implements ScheduledPaymentsApi {

    public ScheduledPaymentsApiController(@Value("${rs.page.default.scheduled-payments.size:10}") int pageLimitSchedulePayments,
                                          FRScheduledPaymentRepository frScheduledPaymentRepository,
                                          AccountDataInternalIdFilter accountDataInternalIdFilter) {
        super(pageLimitSchedulePayments, frScheduledPaymentRepository, accountDataInternalIdFilter);
    }

}
