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
package com.forgerock.sapi.gateway.ob.uk.rs.server.api.obie.account.v3_1_1.scheduledpayments;

import com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.payment.FRScheduledPaymentConverter;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.AccountDataInternalIdFilter;
import com.forgerock.sapi.gateway.ob.uk.rs.server.common.util.PaginationUtil;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.document.account.FRScheduledPayment;
import com.forgerock.sapi.gateway.ob.uk.rs.server.persistence.repository.accounts.scheduledpayments.FRScheduledPaymentRepository;
import com.forgerock.sapi.gateway.ob.uk.rs.obie.api.account.v3_1_1.scheduledpayments.ScheduledPaymentsApi;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code;
import uk.org.openbanking.datamodel.account.OBReadScheduledPayment2;
import uk.org.openbanking.datamodel.account.OBReadScheduledPayment2Data;

import java.util.List;
import java.util.stream.Collectors;

import static com.forgerock.sapi.gateway.ob.uk.common.datamodel.converter.account.FRExternalPermissionsCodeConverter.toFRExternalPermissionsCodeList;

@Controller("ScheduledPaymentsApiV3.1.1")
@Slf4j
public class ScheduledPaymentsApiController implements ScheduledPaymentsApi {
    private final FRScheduledPaymentRepository frScheduledPaymentRepository;
    private final AccountDataInternalIdFilter accountDataInternalIdFilter;
    @Value("${rs.page.default.scheduled-payments.size:10}")
    private int PAGE_LIMIT_SCHEDULE_PAYMENTS;

    public ScheduledPaymentsApiController(FRScheduledPaymentRepository frScheduledPaymentRepository,
                                          AccountDataInternalIdFilter accountDataInternalIdFilter) {
        this.frScheduledPaymentRepository = frScheduledPaymentRepository;
        this.accountDataInternalIdFilter = accountDataInternalIdFilter;
    }

    @Override
    public ResponseEntity<OBReadScheduledPayment2> getAccountScheduledPayments(String accountId,
                                                                               int page,
                                                                               String authorization,
                                                                               DateTime xFapiCustomerLastLoggedTime,
                                                                               String xFapiCustomerIpAddress,
                                                                               String xFapiInteractionId,
                                                                               String xCustomerUserAgent,
                                                                               List<OBExternalPermissions1Code> permissions,
                                                                               String httpUrl
    ) {
        log.info("Read scheduled payments for account {} with minimumPermissions {}", accountId, permissions);
        Page<FRScheduledPayment> scheduledPayments = frScheduledPaymentRepository.byAccountIdWithPermissions(
                accountId,
                toFRExternalPermissionsCodeList(permissions),
                PageRequest.of(page, PAGE_LIMIT_SCHEDULE_PAYMENTS));
        int totalPages = scheduledPayments.getTotalPages();

        return ResponseEntity.ok(new OBReadScheduledPayment2()
                .data(new OBReadScheduledPayment2Data()
                        .scheduledPayment(
                                scheduledPayments.getContent()
                                        .stream()
                                        .map(FRScheduledPayment::getScheduledPayment)
                                        .map(FRScheduledPaymentConverter::toOBScheduledPayment2)
                                        .map(dd -> accountDataInternalIdFilter.apply(dd))
                                        .collect(Collectors.toList())))
                .links(PaginationUtil.generateLinks(httpUrl, page, totalPages))
                .meta(PaginationUtil.generateMetaData(totalPages)));
    }

    @Override
    public ResponseEntity<OBReadScheduledPayment2> getScheduledPayments(int page,
                                                                        String authorization,
                                                                        DateTime xFapiCustomerLastLoggedTime,
                                                                        String xFapiCustomerIpAddress,
                                                                        String xFapiInteractionId,
                                                                        String xCustomerUserAgent,
                                                                        List<String> accountIds,
                                                                        List<OBExternalPermissions1Code> permissions,
                                                                        String httpUrl
    ) {
        log.info("Reading schedule payment from account ids {}", accountIds);
        Page<FRScheduledPayment> scheduledPayments = frScheduledPaymentRepository.byAccountIdInWithPermissions(
                accountIds,
                toFRExternalPermissionsCodeList(permissions),
                PageRequest.of(page, PAGE_LIMIT_SCHEDULE_PAYMENTS));
        int totalPages = scheduledPayments.getTotalPages();

        return ResponseEntity.ok(new OBReadScheduledPayment2().data(new OBReadScheduledPayment2Data().scheduledPayment(
                        scheduledPayments.getContent()
                                .stream()
                                .map(FRScheduledPayment::getScheduledPayment)
                                .map(FRScheduledPaymentConverter::toOBScheduledPayment2)
                                .map(dd -> accountDataInternalIdFilter.apply(dd))
                                .collect(Collectors.toList())))
                .links(PaginationUtil.generateLinks(httpUrl, page, totalPages))
                .meta(PaginationUtil.generateMetaData(totalPages)));
    }
}
