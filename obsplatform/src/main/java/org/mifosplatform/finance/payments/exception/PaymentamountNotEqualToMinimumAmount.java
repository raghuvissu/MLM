package org.mifosplatform.finance.payments.exception;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class PaymentamountNotEqualToMinimumAmount extends AbstractPlatformDomainRuleException {


	private static final long serialVersionUID = -2726286660273906232L;
	public PaymentamountNotEqualToMinimumAmount(final BigDecimal amount) {
		super("error.msg.payments.amount.not.equal.minimum.amount", "Payment amount not eqaul to minimum amount "+amount, amount);
	}
}
