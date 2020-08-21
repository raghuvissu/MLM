package org.mifosplatform.crm.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public interface CrmServices {
	
	

	public String findOneTargetcCrm();
	
	public ClientData retriveClientTotalData(String key,String value);

	public CommandProcessingResult createClient(JsonCommand command);

	CommandProcessingResult createOffice(JsonCommand command);
	
	
}
