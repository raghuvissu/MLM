package org.mifosplatform.finance.entitypayments.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.entitypayments.service.EntityPaymentWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityPaymentCommandHandler implements NewCommandSourceHandler {

	private final EntityPaymentWritePlatformService writePlatformService;

	@Autowired
	public EntityPaymentCommandHandler(final EntityPaymentWritePlatformService writePlatformService) {
		
		this.writePlatformService = writePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {

		return this.writePlatformService.createEntityPayment(command);
	}
	
	
}
