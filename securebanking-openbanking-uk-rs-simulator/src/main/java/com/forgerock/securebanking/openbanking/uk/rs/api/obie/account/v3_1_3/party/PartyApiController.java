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
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_3.party;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code;
import uk.org.openbanking.datamodel.account.OBReadParty2;
import uk.org.openbanking.datamodel.account.OBReadParty3;

import java.util.List;

import static com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.AccountsUtil.DUMMY_FINANCIAL_ID;

@Controller("PartyApiV3.1.3")
@Slf4j
public class PartyApiController implements PartyApi {

    private final com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_2.party.PartyApiController previousVersionController;

    public PartyApiController(@Qualifier("PartyApiV3.1.2") com.forgerock.securebanking.openbanking.uk.rs.api.obie.account.v3_1_2.party.PartyApiController previousVersionController) {
        this.previousVersionController = previousVersionController;
    }

    @Override
    public ResponseEntity<OBReadParty2> getAccountParty(String accountId,
                                                        String authorization,
                                                        DateTime xFapiAuthDate,
                                                        String xFapiCustomerIpAddress,
                                                        String xFapiInteractionId,
                                                        String xCustomerUserAgent,
                                                        List<OBExternalPermissions1Code> permissions,
                                                        String httpUrl) {
        return previousVersionController.getAccountParty(
                accountId,
                DUMMY_FINANCIAL_ID,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                permissions,
                httpUrl);
    }

    @Override
    public ResponseEntity<OBReadParty3> getAccountParties(String accountId,
                                                          String authorization,
                                                          DateTime xFapiAuthDate,
                                                          String xFapiCustomerIpAddress,
                                                          String xFapiInteractionId,
                                                          String xCustomerUserAgent,
                                                          String userId,
                                                          List<OBExternalPermissions1Code> permissions,
                                                          String httpUrl) {
        return previousVersionController.getAccountParties(
                accountId,
                DUMMY_FINANCIAL_ID,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                userId,
                permissions,
                httpUrl);
    }

    @Override
    public ResponseEntity<OBReadParty2> getParty(String authorization,
                                                 DateTime xFapiAuthDate,
                                                 String xFapiCustomerIpAddress,
                                                 String xFapiInteractionId,
                                                 String xCustomerUserAgent,
                                                 String userId,
                                                 List<OBExternalPermissions1Code> permissions,
                                                 String httpUrl) {
        return previousVersionController.getParty(
                DUMMY_FINANCIAL_ID,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                userId,
                permissions,
                httpUrl);
    }
}
