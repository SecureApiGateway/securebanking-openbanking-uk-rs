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
package com.forgerock.securebanking.openbanking.uk.rs.api.backoffice.payment.calculation;

import com.forgerock.securebanking.openbanking.uk.common.api.meta.obie.OBVersion;
import lombok.extern.slf4j.Slf4j;
import uk.org.openbanking.datamodel.common.OBChargeBearerType1Code;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse3DataCharges;
import uk.org.openbanking.datamodel.payment.OBWriteDomesticConsentResponse5DataCharges;
import uk.org.openbanking.datamodel.payment.OBWriteFileConsentResponse3;
import uk.org.openbanking.datamodel.payment.OBWriteFileConsentResponse4;

/**
 * Validation class for Domestic Payment Consent response
 * <ul>
 *     <li>
 *         Consent response {@link OBWriteFileConsentResponse3} from v3.1.2 to v3.1.4
 *     </li>
 *     <li>
 *         Consent response {@link OBWriteFileConsentResponse4} from v3.1.5 to v3.1.10
 *     </li>
 * </ul>
 */
@Slf4j
public class FilePaymentConsentResponseCalculation extends PaymentConsentResponseCalculation {

    public static final String TYPE = "UK.OBIE.CHAPSOut";

    @SuppressWarnings("rawtypes")
    @Override
    public Class getResponseClass(OBVersion version) {
        log.debug("{} is the version to calculate response elements", version.getCanonicalName());
        if (version.isBeforeVersion(OBVersion.v3_1_5)) {
            return OBWriteFileConsentResponse3.class;
        }
        return OBWriteFileConsentResponse4.class;
    }

    @Override
    public <T, R> R calculate(T consentRequest, R consentResponse) {
        if (consentResponse instanceof OBWriteFileConsentResponse3) {
            log.debug("OBWriteDomesticConsentResponse3 instance");
            ((OBWriteFileConsentResponse3) consentResponse)
                    .getData()
                    .addChargesItem(
                            new OBWriteDomesticConsentResponse3DataCharges()
                                    .chargeBearer(OBChargeBearerType1Code.BORNEBYDEBTOR)
                                    .type(TYPE)
                                    .amount(getDefaultAmount())
                    );

        } else {
            log.debug("OBWriteDomesticConsentResponse4 instance");
            ((OBWriteFileConsentResponse4) consentResponse)
                    .getData()
                    .addChargesItem(
                            new OBWriteDomesticConsentResponse5DataCharges()
                                    .chargeBearer(OBChargeBearerType1Code.BORNEBYDEBTOR)
                                    .type(TYPE)
                                    .amount(getDefaultAmount())
                    );
        }
        return consentResponse;
    }

}
