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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.account.FRReadConsentConverter;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1_10.accounts.AccountAccessConsentsApi;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.account.v3_1_10.AccountAccessConsent;

import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code;
import uk.org.openbanking.datamodel.account.OBReadConsent1;
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1;
import uk.org.openbanking.datamodel.account.OBReadConsentResponse1Data;
import uk.org.openbanking.datamodel.account.OBReadData1;
import uk.org.openbanking.datamodel.account.OBRisk2;
import uk.org.openbanking.datamodel.common.OBExternalRequestStatus1Code;

class OBReadConsentResponseFactoryTest {

    private final OBReadConsentResponseFactory responseFactory = new OBReadConsentResponseFactory();

    @Test
    void buildConsentResponse() {
        final AccountAccessConsent consent = new AccountAccessConsent();
        final String consentId = "CONSENT-1223434";
        final String apiClientId = "test-client-1";
        final DateTime creationDateTime = DateTime.now();
        consent.setId(consentId);
        consent.setStatus("AwaitingAuthorisation");
        consent.setApiClientId(apiClientId);
        consent.setCreationDateTime(creationDateTime);
        consent.setStatusUpdateDateTime(creationDateTime);
        final OBReadConsent1 consentRequest = new OBReadConsent1().data(
                        new OBReadData1().permissions(List.of(OBExternalPermissions1Code.READACCOUNTSBASIC, OBExternalPermissions1Code.READBALANCES))
                                .transactionToDateTime(DateTime.now())
                                .transactionFromDateTime(DateTime.now().minusDays(30))
                                .expirationDateTime(DateTime.now().plusDays(90)))
                        .risk(new OBRisk2());
        consent.setRequestObj(FRReadConsentConverter.toFRReadConsent(consentRequest));
        final OBReadConsentResponse1 obReadConsentResponse1 = responseFactory.buildConsentResponse(consent, AccountAccessConsentsApi.class);
        final OBReadConsentResponse1Data response1Data = obReadConsentResponse1.getData();
        assertThat(response1Data.getConsentId()).isEqualTo(consentId);
        assertThat(response1Data.getStatus()).isEqualTo(OBExternalRequestStatus1Code.AWAITINGAUTHORISATION);
        assertThat(response1Data.getPermissions()).isEqualTo(consentRequest.getData().getPermissions());
        assertThat(response1Data.getCreationDateTime()).isEqualTo(creationDateTime);
        assertThat(response1Data.getStatusUpdateDateTime()).isEqualTo(creationDateTime);
        assertThat(response1Data.getExpirationDateTime()).isEqualTo(consentRequest.getData().getExpirationDateTime());
        assertThat(response1Data.getTransactionFromDateTime()).isEqualTo(consentRequest.getData().getTransactionFromDateTime());
        assertThat(response1Data.getTransactionFromDateTime()).isEqualTo(consentRequest.getData().getTransactionFromDateTime());
        assertThat(obReadConsentResponse1.getMeta()).isNotNull();
        assertThat(obReadConsentResponse1.getLinks().getSelf().toString()).endsWith("/open-banking/v3.1.10/aisp/account-access-consents/" + consentId);
    }
}