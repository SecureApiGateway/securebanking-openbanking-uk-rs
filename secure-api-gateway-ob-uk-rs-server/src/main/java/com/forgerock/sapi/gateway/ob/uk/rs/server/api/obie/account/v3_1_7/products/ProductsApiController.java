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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_7.products;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1_7.products.ProductsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.AccountDataInternalIdFilter;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.products.FRProductRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.account.consent.AccountResourceAccessService;

import org.springframework.stereotype.Controller;

@Controller("ProductsApiV3.1.7")
public class ProductsApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_6.products.ProductsApiController implements ProductsApi {

    public ProductsApiController(FRProductRepository frProductRepository, AccountDataInternalIdFilter accountDataInternalIdFilter, AccountResourceAccessService accountResourceAccessService) {
        super(frProductRepository, accountDataInternalIdFilter, accountResourceAccessService);
    }
}
