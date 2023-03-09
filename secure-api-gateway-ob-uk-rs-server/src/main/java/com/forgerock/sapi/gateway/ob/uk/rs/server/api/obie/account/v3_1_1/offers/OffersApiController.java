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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_1.offers;

import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.AccountDataInternalIdFilter;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.accounts.offers.FROfferRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1_1.offers.OffersApi;
import org.springframework.stereotype.Controller;

@Controller("OffersApiV3.1.1")
public class OffersApiController extends com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1.offers.OffersApiController implements OffersApi {

    public OffersApiController(FROfferRepository frOfferRepository,
                               AccountDataInternalIdFilter accountDataInternalIdFilter) {
        super(frOfferRepository, accountDataInternalIdFilter);
    }
}