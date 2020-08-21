/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * {@link AbstractPlatformDomainRuleException} thrown when office mismatch occurs
 */
public class InvalidReferalException extends AbstractPlatformDomainRuleException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidReferalException(final String entity, final String postFix, String defaultUserMessage, Object... defaultUserMessageArgs) {
        super("error.msg."+entity+"."+postFix+".invalid.referal", defaultUserMessage, defaultUserMessageArgs);
    }
}
