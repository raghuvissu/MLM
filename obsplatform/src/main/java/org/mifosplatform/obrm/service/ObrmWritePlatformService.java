package org.mifosplatform.obrm.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ObrmWritePlatformService {

	public CommandProcessingResult createClient(JsonCommand jsonCommand);
	
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command);


	public CommandProcessingResult createOffice(JsonCommand jsonCommand);

}
