package org.mifosplatform.organisation.lco.service;

import java.sql.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.serialization.ChannelCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.lco.serialization.LCOCommandFromApiJsonDesrializer;
import org.mifosplatform.portfolio.jvtransaction.domain.JVTransaction;
import org.mifosplatform.portfolio.jvtransaction.domain.JVTransactionRepository;
import org.mifosplatform.portfolio.service.domain.ServiceDetails;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class LCOWriteServiceImplementation implements LCOWritePlatformService{

	private final PlatformSecurityContext context;
	private final LCOCommandFromApiJsonDesrializer apiJsonDeserializer;
	private final JVTransactionRepository jVTransactionRepository;
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public LCOWriteServiceImplementation(PlatformSecurityContext context,
			LCOCommandFromApiJsonDesrializer apiJsonDesrializer,
			JVTransactionRepository jvTransactionRepository,
			FromJsonHelper fromApiJsonHelper) {
		
		this.context = context;
		this.apiJsonDeserializer=apiJsonDesrializer;
		this.jVTransactionRepository=jvTransactionRepository;
		this.fromApiJsonHelper=fromApiJsonHelper;
		
		}
	@Override
	public CommandProcessingResult renewal(JsonCommand command) {
		JVTransaction jvTransaction = null;

		try{
			context.authenticatedUser();
			apiJsonDeserializer.validateForRenewal(command.json());
			
			final JsonArray lcoclientArray = command.arrayOfParameterNamed("lco").getAsJsonArray();
			jvTransaction= this.assembleDetails(lcoclientArray,jvTransaction);
			this.jVTransactionRepository.saveAndFlush(jvTransaction);
			   return new CommandProcessingResult(jvTransaction.getId());
		}catch (Exception e) {
		        System.out.println(e);
		        return  CommandProcessingResult.empty();
		}
	}
	
	
private JVTransaction assembleDetails(JsonArray lcoClientArray, JVTransaction jvTransaction) {
		
		String[]  lcoClients = null;
		lcoClients = new String[lcoClientArray.size()];
		if(lcoClientArray.size() > 0){
			for(int i = 0; i < lcoClientArray.size(); i++){
				lcoClients[i] = lcoClientArray.get(i).toString();
			}
	
		for (final String lcoClient : lcoClients) {
			final JsonElement element = fromApiJsonHelper.parse(lcoClient);
			final Float transAmount = Float.parseFloat(fromApiJsonHelper.extractStringNamed("balanceAmount", element));
			final LocalDate endDate = fromApiJsonHelper.extractLocalDateNamed("endDate", element);
			final LocalDate startDate = fromApiJsonHelper.extractLocalDateNamed("startDate", element);
			final LocalDate jvDate= new LocalDate();
			jvTransaction = new JVTransaction(jvDate,startDate,endDate,transAmount);
		}	 
	}	
	
	return jvTransaction;
}
	
}
