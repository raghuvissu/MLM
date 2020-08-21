package org.mifosplatform.finance.payments.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PaymentWritePlatformService {

	CommandProcessingResult createPayment(JsonCommand command);
	
	CommandProcessingResult confirmPayment(Long paymentId, String otp);

	CommandProcessingResult cancelPayment(JsonCommand command,Long entityId);

	CommandProcessingResult paypalEnquirey(JsonCommand command);

	CommandProcessingResult createReferalWithdrawPayment(JsonCommand command, Long entityId);

}
