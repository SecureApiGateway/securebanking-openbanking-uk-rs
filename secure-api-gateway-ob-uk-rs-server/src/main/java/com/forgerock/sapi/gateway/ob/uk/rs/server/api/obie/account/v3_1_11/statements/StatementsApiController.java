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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_11.statements;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1_11.statements.StatementsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.AccountDataInternalIdFilter;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.account.consent.AccountResourceAccessService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.statement.StatementPDFService;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.statements.FRStatementRepository;

@Controller("StatementsApiV3.1.11")
public class StatementsApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_10.statements.StatementsApiController implements StatementsApi {
    public StatementsApiController(@Value("${rs.page.default.statement.size:10}") int pageLimitStatements,
            FRStatementRepository frStatementRepository,
            AccountDataInternalIdFilter accountDataInternalIdFilter,
            StatementPDFService statementPDFService,
            AccountResourceAccessService accountResourceAccessService) {
        super(pageLimitStatements, frStatementRepository, accountDataInternalIdFilter, statementPDFService, accountResourceAccessService);
    }
}