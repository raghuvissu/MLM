/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Exception thrown when an attempt is made update the parent of a root Referal.
 */
public class RootReferalParentCannotBeUpdated extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RootReferalParentCannotBeUpdated() {
        super("error.msg.referal.cannot.update.parent.referal.of.root.referal", "The root Referal must not be set with a parent Referal.");
    }
}
