package org.mifosplatform.finance.payments.exception;

import java.math.BigDecimal;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class WalletAmountExceedsException extends AbstractPlatformDomainRuleException {


	private static final long serialVersionUID = -2726286660273906232L;
	public WalletAmountExceedsException(final BigDecimal amount) {
		super("error.msg.walletamount.is.graterthan.actualamount", "Wallet amount exceeds with actual amount",amount);
	}
}
