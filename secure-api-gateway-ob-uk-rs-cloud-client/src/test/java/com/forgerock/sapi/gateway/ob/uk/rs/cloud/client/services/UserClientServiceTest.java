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
package com.forgerock.sapi.gateway.ob.uk.rs.cloud.client.services;

import com.forgerock.sapi.gateway.ob.uk.rs.cloud.client.configuration.ConsentRepoConfiguration;
import com.forgerock.sapi.gateway.ob.uk.rs.cloud.client.exceptions.ErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.cloud.client.exceptions.ExceptionClient;
import com.forgerock.sapi.gateway.ob.uk.rs.cloud.client.model.User;
import com.forgerock.sapi.gateway.ob.uk.rs.cloud.client.utils.url.UrlContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

/**
 * Unit test for {@link UserClientService}
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserClientServiceTest {

    @InjectMocks
    private UserClientService userClientService;

    @Mock
    protected ConsentRepoConfiguration configurationPropertiesClient;

    @Mock
    protected RestTemplate restTemplate;

    protected MockedStatic<UrlContext> urlContextMockedStatic;

    private final static String USER_NAME = "psu4test";
    private final static String ACCOUNT_ACTIVE_STATUS = "active";

    @BeforeEach
    public void setup() {
        urlContextMockedStatic = Mockito.mockStatic(UrlContext.class);
        urlContextMockedStatic.when(
                () -> UrlContext.UrlUserQueryFilter(anyString(), anyString())
        ).thenReturn("http://a.domain/context?_queryFilter=userName+eq+%22" + USER_NAME + "%22");
    }

    @AfterEach
    public void close() {
        urlContextMockedStatic.close();
    }

    @Test
    public void ShouldGetUserDataFromPlatform() throws ExceptionClient {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .userName(USER_NAME)
                .accountStatus(ACCOUNT_ACTIVE_STATUS)
                .build();
        // When
        when(restTemplate.exchange(
                anyString(),
                eq(GET),
                isNull(),
                eq(User.class))
        ).thenReturn(ResponseEntity.ok(user));

        User userResponse = userClientService.getUserByName(user.getUserName());
        // Then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getAccountStatus()).isEqualTo(ACCOUNT_ACTIVE_STATUS);
        assertThat(userResponse.getUserName()).isEqualTo(user.getUserName());
        assertThat(userResponse.getId()).isEqualTo(user.getId());
    }

    @Test
    public void ShouldRaiseNotFound_UserAccountNoActive() throws ExceptionClient {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .userName(USER_NAME)
                .accountStatus("inactive")
                .build();
        // when
        when(restTemplate.exchange(
                anyString(),
                eq(GET),
                isNull(),
                eq(User.class))
        ).thenReturn(ResponseEntity.ok(user));


        ExceptionClient exception = catchThrowableOfType(() ->
                        userClientService.getUserByName(USER_NAME)
                , ExceptionClient.class
        );

        // Then
        assertThat(exception.getErrorClient().getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(exception.getErrorClient().getErrorType().getErrorCode()).isEqualTo(ErrorType.NOT_FOUND.getErrorCode());
        assertThat(exception.getErrorClient().getErrorType().getInternalCode()).isEqualTo(ErrorType.NOT_FOUND.getInternalCode());
    }

    @Test
    public void ShouldRaiseNotFoundUserDataFromPlatform() {
        // When
        when(restTemplate.exchange(
                anyString(),
                eq(GET),
                isNull(),
                eq(User.class))
        ).thenReturn(ResponseEntity.notFound().build());

        ExceptionClient exception = catchThrowableOfType(() ->
                        userClientService.getUserByName(USER_NAME)
                , ExceptionClient.class
        );

        // Then
        assertThat(exception.getErrorClient().getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(exception.getErrorClient().getErrorType().getErrorCode()).isEqualTo(ErrorType.NOT_FOUND.getErrorCode());
        assertThat(exception.getErrorClient().getErrorType().getInternalCode()).isEqualTo(ErrorType.NOT_FOUND.getInternalCode());
    }

    @Test
    public void ShouldRaiseParameterErrorWhenUserNull() {
        // When
        ExceptionClient exception = catchThrowableOfType(() ->
                        userClientService.getUserByName(null)
                , ExceptionClient.class
        );

        // Then
        assertThat(exception.getErrorClient().getErrorType()).isEqualTo(ErrorType.PARAMETER_ERROR);
        assertThat(exception.getErrorClient().getErrorType().getErrorCode()).isEqualTo(ErrorType.PARAMETER_ERROR.getErrorCode());
        assertThat(exception.getErrorClient().getErrorType().getInternalCode()).isEqualTo(ErrorType.PARAMETER_ERROR.getInternalCode());
    }

}