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
package com.forgerock.sapi.gateway.ob.uk.rs.obie.api.event.v3_1_11.aggregatedpolling;

import org.springframework.web.bind.annotation.RequestMapping;

import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.swagger.SwaggerApiTags;

import io.swagger.annotations.Api;

@Api(tags = {"v3.1.11", SwaggerApiTags.EVENT_NOTIFICATION_TAG})
@RequestMapping(value = "/open-banking/v3.1.11/events")
public interface AggregatedPollingApi extends com.forgerock.sapi.gateway.ob.uk.rs.obie.api.event.v3_1_10.aggregatedpolling.AggregatedPollingApi {
}