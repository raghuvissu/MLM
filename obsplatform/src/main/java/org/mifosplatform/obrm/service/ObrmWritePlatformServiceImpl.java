package org.mifosplatform.obrm.service;

import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.obrm.api.ObrmApiConstants;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAddress;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObrmWritePlatformServiceImpl implements ObrmWritePlatformService{

	private final OfficeRepository officeRepository;
	private final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	
	@Autowired
	public ObrmWritePlatformServiceImpl(OfficeRepository officeRepository,
			final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService,
			final MCodeReadPlatformService mCodeReadPlatformService) {
		
		this.officeRepository=officeRepository;
		this.obrmReadWriteConsiliatrService = obrmReadWriteConsiliatrService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
}
	
	@Override
	public CommandProcessingResult createClient(JsonCommand jsonCommand) {
			String resourceId=null,accountNo =null;
			final Long officeId = jsonCommand.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);
            final Office clientOffice = this.officeRepository.findOne(officeId);

            if (clientOffice == null) { throw new OfficeNotFoundException(officeId); }
            String priValue = this.accountNumberPriRequirValue();
            Client client = Client.createNew(clientOffice, null, jsonCommand,priValue);
			String result = this.obrmReadWriteConsiliatrService.processObrmRequest(ObrmApiConstants.CREATECLIENT_OPCODE,client.obrmRequestInput());
			System.out.println(result);
			if(result !=null){
				resourceId = this.retrivePoid(result);
				accountNo = this.retriveAccountNo(result);
			}
				
			return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).withTransactionId(accountNo).build();
	}
	
	@Override
	public CommandProcessingResult createOffice(JsonCommand command) {
		
		String resourceId = null;
        Office ofice = Office.fromJson(null, command);
        
        OfficeAddress addres =OfficeAddress.fromJson(command,ofice);
        ofice.setOfficeAddress(addres);
        String result = this.obrmReadWriteConsiliatrService.processObrmRequest(ObrmApiConstants.CREATEOFFICE_OPCODE,addres.obrmRequestInput());
		if(result !=null){
			resourceId = this.retrivePoid(result);
		}
		return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).build();
		
	}
	
	@Override
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command) {
		String resourceId =null;
	

        ClientServiceData clientServiceData = ClientServiceData.fromJsonForOBRM(command);
		String result = this.obrmReadWriteConsiliatrService.processObrmRequest(ObrmApiConstants.CREATECLIENTSIMPLEACTIVATION_OPCODE,clientServiceData.obrmRequestInput());
		System.out.println(result);
		if(result !=null){
			resourceId = this.retrivePoid(result);
		}
			
		return new CommandProcessingResultBuilder().withResourceIdAsString(resourceId).build();
}
	
	
	
	
	
	private String retrivePoid(String result) {
		try{
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:MSO_OP_CUST_REGISTER_CUSTOMER_outputFlist");
			String returnValue = object.optString("brm:POID");
			if(returnValue !=null){
				String[] args = returnValue.split(" ");
				returnValue = args[2];
				System.out.println(returnValue);
			}else{
				throw new PlatformDataIntegrityException("poid.invalid","","","");	
			}
			return returnValue;
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}

	private String retriveAccountNo(String result) {
		try{
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:MSO_OP_CUST_REGISTER_CUSTOMER_outputFlist");
			return object.optString("brm:ACCOUNT_NO");
			
			
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());	
		}
	}
	
	
	private String accountNumberPriRequirValue() {
		final Collection<MCodeData> mcodeData = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.NUMBER_GENERATOR);
		String priValue=null;
		for(MCodeData data:mcodeData){
    	   if(data.getOrderPossition() == 1){
    		   priValue = data.getmCodeValue();
    	        break;
    	   }
	    }
		if(priValue == null){
			throw new PlatformDataIntegrityException("error.msg.number.generator.not.available","","","");
		}
		return priValue;
	}
}
