package org.mifosplatform.crm.service;

import java.util.HashMap;
import java.util.Map;

import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.obrm.service.ObrmReadWriteConsiliatrService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CRMServicesImpl implements CrmServices{

	private final ConfigurationRepository configurationRepository;
	private final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService;
	 
	@Autowired
	public CRMServicesImpl(ConfigurationRepository configurationRepository,
			final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService) {
		
		this.configurationRepository = configurationRepository;
		this.obrmReadWriteConsiliatrService = obrmReadWriteConsiliatrService;
	}

	@Override
	public String findOneTargetcCrm() {
		try{ 
			final Configuration configuration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_CRM_ENABLE);
		     if(null != configuration && configuration.isEnabled()){
		    	 return configuration.getValue();
		     }else{
		    	 return null;
		     }
		}catch(Exception e){
			 throw new PlatformDataIntegrityException("error.msg.configuration.not.available", e.getMessage());
		}
	}
	
	
	@Override
	public ClientData retriveClientTotalData(String key, String value) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("key", key);inputs.put("value", value);inputs.put("target","client360");
			return (ClientData)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
	}
	
	
	@Override
	public CommandProcessingResult createClient(JsonCommand command) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("json", command);
			inputs.put("target","createclient");
			return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
		
	}
	


	@Override
	public CommandProcessingResult createOffice(JsonCommand command) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("json", command);
			inputs.put("target","createOffice");
			return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
		
	}
	
	
	
	
	
	
	private Object processRequestCommandHandler(String targetCCrm, Map<String, Object> inputs) {
		switch(targetCCrm){
			case "Oracle":
				return this.obrmReadWriteConsiliatrService.obrmProcessCommandHandler(inputs);
			case "SAP":
				return /*this.processXYZRequest(inputs);*/null;
				
			default:
				 throw new PlatformDataIntegrityException("invalid.target.ccrm", "","","");	
		}
	}



	

}
