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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_0.transactions;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRFinancialAccount;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRTransactionData;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.account.FRAccount;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.account.FRTransaction;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.accounts.FRAccountRepository;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.transactions.FRTransactionRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.server.testsupport.api.HttpHeadersTestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import uk.org.openbanking.datamodel.account.OBReadTransaction3;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.testsupport.account.FRFinancialAccountTestDataFactory.aValidFRFinancialAccount;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.testsupport.account.FRTransactionDataTestDataFactory.aValidFRTransactionData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Spring Boot Test for {@link TransactionsApiController}.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class TransactionsApiControllerTest {

    private static final String BASE_URL = "http://localhost:";
    private static final String ACCOUNT_TRANSACTIONS_URI = "/open-banking/v3.0/aisp/accounts/{AccountId}/transactions";
    private static final String TRANSACTIONS_URI = "/open-banking/v3.0/aisp/transactions";

    @LocalServerPort
    private int port;

    @Autowired
    private FRAccountRepository frAccountRepository;

    @Autowired
    private FRTransactionRepository frTransactionRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String accountId;

    @BeforeEach
    public void saveData() {
        FRFinancialAccount financialAccount = aValidFRFinancialAccount();
        FRAccount account = FRAccount.builder()
                .userID("AUserId")
                .account(financialAccount)
                .latestStatementId("5678")
                .build();
        frAccountRepository.save(account);
        accountId = account.getId();

        FRTransactionData transactionData = aValidFRTransactionData(accountId);
        FRTransaction transaction = FRTransaction.builder()
                .accountId(accountId)
                .transaction(transactionData)
                .bookingDateTime(transactionData.getBookingDateTime())
                .build();
        frTransactionRepository.save(transaction);
    }

    @AfterEach
    public void removeData() {
        frAccountRepository.deleteAll();
        frTransactionRepository.deleteAll();
    }

    @Test
    public void shouldGetAccountTransactions() {
        // Given
        String url = accountTransactionsUrl(accountId);

        // When
        ResponseEntity<OBReadTransaction3> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(HttpHeadersTestDataFactory.requiredAccountHttpHeaders(url, accountId)),
                OBReadTransaction3.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OBReadTransaction3 returnedTransaction = response.getBody();
        assertThat(returnedTransaction).isNotNull();
        assertThat(returnedTransaction.getData().getTransaction().get(0).getAccountId()).isEqualTo(accountId);
        assertThat(response.getBody().getLinks().getSelf().toString()).isEqualTo(url);
    }

    @Test
    public void shouldGetTransactions() {
        // Given
        String url = transactionsUrl();

        // When
        ResponseEntity<OBReadTransaction3> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(HttpHeadersTestDataFactory.requiredAccountHttpHeaders(url, accountId)),
                OBReadTransaction3.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OBReadTransaction3 returnedTransaction = response.getBody();
        assertThat(returnedTransaction).isNotNull();
        assertThat(returnedTransaction.getData().getTransaction().get(0).getAccountId()).isEqualTo(accountId);
        assertThat(response.getBody().getLinks().getSelf().toString()).isEqualTo(url);
    }

    private String accountTransactionsUrl(String accountId) {
        String url = BASE_URL + port + ACCOUNT_TRANSACTIONS_URI;
        return url.replace("{AccountId}", accountId);
    }

    private String transactionsUrl() {
        return BASE_URL + port + TRANSACTIONS_URI;
    }
}