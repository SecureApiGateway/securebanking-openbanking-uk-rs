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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_10.transactions;

import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.AccountDataInternalIdFilter;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.transactions.FRTransactionRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1_10.transactions.TransactionsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.account.consent.AccountResourceAccessService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller("TransactionsApiV3.1.10")
public class TransactionsApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_9.transactions.TransactionsApiController implements TransactionsApi {

    public TransactionsApiController(@Value("${rs.page.default.transaction.size:120}") int pageLimitTransactions,
                                     FRTransactionRepository FRTransactionRepository,
                                     AccountDataInternalIdFilter accountDataInternalIdFilter,
                                     AccountResourceAccessService accountResourceAccessService) {
        super(pageLimitTransactions, FRTransactionRepository, accountDataInternalIdFilter, accountResourceAccessService);
    }
}
