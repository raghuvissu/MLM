package org.mifosplatform.finance.payments.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentOTPNotFoundException extends AbstractPlatformDomainRuleException {


	private static final long serialVersionUID = -2726286660273906232L;
	public PaymentOTPNotFoundException(final String otp) {
		super("error.msg.payments.payment.otp.invalid", "Payment confrim otp "+otp+" is not equal with actual otp.");
	}
}
