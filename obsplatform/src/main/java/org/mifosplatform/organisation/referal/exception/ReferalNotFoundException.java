/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when office resources are not found.
 */
public class ReferalNotFoundException extends AbstractPlatformResourceNotFoundException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReferalNotFoundException(final Long id) {
        super("error.msg.referal.id.invalid", "Referal with identifier " + id + " does not exist", id);
    }
	
	public ReferalNotFoundException(final String externalId) {
		super("error.msg.referal.id.invalid", "Referal with identifier " + externalId + " does not exist", externalId);
    }
}