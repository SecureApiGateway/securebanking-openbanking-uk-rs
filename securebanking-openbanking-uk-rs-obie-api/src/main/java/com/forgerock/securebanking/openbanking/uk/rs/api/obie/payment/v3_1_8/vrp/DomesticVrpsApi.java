/**
 * Copyright © 2020-2021 ForgeRock AS (obst@forgerock.com)
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
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.1.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.forgerock.securebanking.openbanking.uk.rs.api.obie.payment.v3_1_8.vrp;

import com.forgerock.securebanking.openbanking.uk.error.OBErrorResponseException;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.org.openbanking.datamodel.error.OBErrorResponse1;
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPDetails;
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPRequest;
import uk.org.openbanking.datamodel.vrp.OBDomesticVRPResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-11-17T13:54:56.728Z[Europe/London]")
@Validated
@Api(value = "domestic-vrps", description = "the domestic-vrps API")
@RequestMapping(value = "/open-banking/v3.1.8/pisp")
public interface DomesticVrpsApi {

    /**
     * GET /domestic-vrps/{DomesticVRPId} : Retrieve a domestic VRP
     * Retrieve a domestic VRP
     *
     * @param domesticVRPId DomesticVRPId (required)
     * @param authorization An Authorisation Token as per https://tools.ietf.org/html/rfc6750 (required)
     * @param xFapiAuthDate The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC (optional)
     * @param xFapiCustomerIpAddress The PSU&#39;s IP address if the PSU is currently logged in with the TPP. (optional)
     * @param xFapiInteractionId An RFC4122 UID used as a correlation id. (optional)
     * @param xCustomerUserAgent Indicates the user-agent that the PSU is using. (optional)
     * @param xReadRefundAccount Indicates if 'ReadRefundAccount' was set to 'Yes' or 'No' in the consent. Defaults to 'No'
     * @return Default response (status code 200)
     *         or Bad request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Method Not Allowed (status code 405)
     *         or Not Acceptable (status code 406)
     *         or Unsupported Media Type (status code 415)
     *         or Too Many Requests (status code 429)
     *         or Internal Server Error (status code 500)
     */
    @ApiOperation(value = "Retrieve a domestic VRP", nickname = "domesticVrpGet", notes = "Retrieve a domestic VRP",
            response = OBDomesticVRPResponse.class, authorizations = {
            @Authorization(value = "TPPOAuth2Security", scopes = {
                    @AuthorizationScope(scope = "payments", description = "Generic payment scope")})
    }, tags = {"Domestic VRPs",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Default response", response = OBDomesticVRPResponse.class),
            @ApiResponse(code = 400, message = "Bad request", response = OBErrorResponse1.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden", response = OBErrorResponse1.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 406, message = "Not Acceptable"),
            @ApiResponse(code = 415, message = "Unsupported Media Type"),
            @ApiResponse(code = 429, message = "Too Many Requests"),
            @ApiResponse(code = 500, message = "Internal Server Error", response = OBErrorResponse1.class)})

    @RequestMapping(
            value = "/domestic-vrps/{DomesticVRPId}",
            produces = {"application/json; charset=utf-8", "application/json", "application/jose+jwe"},
            method = RequestMethod.GET
    )
    ResponseEntity<OBDomesticVRPResponse> domesticVrpGet(
            @ApiParam(value = "DomesticVRPId", required = true)
            @PathVariable("DomesticVRPId") String domesticVRPId,

            @ApiParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true)
            @RequestHeader(value = "Authorization", required = true) String authorization,

            @ApiParam(value = "The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC")
            @RequestHeader(value = "x-fapi-auth-date", required = false) String xFapiAuthDate,

            @ApiParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.")
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress,

            @ApiParam(value = "An RFC4122 UID used as a correlation id.")
            @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId,

            @ApiParam(value = "Indicates the user-agent that the PSU is using.")
            @RequestHeader(value = "x-customer-user-agent", required = false) String xCustomerUserAgent,

            @ApiParam(value = "Indicates if 'ReadRefundAccount' was set to 'Yes' or 'No' in the consent. Defaults to 'No'", required = false)
            @RequestHeader(value = "x-read-refund-account", required = false) String xReadRefundAccount,

            HttpServletRequest request,

            Principal principal
    ) throws OBErrorResponseException;


    /**
     * GET /domestic-vrps/{DomesticVRPId}/payment-details : Retrieve a domestic VRP
     * Retrieve a domestic VRP
     *
     * @param domesticVRPId DomesticVRPId (required)
     * @param authorization An Authorisation Token as per https://tools.ietf.org/html/rfc6750 (required)
     * @param xFapiAuthDate The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC (optional)
     * @param xFapiCustomerIpAddress The PSU&#39;s IP address if the PSU is currently logged in with the TPP. (optional)
     * @param xFapiInteractionId An RFC4122 UID used as a correlation id. (optional)
     * @param xCustomerUserAgent Indicates the user-agent that the PSU is using. (optional)
     * @param xReadRefundAccount Indicates if 'ReadRefundAccount' was set to 'Yes' or 'No' in the consent. Defaults to 'No'
     * @return Default response (status code 200)
     *         or Bad request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Method Not Allowed (status code 405)
     *         or Not Acceptable (status code 406)
     *         or Unsupported Media Type (status code 415)
     *         or Too Many Requests (status code 429)
     *         or Internal Server Error (status code 500)
     */
    @ApiOperation(value = "Retrieve a domestic VRP", nickname = "domesticVrpPaymentDetailsGet", notes = "Retrieve a domestic VRP", response = OBDomesticVRPDetails.class, authorizations = {
            @Authorization(value = "TPPOAuth2Security", scopes = {
                    @AuthorizationScope(scope = "payments", description = "Generic payment scope")})
    }, tags = {"Domestic VRPs",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Default response", response = OBDomesticVRPDetails.class),
            @ApiResponse(code = 400, message = "Bad request", response = OBErrorResponse1.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden", response = OBErrorResponse1.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 406, message = "Not Acceptable"),
            @ApiResponse(code = 415, message = "Unsupported Media Type"),
            @ApiResponse(code = 429, message = "Too Many Requests"),
            @ApiResponse(code = 500, message = "Internal Server Error", response = OBErrorResponse1.class)})

    @RequestMapping(
            value = "/domestic-vrps/{DomesticVRPId}/payment-details",
            produces = {"application/json; charset=utf-8", "application/json", "application/jose+jwe"},
            method = RequestMethod.GET
    )
    ResponseEntity<OBDomesticVRPDetails> domesticVrpPaymentDetailsGet(
            @ApiParam(value = "DomesticVRPId", required = true)
            @PathVariable("DomesticVRPId") String domesticVRPId,

            @ApiParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true)
            @RequestHeader(value = "Authorization", required = true) String authorization,

            @ApiParam(value = "The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC")
            @RequestHeader(value = "x-fapi-auth-date", required = false) String xFapiAuthDate,

            @ApiParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.")
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress,

            @ApiParam(value = "An RFC4122 UID used as a correlation id.")
            @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId,

            @ApiParam(value = "Indicates the user-agent that the PSU is using.")
            @RequestHeader(value = "x-customer-user-agent", required = false) String xCustomerUserAgent,

            @ApiParam(value = "Indicates if 'ReadRefundAccount' was set to 'Yes' or 'No' in the consent. Defaults to 'No'", required = false)
            @RequestHeader(value = "x-read-refund-account", required = false) String xReadRefundAccount,

            HttpServletRequest request,

            Principal principal
    ) throws OBErrorResponseException;


    /**
     * POST /domestic-vrps : Create a domestic VRP
     * Create a domestic VRP
     *
     * @param authorization An Authorisation Token as per https://tools.ietf.org/html/rfc6750 (required)
     * @param xJwsSignature A detached JWS signature of the body of the payload. (required)
     * @param obDomesticVRPRequest Default (required)
     * @param xFapiAuthDate The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are
     *                      represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC (optional)
     * @param xFapiCustomerIpAddress The PSU&#39;s IP address if the PSU is currently logged in with the TPP. (optional)
     * @param xFapiInteractionId An RFC4122 UID used as a correlation id. (optional)
     * @param xCustomerUserAgent Indicates the user-agent that the PSU is using. (optional)
     * @param xReadRefundAccount Indicates if 'ReadRefundAccount' was set to 'Yes' or 'No' in the consent. Defaults to 'No'
     * @return Default response (status code 201)
     *         or Bad request (status code 400)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Method Not Allowed (status code 405)
     *         or Not Acceptable (status code 406)
     *         or Unsupported Media Type (status code 415)
     *         or Too Many Requests (status code 429)
     *         or Internal Server Error (status code 500)
     */
    @ApiOperation(value = "Create a domestic VRP", nickname = "domesticVrpPost", notes = "Create a domestic VRP",
            response = OBDomesticVRPResponse.class, authorizations = {
            @Authorization(value = "TPPOAuth2Security", scopes = {
                    @AuthorizationScope(scope = "payments", description = "Generic payment scope")})
    }, tags = {"Domestic VRPs",})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Default response", response = OBDomesticVRPResponse.class),
            @ApiResponse(code = 400, message = "Bad request", response = OBErrorResponse1.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden", response = OBErrorResponse1.class),
            @ApiResponse(code = 405, message = "Method Not Allowed"),
            @ApiResponse(code = 406, message = "Not Acceptable"),
            @ApiResponse(code = 415, message = "Unsupported Media Type"),
            @ApiResponse(code = 429, message = "Too Many Requests"),
            @ApiResponse(code = 500, message = "Internal Server Error", response = OBErrorResponse1.class)})

    @RequestMapping(
            value = "/domestic-vrps",
            produces = {"application/json; charset=utf-8", "application/json", "application/jose+jwe"},
            consumes = {"application/json; charset=utf-8", "application/json", "application/jose+jwe"},
            method = RequestMethod.POST
    )
    ResponseEntity<OBDomesticVRPResponse> domesticVrpPost(
            @ApiParam(value = "An Authorisation Token as per https://tools.ietf.org/html/rfc6750", required = true)
            @RequestHeader(value = "Authorization", required = true) String authorization,

            @ApiParam(value = "A detached JWS signature of the body of the payload.", required = true)
            @RequestHeader(value = "x-jws-signature", required = true) String xJwsSignature,

            @ApiParam(value = "Default", required = true)
            @Valid
            @RequestBody OBDomesticVRPRequest obDomesticVRPRequest,

            @ApiParam(value = "The time when the PSU last logged in with the TPP.  All dates in the HTTP headers are" +
                    " represented as RFC 7231 Full Dates. An example is below:  Sun, 10 Sep 2017 19:43:31 UTC")
            @RequestHeader(value = "x-fapi-auth-date", required = false) String xFapiAuthDate,

            @ApiParam(value = "The PSU's IP address if the PSU is currently logged in with the TPP.")
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String xFapiCustomerIpAddress,

            @ApiParam(value = "An RFC4122 UID used as a correlation id.")
            @RequestHeader(value = "x-fapi-interaction-id", required = false) String xFapiInteractionId,

            @ApiParam(value = "Indicates the user-agent that the PSU is using.")
            @RequestHeader(value = "x-customer-user-agent", required = false) String xCustomerUserAgent,

            @ApiParam(value = "Indicates if 'ReadRefundAccount' was set to 'Yes' or 'No' in the consent. Defaults to 'No'", required = false)
            @RequestHeader(value = "x-read-refund-account", required = false) String xReadRefundAccount,

            HttpServletRequest request,

            Principal principal
    ) throws OBErrorResponseException;

}
