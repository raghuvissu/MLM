package org.mifosplatform.finance.entitypayments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface EntityPaymentWritePlatformService {

	CommandProcessingResult createEntityPayment(final JsonCommand command);

}
