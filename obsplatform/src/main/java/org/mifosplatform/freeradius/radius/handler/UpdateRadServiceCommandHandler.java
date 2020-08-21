package org.mifosplatform.freeradius.radius.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.freeradius.radius.service.RadiusWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

	@Service
	public class UpdateRadServiceCommandHandler implements NewCommandSourceHandler {

		private final RadiusWritePlatformService writePlatformService;

		@Autowired
		public UpdateRadServiceCommandHandler(final RadiusWritePlatformService writePlatformService) {
			this.writePlatformService = writePlatformService;
		}

		@Transactional
		@Override
		public CommandProcessingResult processCommand(final JsonCommand command) {

			return this.writePlatformService.updateRadService(command.entityId(),command);
		}
	}	
	

