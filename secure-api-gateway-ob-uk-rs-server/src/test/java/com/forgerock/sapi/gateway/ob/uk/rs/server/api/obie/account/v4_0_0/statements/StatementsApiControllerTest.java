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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v4_0_0.statements;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRFinancialAccount;
import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRStatementData;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorResponseCategory;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.ApiConstants.ParametersFieldName;
import com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v4_0_0.statements.StatementsApiController;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.account.consent.AccountResourceAccessService;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.statement.StatementPDFService;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.account.v3_1_10.AccountAccessConsent;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.v4.account.FRAccount;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.v4.account.FRStatement;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.v4.accounts.accounts.FRAccountRepository;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.v4.accounts.statements.FRStatementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import uk.org.openbanking.datamodel.v4.account.OBReadStatement2;
import uk.org.openbanking.datamodel.v4.error.OBErrorResponse1;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.testsupport.v4.account.FRFinancialAccountTestDataFactory.aValidFRFinancialAccount;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.testsupport.v4.account.FRStatementDataTestDataFactory.aValidFRStatementData;
import static com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.AccountResourceAccessServiceTestHelpersV4.createAuthorisedConsentAllPermissions;
import static com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.AccountResourceAccessServiceTestHelpersV4.mockAccountResourceAccessServiceResponse;
import static com.forgerock.sapi.gateway.ob.uk.rs.server.testsupport.api.HttpHeadersTestDataFactory.requiredAccountApiHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Spring Boot Test for {@link StatementsApiController}.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class StatementsApiControllerTest {

    private static final String BASE_URL = "http://localhost:";
    private static final String ACCOUNT_STATEMENTS_URI = "/open-banking/v4.0.0/aisp/accounts/{AccountId}/statements";
    private static final String ACCOUNT_STATEMENTS_FILE_URI = "/open-banking/v4.0.0/aisp/accounts/{AccountId}/statements/{StatementId}/file";
    private static final String STATEMENTS_URI = "/open-banking/v4.0.0/aisp/statements";

    @LocalServerPort
    private int port;

    @Autowired
    private FRAccountRepository frAccountRepository;

    @Autowired
    private FRStatementRepository frStatementRepository;

    @Autowired
    private StatementPDFService statementPDFService;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    @Qualifier("v4.0.0DefaultAccountResourceAccessService")
    private AccountResourceAccessService accountResourceAccessService;

    private String accountId;

    private FRStatementData statementData;

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

        statementData = aValidFRStatementData(accountId);
        FRStatement statement = FRStatement.builder()
                .accountId(accountId)
                .statement(statementData)
                .startDateTime(statementData.getStartDateTime())
                .endDateTime(statementData.getEndDateTime())
                .build();
        frStatementRepository.save(statement);
    }

    @AfterEach
    public void removeData() {
        frAccountRepository.deleteAll();
        frStatementRepository.deleteAll();
    }

    @Test
    public void shouldGetAccountStatements() {
        // Given
        String url = accountStatementsUrl(accountId);

        final AccountAccessConsent consent = createAuthorisedConsentAllPermissions(accountId);
        mockAccountResourceAccessServiceResponse(accountResourceAccessService, consent, accountId);

        // When
        ResponseEntity<OBReadStatement2> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(requiredAccountApiHeaders(consent.getId(), consent.getApiClientId())),
                OBReadStatement2.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OBReadStatement2 returnedStatement = response.getBody();
        assertThat(returnedStatement).isNotNull();
        assertThat(returnedStatement.getData().getStatement().get(0).getAccountId()).isEqualTo(accountId);
        assertThat(response.getBody().getLinks().getSelf().toString()).isEqualTo(url);
    }

    @Test
    public void shouldGetAccountStatementsForDateRange() {
        // Given
        String url = accountStatementsUrl(accountId) + "?" + ParametersFieldName.FROM_STATEMENT_DATE_TIME + "=" + LocalDateTime.now().minusDays(60)
                + "&" + ParametersFieldName.TO_STATEMENT_DATE_TIME + "=" + LocalDateTime.now().plusMinutes(1);

        final AccountAccessConsent consent = createAuthorisedConsentAllPermissions(accountId);
        mockAccountResourceAccessServiceResponse(accountResourceAccessService, consent, accountId);

        // When
        ResponseEntity<OBReadStatement2> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(requiredAccountApiHeaders(consent.getId(), consent.getApiClientId())),
                OBReadStatement2.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OBReadStatement2 returnedStatement = response.getBody();
        assertThat(returnedStatement).isNotNull();
        assertThat(returnedStatement.getData().getStatement().get(0).getAccountId()).isEqualTo(accountId);
    }

    @Test
    public void shouldGetStatements() {
        // Given
        String url = statementsUrl();

        final AccountAccessConsent consent = createAuthorisedConsentAllPermissions(accountId);
        mockAccountResourceAccessServiceResponse(accountResourceAccessService, consent);

        // When
        ResponseEntity<OBReadStatement2> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(requiredAccountApiHeaders(consent.getId(), consent.getApiClientId())),
                OBReadStatement2.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        OBReadStatement2 returnedStatement = response.getBody();
        assertThat(returnedStatement).isNotNull();
        assertThat(returnedStatement.getData().getStatement().get(0).getAccountId()).isEqualTo(accountId);
        assertThat(response.getBody().getLinks().getSelf().toString()).isEqualTo(url);
    }

    @Test
    public void shouldGetStatementFile() throws IOException {
        // Given
        String url = accountStatementsFileUrl(accountId, statementData.getStatementId());

        final AccountAccessConsent consent = createAuthorisedConsentAllPermissions(accountId);
        mockAccountResourceAccessServiceResponse(accountResourceAccessService, consent, accountId);

        // When
        ResponseEntity<Resource> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(requiredAccountApiHeaders(consent.getId(), consent.getApiClientId(), MediaType.APPLICATION_PDF)),
                Resource.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Resource returnedStatement = response.getBody();
        assertThat(returnedStatement).isNotNull();
        assertThat(returnedStatement.contentLength()).isGreaterThan(0);

    }

    @Test
    public void shouldGet_badRequest_StatementFile() {
        // Given
        String url = accountStatementsFileUrl(accountId, statementData.getStatementId());

        final AccountAccessConsent consent = createAuthorisedConsentAllPermissions(accountId);
        mockAccountResourceAccessServiceResponse(accountResourceAccessService, consent, accountId);

        // When
        ResponseEntity<OBErrorResponse1> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(requiredAccountApiHeaders(consent.getId(), consent.getApiClientId())),
                OBErrorResponse1.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(OBRIErrorResponseCategory.REQUEST_INVALID.getId());
        assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("Invalid header 'Accept' the only supported value for this operation is 'application/pdf'");
    }

    private String accountStatementsUrl(String accountId) {
        String url = BASE_URL + port + ACCOUNT_STATEMENTS_URI;
        return url.replace("{AccountId}", accountId);
    }

    private String accountStatementsFileUrl(String accountId, String statementId) {
        String url = BASE_URL + port + ACCOUNT_STATEMENTS_FILE_URI;
        return url.replace("{AccountId}", accountId).replace("{StatementId}", statementId);
    }

    private String statementsUrl() {
        return BASE_URL + port + STATEMENTS_URI;
    }
}