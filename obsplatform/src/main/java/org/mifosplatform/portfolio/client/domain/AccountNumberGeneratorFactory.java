/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import org.springframework.stereotype.Component;

@Component
public class AccountNumberGeneratorFactory {

    public ZeroPaddedAccountNumberGenerator determineClientAccountNoGenerator(final Long clientId) {
        return new ZeroPaddedAccountNumberGenerator(clientId, 9);
    }

    public ZeroPaddedAccountNumberGenerator determineLoanAccountNoGenerator(final Long loanId) {
        return new ZeroPaddedAccountNumberGenerator(loanId, 9);
    }

    public ZeroPaddedAccountNumberGenerator determineSavingsAccountNoGenerator(final Long savingsAccountId) {
        return new ZeroPaddedAccountNumberGenerator(savingsAccountId, 9);
    }
}