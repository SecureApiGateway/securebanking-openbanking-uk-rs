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
package com.forgerock.sapi.gateway.rs.resource.store.repo.mongo.accounts.accounts;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.account.FRExternalPermissionsCode;
import com.forgerock.sapi.gateway.rs.resource.store.repo.entity.account.FRAccount;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface FRAccountRepositoryCustom {

    Collection<FRAccount> byUserIDWithPermissions(
            @Param("userID") String userID,
            @Param("permissions") List<FRExternalPermissionsCode> permissions,
            Pageable pageable);

    FRAccount byAccountId(
            @Param("accountId") String accountId,
            @Param("permissions") List<FRExternalPermissionsCode> permissions);

    FRAccount byAccountId(
            @Param("accountId") String accountId);

    List<FRAccount> byAccountIds(
            @Param("accountId") List<String> accountIds,
            @Param("permissions") List<FRExternalPermissionsCode> permissions);

    List<String> getUserIds(DateTime from, DateTime to);
}
