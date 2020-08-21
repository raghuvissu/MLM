package org.mifosplatform.finance.commissionpayments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface CommissionPaymentWritePlatformService {

	CommandProcessingResult createPayment(JsonCommand command);

}
