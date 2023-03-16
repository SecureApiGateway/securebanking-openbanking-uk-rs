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
/**
 * NOTE: This class is auto generated by the swagger code generator program (2.2.3).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1.transactions;

import com.forgerock.sapi.gateway.ob.uk.common.error.OBErrorResponseException;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.ApiConstants;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.swagger.SwaggerApiTags;
import io.swagger.annotations.*;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code;
import uk.org.openbanking.datamodel.account.OBReadTransaction4;

import java.util.List;

@Api(tags = {"v3.1", SwaggerApiTags.ACCOUNTS_AND_TRANSACTION_TAG})
@RequestMapping(value = "/open-banking/v3.1/aisp")
public interface TransactionsApi {

    @ApiOperation(value = "Get Account Transactions", notes = "Get transactions related to an account",
            response = OBReadTransaction4.class, authorizations = {
            @Authorization(value = "PSUOAuth2Security", scopes = {
                    @AuthorizationScope(scope = "accounts", description = "Ability to get Accounts information")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account Transactions successfully retrieved", response = OBReadTransaction4.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
            @ApiResponse(code = 405, message = "Method Not Allowed", response = Void.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = Void.class),
            @ApiResponse(code = 429, message = "Too Many Requests", response = Void.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Void.class) })


    @RequestMapping(value = "/accounts/{AccountId}/transactions",
            produces = { "application/json; charset=utf-8" },
            method = RequestMethod.GET)
    ResponseEntity<OBReadTransaction4> getAccountTransactions(
            @ApiParam(value = "A unique identifier used to identify the account resource.", required = true)
            @PathVariable("AccountId") String accountId,

            @ApiParam(value = "Page number.", required = false, defaultValue = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @ApiParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true)
            @RequestHeader(value = "Authorization", required = true) String authorization,

            @ApiParam(value = "The UTC ISO 8601 Date Time to filter transactions FROM  NB Time component is optional " +
                    "- set to 00:00:00 for just Date.   The parameter must NOT have a timezone set")
            @RequestParam(value = ApiConstants.ParametersFieldName.FROM_BOOKING_DATE_TIME, required = false) DateTime fromBookingDateTime,

            @ApiParam(value = "The UTC ISO 8601 Date Time to filter transactions TO  NB Time component is optional " +
                    "- set to 00:00:00 for just Date.   The parameter must NOT have a timezone set")
            @RequestParam(value = ApiConstants.ParametersFieldName.TO_BOOKING_DATE_TIME, required = false) DateTime toBookingDateTime,

            @ApiParam(value = "The time when the PSU last logged in with the TPP. " +
                    "All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  " +
                    "Sun, 10 Sep 2017 19:43:31 UTC")
            @RequestHeader(value="x-fapi-customer-last-logged-time", required=false)
            @DateTimeFormat(pattern = ApiConstants.HTTP_DATE_FORMAT) DateTime xFapiCustomerLastLoggedTime,

            @ApiParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.")
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress,

            @ApiParam(value = "An RFC4122 UID used as a correlation id.")
            @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId,

            @ApiParam(value = "Indicates the user-agent that the PSU is using.")
            @RequestHeader(value = "x-customer-user-agent", required = false) String xCustomerUserAgent,

            @RequestHeader(value = "x-ob-first-available-date", required = false)
            @DateTimeFormat(pattern = ApiConstants.BOOKED_TIME_DATE_FORMAT) DateTime firstAvailableDate,

            @RequestHeader(value = "x-ob-last-available-date", required = false)
            @DateTimeFormat(pattern = ApiConstants.BOOKED_TIME_DATE_FORMAT) DateTime lastAvailableDate,

            @ApiParam(value = "The OB permissions")
            @RequestHeader(value = "x-ob-permissions", required = true) List<OBExternalPermissions1Code> permissions,

            @ApiParam(value = "The origin http url")
            @RequestHeader(value = "x-ob-url", required = true) String httpUrl
    ) throws OBErrorResponseException;

    @ApiOperation(value = "Get Transactions", notes = "Get Transactions", response = OBReadTransaction4.class, authorizations = {
        @Authorization(value = "PSUOAuth2Security", scopes = {
            @AuthorizationScope(scope = "accounts", description = "Ability to get Accounts information")
            })
    })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Transactions successfully retrieved", response = OBReadTransaction4.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 405, message = "Method Not Allowed", response = Void.class),
        @ApiResponse(code = 406, message = "Not Acceptable", response = Void.class),
        @ApiResponse(code = 429, message = "Too Many Requests", response = Void.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Void.class) })


    @RequestMapping(value = "/transactions",
        produces = { "application/json; charset=utf-8" }, 
        method = RequestMethod.GET)
    ResponseEntity<OBReadTransaction4> getTransactions(
            @ApiParam(value = "Page number.", required = false, defaultValue = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @ApiParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true)
            @RequestHeader(value = "Authorization", required = true) String authorization,

            @ApiParam(value = "The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are " +
                    "represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC")
            @RequestHeader(value="x-fapi-customer-last-logged-time", required=false)
            @DateTimeFormat(pattern = ApiConstants.HTTP_DATE_FORMAT) DateTime xFapiCustomerLastLoggedTime,

            @ApiParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.")
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress,

            @ApiParam(value = "An RFC4122 UID used as a correlation id.")
            @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId,

            @ApiParam(value = "The UTC ISO 8601 Date Time to filter transactions FROM  NB Time component is optional " +
                    "- set to 00:00:00 for just Date.   The parameter must NOT have a timezone set")
            @RequestParam(value = ApiConstants.ParametersFieldName.FROM_BOOKING_DATE_TIME, required = false) DateTime fromBookingDateTime,

            @ApiParam(value = "The UTC ISO 8601 Date Time to filter transactions TO  NB Time component is optional - " +
                    "set to 00:00:00 for just Date.  The parameter must NOT have a timezone set")
            @RequestParam(value = ApiConstants.ParametersFieldName.TO_BOOKING_DATE_TIME, required = false) DateTime toBookingDateTime,

            @ApiParam(value = "Indicates the user-agent that the PSU is using.")
            @RequestHeader(value = "x-customer-user-agent", required = false) String xCustomerUserAgent,

            @RequestHeader(value = "x-ob-first-available-date", required = false)
            @DateTimeFormat(pattern = ApiConstants.BOOKED_TIME_DATE_FORMAT) DateTime firstAvailableDate,

            @RequestHeader(value = "x-ob-last-available-date", required = false)
            @DateTimeFormat(pattern = ApiConstants.BOOKED_TIME_DATE_FORMAT) DateTime lastAvailableDate,

            @RequestHeader(value = "x-ob-account-ids", required = true) List<String> accountIds,

            @RequestHeader(value = "x-ob-permissions", required = true) List<OBExternalPermissions1Code> permissions,

            @RequestHeader(value = "x-ob-url", required = true) String httpUrl
    ) throws OBErrorResponseException;

    @ApiOperation(value = "Get Statement Transactions", nickname = "getAccountStatementTransactions",
            notes = "Get Statement Transactions related to an account", response = OBReadTransaction4.class, authorizations = {
            @Authorization(value = "PSUOAuth2Security", scopes = {
                    @AuthorizationScope(scope = "accounts", description = "Ability to get Accounts information")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account Transactions successfully retrieved", response = OBReadTransaction4.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 406, message = "Not Acceptable"),
            @ApiResponse(code = 429, message = "Too Many Requests"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    @RequestMapping(value = "/accounts/{AccountId}/statements/{StatementId}/transactions",
            produces = { "application/json; charset=utf-8" },
            method = RequestMethod.GET)
    ResponseEntity<OBReadTransaction4> getAccountStatementTransactions(
            @ApiParam(value = "A unique identifier used to identify the account resource.", required = true)
            @PathVariable("AccountId") String accountId,

            @ApiParam(value = "Page number.", required = false, defaultValue = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @ApiParam(value = "A unique identifier used to identify the statement resource.", required = true)
            @PathVariable("StatementId") String statementId,

            @ApiParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true)
            @RequestHeader(value = "Authorization", required = true) String authorization,

            @ApiParam(value = "The UTC ISO 8601 Date Time to filter transactions FROM  NB Time component is optional " +
                    "- set to 00:00:00 for just Date.   The parameter must NOT have a timezone set")
            @RequestParam(value = ApiConstants.ParametersFieldName.FROM_BOOKING_DATE_TIME, required = false) DateTime fromBookingDateTime,

            @ApiParam(value = "The UTC ISO 8601 Date Time to filter transactions TO  NB Time component is optional " +
                    "- set to 00:00:00 for just Date.   The parameter must NOT have a timezone set")
            @RequestParam(value = ApiConstants.ParametersFieldName.TO_BOOKING_DATE_TIME, required = false) DateTime toBookingDateTime,

            @ApiParam(value = "The time when the PSU last logged in with the TPP.  " +
                    "All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  " +
                    "Sun, 10 Sep 2017 19:43:31 UTC")
            @RequestHeader(value="x-fapi-customer-last-logged-time", required=false)
            @DateTimeFormat(pattern = ApiConstants.HTTP_DATE_FORMAT) DateTime xFapiCustomerLastLoggedTime,

            @ApiParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.")
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress,

            @ApiParam(value = "An RFC4122 UID used as a correlation id.")
            @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId,

            @ApiParam(value = "Indicates the user-agent that the PSU is using.")
            @RequestHeader(value = "x-customer-user-agent", required = false) String xCustomerUserAgent,

            @RequestHeader(value = "x-ob-first-available-date", required = false)
            @DateTimeFormat(pattern = ApiConstants.BOOKED_TIME_DATE_FORMAT) DateTime firstAvailableDate,

            @RequestHeader(value = "x-ob-last-available-date", required = false)
            @DateTimeFormat(pattern = ApiConstants.BOOKED_TIME_DATE_FORMAT) DateTime lastAvailableDate,

            @RequestHeader(value = "x-ob-permissions", required = true) List<OBExternalPermissions1Code> permissions,

            @RequestHeader(value = "x-ob-url", required = true) String httpUrl
    ) throws OBErrorResponseException;


}
