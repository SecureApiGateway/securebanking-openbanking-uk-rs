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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.funds.v3_1_3;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.funds.v3_1_3.FundsConfirmationsApi;

import uk.org.openbanking.datamodel.fund.OBFundsConfirmation1;

@Controller("FundsConfirmationsApiV3.1.3")
public class FundsConfirmationsApiController implements FundsConfirmationsApi {

    private final com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.funds.v3_1_2.FundsConfirmationsApiController previousVersionController;

    public FundsConfirmationsApiController(@Qualifier("FundsConfirmationsApiV3.1.2") com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.funds.v3_1_2.FundsConfirmationsApiController previousVersionController) {
        this.previousVersionController = previousVersionController;
    }

    @Override
    public ResponseEntity createFundsConfirmations(
            @Valid OBFundsConfirmation1 obFundsConfirmation1,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            String apiClientId,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        return previousVersionController.createFundsConfirmation(
                obFundsConfirmation1,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                apiClientId,
                request,
                principal);
    }

    @Override
    public ResponseEntity getFundsConfirmationId(
            String fundsConfirmationId,
            String authorization,
            DateTime xFapiAuthDate,
            String xFapiCustomerIpAddress,
            String xFapiInteractionId,
            String xCustomerUserAgent,
            HttpServletRequest request,
            Principal principal
    ) throws OBErrorResponseException {
        return previousVersionController.getFundsConfirmationId(
                fundsConfirmationId,
                authorization,
                xFapiAuthDate,
                xFapiCustomerIpAddress,
                xFapiInteractionId,
                xCustomerUserAgent,
                request,
                principal);
    }
}
