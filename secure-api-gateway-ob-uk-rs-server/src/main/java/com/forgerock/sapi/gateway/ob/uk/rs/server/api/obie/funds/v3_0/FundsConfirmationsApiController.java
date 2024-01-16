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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.funds.v3_0;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.common.FRAmountConverter.toOBActiveOrHistoricCurrencyAndAmount;
import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.funds.FRFundsConfirmationConverter.toFRFundsConfirmationData;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.funds.FRFundsConfirmationData;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorResponseCategory;
import com.forgerock.sapi.gateway.ob.uk.common.error.OBRIErrorType;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.funds.v3_0.FundsConfirmationsApi;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.VersionPathExtractor;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.link.LinksHelper;
import com.forgerock.sapi.gateway.ob.uk.rs.server.service.balance.FundsAvailabilityService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.OBValidationService;
import com.forgerock.sapi.gateway.ob.uk.rs.validation.obie.funds.FundsConfirmationValidator.FundsConfirmationValidationContext;
import com.forgerock.sapi.gateway.rcs.consent.store.client.funds.v3_1_10.FundsConfirmationConsentStoreClient;
import com.forgerock.sapi.gateway.rcs.consent.store.datamodel.funds.v3_1_10.FundsConfirmationConsent;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.account.FRAccount;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.funds.FRFundsConfirmation;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.accounts.FRAccountRepository;
import com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.funds.FundsConfirmationRepository;

import lombok.extern.slf4j.Slf4j;
import uk.org.openbanking.datamodel.common.Meta;
import uk.org.openbanking.datamodel.fund.OBFundsConfirmation1;
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationDataResponse1;
import uk.org.openbanking.datamodel.fund.OBFundsConfirmationResponse1;

@Controller("FundsConfirmationsApiV3.0")
@Slf4j
public class FundsConfirmationsApiController implements FundsConfirmationsApi {

    private final FundsConfirmationRepository fundsConfirmationRepository;
    private final FundsAvailabilityService fundsAvailabilityService;
    private final FRAccountRepository accountRepository;
    private final OBValidationService<FundsConfirmationValidationContext> fundsConfirmationValidator;
    private final FundsConfirmationConsentStoreClient consentStoreClient;

    public FundsConfirmationsApiController(
            FundsConfirmationRepository fundsConfirmationRepository,
            FundsAvailabilityService fundsAvailabilityService,
            FRAccountRepository accountRepository,
            OBValidationService<FundsConfirmationValidationContext> fundsConfirmationValidator,
            FundsConfirmationConsentStoreClient consentStoreClient
    ) {
        this.fundsConfirmationRepository = fundsConfirmationRepository;
        this.fundsAvailabilityService = fundsAvailabilityService;
        this.accountRepository = accountRepository;
        this.fundsConfirmationValidator = fundsConfirmationValidator;
        this.consentStoreClient = consentStoreClient;
    }

    @Override
    public ResponseEntity<OBFundsConfirmationResponse1> createFundsConfirmation(
            @Valid OBFundsConfirmation1 obFundsConfirmation1,
            String authorization,
            DateTime xFapiCustomerLastLoggedTime,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        log.debug("Create funds confirmation: {}", obFundsConfirmation1);

        String consentId = obFundsConfirmation1.getData().getConsentId();
        log.debug("Attempting to get consent: {}, clientId: {}", consentId, apiClientId);
        final FundsConfirmationConsent consent = consentStoreClient.getConsent(consentId, apiClientId);
        log.debug("Got consent from store: {}", consent);

        Optional<FRFundsConfirmation> isSubmission = fundsConfirmationRepository.findById(consentId);
        FRFundsConfirmation frFundsConfirmation = isSubmission
                .orElseGet(() ->
                        FRFundsConfirmation.builder()
                                .id(consentId)
                                .created(DateTime.now())
                                .obVersion(VersionPathExtractor.getVersionFromPath(request))
                                .build()
                );

        // validate funds confirmation
        log.debug("Validating funds confirmation");
        FRAccount account = accountRepository.byAccountId(consent.getAuthorisedDebtorAccountId());
        if (account == null) {
            log.warn("Funds confirmation verification failed, Account with ID {} not found", consent.getAuthorisedDebtorAccountId());
            throw new OBErrorResponseException(
                    OBRIErrorType.FUNDS_CONFIRMATION_DEBTOR_ACCOUNT_NOT_FOUND.getHttpStatus(),
                    OBRIErrorResponseCategory.SERVER_INTERNAL_ERROR,
                    OBRIErrorType.FUNDS_CONFIRMATION_DEBTOR_ACCOUNT_NOT_FOUND.toOBError1(consent.getAuthorisedDebtorAccountId())
            );
        }
        final FundsConfirmationValidationContext validationContext = new FundsConfirmationValidationContext(
                obFundsConfirmation1,
                consent.getRequestObj().getData().getExpirationDateTime(),
                consent.getStatus(),
                account.getAccount().getCurrency()
        );

        fundsConfirmationValidator.validate(validationContext);
        log.debug("Funds Confirmation validation successful");

        // Check if funds are available on the account selected in consent
        boolean areFundsAvailable = fundsAvailabilityService.isFundsAvailable(
                consent.getAuthorisedDebtorAccountId(),
                obFundsConfirmation1.getData().getInstructedAmount().getAmount());
        frFundsConfirmation.setFundsAvailable(areFundsAvailable);
        frFundsConfirmation.setFundsConfirmation(toFRFundsConfirmationData(obFundsConfirmation1));
        frFundsConfirmation = fundsConfirmationRepository.save(frFundsConfirmation);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(packageResponse(frFundsConfirmation, request));
    }

    private OBFundsConfirmationResponse1 packageResponse(FRFundsConfirmation fundsConfirmation, HttpServletRequest request) {
        final FRFundsConfirmationData obFundsConfirmationData = fundsConfirmation.getFundsConfirmation();
        return new OBFundsConfirmationResponse1()
                .data(new OBFundsConfirmationDataResponse1()
                        .instructedAmount(toOBActiveOrHistoricCurrencyAndAmount(obFundsConfirmationData.getInstructedAmount()))
                        .creationDateTime(fundsConfirmation.getCreated())
                        .fundsConfirmationId(fundsConfirmation.getId())
                        .fundsAvailable(fundsConfirmation.isFundsAvailable())
                        .reference(obFundsConfirmationData.getReference())
                        .consentId(fundsConfirmation.getFundsConfirmation().getConsentId()))
                .meta(new Meta())
                .links(LinksHelper.createFundsConfirmationSelfLink(this.getClass(), fundsConfirmation.getId()));
    }

}
