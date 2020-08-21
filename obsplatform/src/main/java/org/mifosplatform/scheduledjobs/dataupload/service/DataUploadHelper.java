package org.mifosplatform.scheduledjobs.dataupload.service;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.mifosplatform.finance.adjustment.data.AdjustmentData;
import org.mifosplatform.finance.adjustment.exception.AdjustmentCodeNotFoundException;
import org.mifosplatform.finance.adjustment.service.AdjustmentReadPlatformService;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.finance.payments.exception.PaymentCodeNotFoundException;
import org.mifosplatform.finance.payments.service.PaymentReadPlatformService;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.ConfigurationPropertyNotFoundException;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.CityDetailsData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.property.data.PropertyDefinationData;
import org.mifosplatform.portfolio.property.service.PropertyReadPlatformService;
import org.mifosplatform.provisioning.provisioning.domain.ModelProvisionMapping;
import org.mifosplatform.provisioning.provisioning.domain.ModelProvisionMappingRepository;
import org.mifosplatform.scheduledjobs.dataupload.data.MRNErrorData;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUpload;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import net.sf.json.JSONObject;

@Service
public class DataUploadHelper {
	
	private final DataUploadRepository dataUploadRepository;
	private final AdjustmentReadPlatformService adjustmentReadPlatformService;
	private final PaymentReadPlatformService paymodeReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final PropertyReadPlatformService propertyReadPlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ModelProvisionMappingRepository modelProvisionMappingRepository;
	private final OfficeRepository officeRepository;
    private final CodeValueRepository codeValueRepository;
    private final PlanRepository planRepository;
    private final ConfigurationRepository configurationRepository;
    String dateFormat = "dd MMMM yyyy";
    String date;
	String officeId;
	@Autowired
	public DataUploadHelper(final DataUploadRepository dataUploadRepository,final PaymentReadPlatformService paymodeReadPlatformService,
			final AdjustmentReadPlatformService adjustmentReadPlatformService,final MCodeReadPlatformService mCodeReadPlatformService,
		final PropertyReadPlatformService propertyReadPlatformService,final AddressReadPlatformService addressReadPlatformService,
		final ModelProvisionMappingRepository modelProvisionMappingRepository,final OfficeRepository officeRepository,final CodeValueRepository codeValueRepository,final PlanRepository planRepository,
		 final ConfigurationRepository configurationRepository) {
		this.dataUploadRepository=dataUploadRepository;
		this.paymodeReadPlatformService=paymodeReadPlatformService;
		this.adjustmentReadPlatformService=adjustmentReadPlatformService;
		this.mCodeReadPlatformService=mCodeReadPlatformService;
		this.propertyReadPlatformService=propertyReadPlatformService;
		this.addressReadPlatformService=addressReadPlatformService;
		this.modelProvisionMappingRepository = modelProvisionMappingRepository;
		this.officeRepository = officeRepository;
		this.codeValueRepository = codeValueRepository;
		this.planRepository=planRepository;
		this.configurationRepository = configurationRepository;
		
		
	}
	
	

	
	
	public String buildJsonForHardwareItems(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)  {
		
		if(currentLineData.length>=9){
			final HashMap<String, String> map = new HashMap<>();
			if(currentLineData[0] != null)
			map.put("itemMasterId",currentLineData[0]);
			map.put("serialNumber",currentLineData[1]);
			map.put("grnId",currentLineData[2]);
			map.put("provisioningSerialNumber",currentLineData[3]);
			map.put("quality", currentLineData[4]);
			map.put("status",currentLineData[5]);
			map.put("warranty", currentLineData[6]);
			map.put("remarks", currentLineData[7]);
			if(!currentLineData[8].isEmpty()){
				ModelProvisionMapping modelProvisionMapping = this.modelProvisionMappingRepository.findOneByModel( currentLineData[8]);
				if(modelProvisionMapping != null){
					map.put("itemModel", modelProvisionMapping.getId().toString());
				}else{
					errorData.add(new MRNErrorData((long)i, "invalid model"));
					return null;
				}
				
			}else{
				map.put("itemModel", currentLineData[8]);
			}
			
			if("N".equalsIgnoreCase(currentLineData[9])){
				map.put("isPairing", "N");
			}else{
				if(!currentLineData[10].equals(currentLineData[0]) ){
					map.put("isPairing", "Y");
					map.put("pairedItemId", currentLineData[10]);
				}else{
					map.put("isPairing", "N");
				}
			}
			map.put("locale", "en");
			return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
			
		}
	}


	public String buildJsonForMrn(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		
		if(currentLineData.length>=2){
			final HashMap<String, String> map = new HashMap<>();
			map.put("mrnId",currentLineData[0]);
			map.put("serialNumber",currentLineData[1]);
			map.put("type",currentLineData[2]);
			map.put("locale","en");
			return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}
		
	}


	public String buildJsonForMoveItems(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		
		if(currentLineData.length>=2){
			final HashMap<String, String> map = new HashMap<>();
			map.put("itemId",currentLineData[0]);
			map.put("serialNumber",currentLineData[1]);
			map.put("type",currentLineData[2]);
			map.put("locale","en");
			return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}
	}


	public String buildJsonForEpg(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException {
		
		if(currentLineData.length >=11){
			final HashMap<String, String> map = new HashMap<>();
			map.put("channelName",currentLineData[0]);
			map.put("channelIcon",currentLineData[1]);
			map.put("programDate",new SimpleDateFormat("dd/MM/yyyy").parse(currentLineData[2]).toString());
			map.put("startTime",currentLineData[3]);
			map.put("stopTime",currentLineData[4]);
			map.put("programTitle",currentLineData[5]);
			map.put("programDescription",currentLineData[6]);
			map.put("type",currentLineData[7]);
			map.put("genre",currentLineData[8]);
			map.put("locale",currentLineData[9]);
			map.put("dateFormat",currentLineData[10]);
			map.put("locale","en");
				return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}


	}


	public String buildJsonForAdjustment(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if(currentLineData.length >= 6){
			final HashMap<String, String> map = new HashMap<>();
			List<AdjustmentData> adjustmentDataList=this.adjustmentReadPlatformService.retrieveAllAdjustmentsCodes();
			if(!adjustmentDataList.isEmpty()){
				 for(AdjustmentData adjustmentData:adjustmentDataList){
					   if( adjustmentData.getAdjustmentCode().equalsIgnoreCase(currentLineData[2].toString())){ 
					        	 
						   map.put("adjustment_code", adjustmentData.getId().toString());
						   break;
					   }else{
						   map.put("adjustment_code", String.valueOf(-1));	
					   }
				    }
				 String adjustmentCode = map.get("adjustment_code");
				   if(adjustmentCode!=null && Long.valueOf(adjustmentCode)<=0){
				    	
				    	throw new AdjustmentCodeNotFoundException(currentLineData[2].toString());
				    }
				map.put("adjustment_date", currentLineData[1]);
				map.put("adjustment_type",currentLineData[3]);
				map.put("amount_paid",currentLineData[4]);
				map.put("Remarks",currentLineData[5]);
				map.put("locale", "en");
				map.put("dateFormat","dd MMMM yyyy");
				return new Gson().toJson(map);	
			}else{
				errorData.add(new MRNErrorData((long)i, "Adjustment Type list is empty"));
				return null;
			}
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}
	}


	public String buildjsonForPayments(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		
	
		if(currentLineData.length >=9){
			final HashMap<String, String> map = new HashMap<>();
			Collection<McodeData> paymodeDataList = this.paymodeReadPlatformService.retrievemCodeDetails("Payment Mode");
				if(!paymodeDataList.isEmpty()){
					for(McodeData paymodeData:paymodeDataList){
						if(paymodeData.getPaymodeCode().equalsIgnoreCase(currentLineData[1])){
							map.put("paymentCode",paymodeData.getId().toString());
							break;
						}else{
							map.put("paymentCode","-1");
						}
					}
					String paymentCode = map.get("paymentCode");
					if(paymentCode!=null && Long.valueOf(paymentCode) <=0){
						throw new PaymentCodeNotFoundException(currentLineData[1].toString());
					}
					
					map.put("amountPaid", currentLineData[2]);
					map.put("receiptNo",currentLineData[3]);
					map.put("remarks",  currentLineData[4]);
					map.put("paymentDate",currentLineData[5]);
					//map.put("chequeNo",currentLineData[6]);
					//map.put("chequeDate",currentLineData[7]);
					//map.put("bankName",currentLineData[8]);
					//map.put("branchName",currentLineData[9]);
					map.put("locale", "en");
					map.put("dateFormat","dd MMMM yyyy");
					map.put("isSubscriptionPayment","false");
					
        	return new Gson().toJson(map);
        }else{
        	errorData.add(new MRNErrorData((long)i, "Paymode type list empty"));
			return null;
        }
		 
	}else{
		errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
		return null;
	}
	}


	public String buildForMediaAsset(Row mediaRow, Row mediaAttributeRow, Row mediaLocationRow) {
		final HashMap<String, String> map = new HashMap<>();
		map.put("mediaTitle",mediaRow.getCell(0).getStringCellValue());//-
		map.put("mediaType",mediaRow.getCell(1).getStringCellValue());//-
		map.put("mediaCategoryId",mediaRow.getCell(2).getStringCellValue());//-
		map.put("image",mediaRow.getCell(3).getStringCellValue());//- 
		map.put("duration",mediaRow.getCell(4).getStringCellValue());//-
		map.put("genre",mediaRow.getCell(5).getStringCellValue());//-
		map.put("subject",mediaRow.getCell(6).getStringCellValue());//-
		map.put("overview",mediaRow.getCell(7).getStringCellValue());//-
		map.put("contentProvider",mediaRow.getCell(8).getStringCellValue());//-
		map.put("rated",mediaRow.getCell(9).getStringCellValue());//-
		//map.put("rating",mediaRow.getCell(10).getNumericCellValue());//-
		map.put("rating",mediaRow.getCell(10).getStringCellValue());//-
		map.put("status",mediaRow.getCell(11).getStringCellValue());//-
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
		map.put("releaseDate",formatter.format(mediaRow.getCell(12).getDateCellValue()));
		map.put("dateFormat",mediaRow.getCell(13).getStringCellValue());
		map.put("locale",mediaRow.getCell(14).getStringCellValue());
				
			JSONArray a = new JSONArray();
			Map<String, Object> m = new LinkedHashMap<String, Object>();
			m.put("attributeType",mediaAttributeRow.getCell(0).getStringCellValue());
			m.put("attributeName", mediaAttributeRow.getCell(1).getNumericCellValue());
			m.put("attributevalue", mediaAttributeRow.getCell(2).getStringCellValue());
			m.put("attributeNickname", mediaAttributeRow.getCell(3).getStringCellValue());
			m.put("attributeImage", mediaAttributeRow.getCell(4).getStringCellValue());
			
			a.put(m);
			map.put("mediaassetAttributes",a.toString());
			
			
			JSONArray b = new JSONArray();
			Map<String, Object> n = new LinkedHashMap<String, Object>();
			n.put("languageId",mediaLocationRow.getCell(0).getNumericCellValue());	
			n.put("formatType",mediaLocationRow.getCell(1).getStringCellValue());
			n.put("location",mediaLocationRow.getCell(2).getStringCellValue());
			b.put(n);
			
			map.put("mediaAssetLocations",b.toString());
			return new Gson().toJson(map);
	}


	public String buildforitemSale(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i)  throws ParseException {
		final HashMap<String, String> map = new HashMap<>();
		if(currentLineData.length>=6){
		map.put("agentId",currentLineData[0]);
		map.put("itemId",currentLineData[1]);
		map.put("orderQuantity",currentLineData[2]);
		map.put("chargeAmount",currentLineData[3]);
		map.put("taxPercantage", currentLineData[4]);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yy");
    	Date date=formatter.parse(currentLineData[5]);
    	SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
    	   
    	map.put("locale", "en");
    	map.put("dateFormat","dd MMMM yyyy");
    	map.put("purchaseDate",formatter1.format(date));
    	return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
		return null;
		}
	}
	public String buildJsonForOffice(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException {
		
		if(currentLineData.length >=9){
			final HashMap<String, String> map = new HashMap<>();
			//parent name business logic
			if(!currentLineData[0].isEmpty()) {
				Office office  = this.officeRepository.findwithName(currentLineData[0]);
				if(office!=null) {
					map.put("parentId",office.getId().toString());
				}else {
					errorData.add(new MRNErrorData((long)i, "invalid parent name"));
					return null;
				}
			}else {
				errorData.add(new MRNErrorData((long)i, "parent name not available"));
				return null;
			}
			
			//office type business logic
			if(!currentLineData[1].isEmpty()) {
				CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(currentLineData[1]);
				if(codeValue!=null) {
					map.put("officeType",codeValue.getId().toString());
				}else {
					errorData.add(new MRNErrorData((long)i, "invalid office type"));
					return null;
				}
			}else {
				errorData.add(new MRNErrorData((long)i, "office type not available"));
				return null;
			}
			//business Type  business logic
			if(!currentLineData[2].isEmpty()) {
				CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(currentLineData[2]);
				if(codeValue!=null) {
					map.put("businessType",currentLineData[2]);
				}else {
					errorData.add(new MRNErrorData((long)i, "invalid business Type"));
					return null;
				}
			}else {
				errorData.add(new MRNErrorData((long)i, "business Type not available"));
				return null;
			}
			
			
			
			//map.put("officeType",currentLineData[1]);
			if(!currentLineData[3].isEmpty()) {
			 map.put("name",currentLineData[3]);
			}else {
				errorData.add(new MRNErrorData((long)i, "name  cannot be blank or name should be alphabets"));
			       return null;
			}
			if(!currentLineData[4].isEmpty()) {
			map.put("externalId",currentLineData[4]);
			}else {
				errorData.add(new MRNErrorData((long)i, "plz enter office description"));
			       return null;
			}
			if(!currentLineData[5].isEmpty()) {
			map.put("contactPerson",currentLineData[5]);
			}else {
				errorData.add(new MRNErrorData((long)i, "contact person can not be blank"));
			       return null;
			}
			if(!currentLineData[6].isEmpty() && currentLineData[6].length()>=10) {
				map.put("phoneNumber",currentLineData[6]);
			}else {
				errorData.add(new MRNErrorData((long)i, "phone number must be in 10 digits"));
				return null;
			}
			if(!currentLineData[7].isEmpty()) {
			map.put("email",currentLineData[7]);
			}else {
				errorData.add(new MRNErrorData((long)i, "email can not be blank"));
			       return null;
			}
			if(!currentLineData[8].isEmpty()) {
			map.put("addressName",currentLineData[8]);
			}else {
				errorData.add(new MRNErrorData((long)i, "address name can not be blank"));
			       return null;
			}
			
			if(!currentLineData[9].isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(currentLineData[9]);
				if(addressData !=null) {
					map.put("city", currentLineData[9]);
					map.put("state", addressData.getState());
					map.put("country", addressData.getCountry());
				}else {
					errorData.add(new MRNErrorData((long)i, "city is invalid"));
					return null;
				}
			}else {
				errorData.add(new MRNErrorData((long)i, "city is not vailable"));
				return null;
			}
			
		
			map.put("zip",currentLineData[10]);
			map.put("locale", "en");
	    	map.put("dateFormat","dd MMMM yyyy");
	    	if(!currentLineData[11].isEmpty() ) {
	    		
		    	SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
		    	formatter.setLenient(false);
		    	try {
		    		Date date=formatter.parse(currentLineData[11]);
		    		SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
		    		map.put("openingDate",formatter1.format(date));
		    	}catch (ParseException e) {
		    		errorData.add(new MRNErrorData((long)i, "given date is in invalid format and that date format must be like 'dd MMMM yyyy' "));
					return null;
		    	}
	    	}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
	    	}
	    	return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}
	
	}

	
	//Add plan
   public String buildJsonForAddPlan(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException, JSONException {
		
			
	     if(currentLineData.length>=5) {
	 
	    	  final HashMap<String, String> map = new HashMap<>();
	    	  map.put("billAlign","true");
	    	  if(!currentLineData[0].isEmpty()) {
	    		  Plan plan=this.planRepository.findwithName(currentLineData[0]);
	    		  if(plan!=null) {
						map.put("planCode",plan.getId().toString());
	    		  }
	    	  }else {
					errorData.add(new MRNErrorData((long)i, "invalid plan code"));
					return null;
				}
	    
	    	//  map.put("planCode",currentLineData[0]);
	    	  map.put("contractPeriod",currentLineData[1]);
	    	  map.put("paytermCode",currentLineData[2]);
	    	  map.put("clientServiceId",currentLineData[3]);
	    	  map.put("isNewplan", "true");
	    	  map.put("locale", "en");
	    	  map.put("dateFormat","dd MMMM yyyy");
	    	  SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
	    	  Date date=formatter.parse(currentLineData[4]);
	    	  SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
	    	  map.put("start_date",formatter1.format(date));
	    	
	    	 return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}

	}
   //change plan
   public String buildJsonForChangePlan(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException, JSONException {
		
		
	     if(currentLineData.length>=7) {
	 
	    	  final HashMap<String, String> map = new HashMap<>();
	    	  map.put("billAlign","true");
	    	  if(!currentLineData[0].isEmpty()) {
	    		  Plan plan=this.planRepository.findwithName(currentLineData[0]);
	    		  if(plan!=null) {
						map.put("planCode",plan.getId().toString());
	    		  }
	    	  }else {
					errorData.add(new MRNErrorData((long)i, "invalid plan code"));
					return null;
				}
	    	//  map.put("planCode",currentLineData[0]);
	    	  map.put("contractPeriod",currentLineData[1]);
	    	  
	    	  map.put("paytermCode",currentLineData[2]);
	    	  map.put("clientServiceId",currentLineData[3]);
	    	  map.put("isNewplan", "true");
	    	  map.put("locale", "en");
	    	  map.put("dateFormat","dd MMMM yyyy");
	    	  SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
	    	  Date date=formatter.parse(currentLineData[4]);
	    	  SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
	    	  map.put("start_date",formatter1.format(date));
	    	  SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMMM yyyy");
	    	  Date date1=formatter.parse(currentLineData[6]);
	    	  SimpleDateFormat formatter3 = new SimpleDateFormat("dd MMMM yyyy");
	    	  map.put("disconnectionDate",formatter1.format(date1));
	    	  map.put("disconnectReason",currentLineData[7]);
	    	  
	    	
	    	 return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}

	}
    //cancel plan
   public String buildJsonForCancelPlan(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException{
		
		
	   if(currentLineData.length>=3) {
			 
	    	  final HashMap<String, String> map = new HashMap<>();
	    	  map.put("disconnectReason",currentLineData[0]);
	    	  map.put("description",currentLineData[1]);
	    	  map.put("locale", "en");
	    	  map.put("dateFormat","dd MMMM yyyy");
	    	  SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
	    	  Date date=formatter.parse(currentLineData[2]);
	    	  SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
	    	  map.put("disconnectionDate",formatter1.format(date));
	    	
	    	 return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}

	}
   public String buildJsonForSuspend(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException{
		
		
	         if(currentLineData.length>=3){
	    	  
	    	  final HashMap<String, String> map = new HashMap<>();
	         
	    	  SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
	    	  Date date=formatter.parse(currentLineData[1]);
	    	  SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
	    	  map.put("locale", "en");
	    	  map.put("dateFormat","dd MMMM yyyy");
	    	  map.put("clientId", currentLineData[0]);
	    	  map.put("suspensionDate",formatter1.format(date));
	    	  map.put("suspensionReason",currentLineData[2]);
	          return new Gson().toJson(map);
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
		}

	}
   public String buildJsonForreactive(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) throws ParseException{
		
		
       if(currentLineData.length>=1){
  	  
  	   final HashMap<String, String> map = new HashMap<>();
       
  	   map.put("clientId", currentLineData[0]);
  	   return new Gson().toJson(map);
	}else{
		errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
		return null;
	}

}
   
   
   public CommandProcessingResult updateFile(DataUpload dataUpload,Long totalRecordCount, Long processRecordCount,
			ArrayList<MRNErrorData> errorData) {
		
		dataUpload.setProcessRecords(processRecordCount);
		dataUpload.setUnprocessedRecords(totalRecordCount-processRecordCount);
		dataUpload.setTotalRecords(totalRecordCount);
		writeCSVData(dataUpload.getUploadFilePath(), errorData,dataUpload);
		processRecordCount=0L;totalRecordCount=0L;
		this.dataUploadRepository.save(dataUpload);
		final String filelocation=dataUpload.getUploadFilePath();
		dataUpload=null;
		writeToFile(filelocation,errorData);
		return new CommandProcessingResult(Long.valueOf(1));
	}


	private void writeToFile(String uploadFilePath,ArrayList<MRNErrorData> errorData) {
		
			FileWriter fw = null;
			try{
				File f = new File(uploadFilePath.replace(".csv", ".log"));
				if(!f.exists()){
					f.createNewFile();
				}
				fw = new FileWriter(f,true);
				for(int k=0;k<errorData.size();k++){
					if(!errorData.get(k).getErrorMessage().equalsIgnoreCase("Success.")){
						fw.append("Data at row: "+errorData.get(k).getRowNumber()+", Message: "+errorData.get(k).getErrorMessage()+"\n");
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(fw!=null){
						fw.flush();
						fw.close();
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	


	private void writeCSVData(String uploadFilePath,ArrayList<MRNErrorData> errorData,DataUpload uploadStatus) {
		

		
		try{
			
			long processRecords = uploadStatus.getProcessRecords();
			long totalRecords = uploadStatus.getTotalRecords();
			long unprocessRecords  = totalRecords-processRecords;
			if(unprocessRecords == 0){
				uploadStatus.setProcessStatus("Success");
				uploadStatus.setErrorMessage("Data successfully saved");
			}else if(unprocessRecords<totalRecords){
				uploadStatus.setProcessStatus("Completed");
				uploadStatus.setErrorMessage("Completed with some errors");
			}else if(unprocessRecords == totalRecords){
				uploadStatus.setProcessStatus("Failed");
				uploadStatus.setErrorMessage("Processing failed");
			}
			
			uploadStatus.setProcessDate(DateUtils.getDateOfTenant());
			this.dataUploadRepository.save(uploadStatus);
			uploadStatus = null;
		}catch(Exception  exception){
			exception.printStackTrace();
		}
		
		
	}

	public String buildjsonForPropertyDefinition(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 10) {
			final HashMap<String, String> map = new HashMap<>();
			 map.put("propertyCode", currentLineData[0]);
			final Collection<MCodeData> propertyTypesList = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_PROPERTY_TYPE);
			if (!propertyTypesList.isEmpty()) {
				for (MCodeData mCodeData : propertyTypesList) {
					if (mCodeData.getmCodeValue().equalsIgnoreCase(currentLineData[1].toString().trim())) {
						map.put("propertyType", mCodeData.getId().toString());
						break;
					} 
				}

				final Collection<PropertyDefinationData> unitCodesList = this.propertyReadPlatformService.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_UNIT,currentLineData[2].trim());
				if (!unitCodesList.isEmpty()) {
					for (PropertyDefinationData unitData : unitCodesList) {
						if (unitData.getCode().equalsIgnoreCase(currentLineData[2].toString().trim())) {
							map.put("unitCode", currentLineData[2].trim());
							break;
						}
					}

					final Collection<PropertyDefinationData> floorList = this.propertyReadPlatformService.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_FLOOR,currentLineData[3].trim());
					if (!floorList.isEmpty()) {
						for (PropertyDefinationData floorData : floorList) {
							if (floorData.getCode().equalsIgnoreCase(currentLineData[3].toString().trim())) {
								map.put("floor", currentLineData[3].trim());
								break;
							}
						}

						final Collection<PropertyDefinationData> buildingCodeList = this.propertyReadPlatformService.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_BUILDING,currentLineData[4].trim());
						if (!buildingCodeList.isEmpty()) {
							for (PropertyDefinationData buildingCode : buildingCodeList) {
								if (buildingCode.getCode().equalsIgnoreCase(currentLineData[4].toString().trim())) {
									map.put("buildingCode", currentLineData[4].trim());
									break;
								}
							}

							final Collection<PropertyDefinationData> parcelList = this.propertyReadPlatformService.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_PARCEL,currentLineData[5].trim());
							if (!buildingCodeList.isEmpty()) {
								for (PropertyDefinationData parcel : parcelList) {
									if (parcel.getCode().equalsIgnoreCase(currentLineData[5].toString().trim())) {
										map.put("parcel", currentLineData[5].trim());
										break;
									}
								}
					         final List<CityDetailsData> cityDetailsList = this.addressReadPlatformService.retrieveAddressDetailsByCityName(currentLineData[6].trim());
					         if (!cityDetailsList.isEmpty()) {
									for (CityDetailsData cityDetail : cityDetailsList) {
										if (cityDetail.getCityName().equalsIgnoreCase(currentLineData[6].toString().trim())) {
											map.put("precinct", currentLineData[6].trim());
											break;
										}
									}
								map.put("poBox", currentLineData[7]);
								map.put("street", currentLineData[8]);
								map.put("state", currentLineData[9]);
								map.put("country", currentLineData[10]);
								return new Gson().toJson(map);
							  } else {
								errorData.add(new MRNErrorData((long) i,"Precinct list is empty"));
								return null;
							  }
							} else {
								errorData.add(new MRNErrorData((long) i,"Parcel list is empty"));
								return null;
							}
						} else {
							errorData.add(new MRNErrorData((long) i,"buildingCode list is empty"));
							return null;
						}
					} else {
						errorData.add(new MRNErrorData((long) i,"floor list is empty"));
						return null;
					}
				} else {
					errorData.add(new MRNErrorData((long) i,"unitCode list is empty"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i,"Property Types list is empty"));
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i,"Improper Data in this line"));
			return null;
		}
	}

	public String buildjsonForPropertyCodeMaster(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		
		 if(currentLineData.length>=3){
			final HashMap<String, String> map = new HashMap<>();
		    final Collection<MCodeData> propertyCodeTypesList =this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.PROPERTY_CODE_TYPE);
			if(!propertyCodeTypesList.isEmpty()){
				 for(MCodeData mCodeData:propertyCodeTypesList){
					   if(mCodeData.getmCodeValue().equalsIgnoreCase(currentLineData[0].toString())){ 
							map.put("propertyCodeType",mCodeData.getmCodeValue());
						   break;
					   }
				    }
				    map.put("code",currentLineData[1]);
					map.put("description",currentLineData[2]);
					if(currentLineData.length==4){
						map.put("referenceValue", currentLineData[3]);
					}
					return new Gson().toJson(map);	
			}else{
				errorData.add(new MRNErrorData((long)i, "Property Code Type list is empty"));
				return null;
			}
		}else{
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;
			
		}
	}





	public String buildJsonForSimpleActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i){
		if(currentLineData.length>=15){
			try {
			JSONObject jsonObject = new JSONObject();
			final JSONArray clientData = this.prepareClientJsonForSimpleActivation(currentLineData, errorData, i);
			final JSONArray clientServiceData = this.prepareClientServiceJsonForSimpleActivation(currentLineData, errorData, i);
			final JSONArray deviceData = this.preparedeviceDataJsonForSimpleActivation(currentLineData, errorData, i);
			final JSONArray planData = this.prepareplanDataJsonForSimpleActivation(currentLineData, errorData, i);
			
			if(clientData !=null && clientServiceData != null && deviceData!=null && planData != null) {
				jsonObject.put("clientData",String.valueOf(clientData));
				jsonObject.put("clientServiceData",String.valueOf(clientServiceData));
				jsonObject.put("deviceData",String.valueOf(deviceData));
				jsonObject.put("planData",String.valueOf(planData));
			
				return jsonObject.toString();
			}else {
				errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
				return null;
			}
			}catch(Exception e) {
				return null;
			}
		}else {
			errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
			return null;	
		}
		
	}
	
	
	private JSONArray prepareClientJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i){
		try{	
			JSONArray array = new JSONArray();
			JSONObject clientObject = new JSONObject();
			//adding required fields to clientJson Object
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			date = formatter.format(new Date());
			
			
			clientObject.put("activationDate", date);
			clientObject.put("entryType","IND");
			
			//for office fun
			if(!currentLineData[0].isEmpty()) {
				Office office  = this.officeRepository.findwithName(currentLineData[0]);
				if(office!=null) {
					officeId = office.getId().toString();
					clientObject.put("officeId",officeId);
				}else {
					errorData.add(new MRNErrorData((long)i, "invalid office name"));
					return null;
				}
			}else {
				errorData.add(new MRNErrorData((long)i, "office not available"));
				return null;
			}
			
			//for client category fun
			CodeValue codeValue = this.codeValueRepository.findOneByCodeValue("Normal");
			if(codeValue!=null) {
				clientObject.put("clientCategory", codeValue.getId().toString());
			}
			if(codeValue!=null) {
				clientObject.put("clientCategory",codeValue.getId().toString());
			}else {
				errorData.add(new MRNErrorData((long)i, "clientCategory"));
				return null;
			}
			
			
			clientObject.put("title", "Mr.");
			clientObject.put("firstname", currentLineData[1]);
			clientObject.put("lastname", ".");
			clientObject.put("phone", currentLineData[2]);
			Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_SELFCAREUSER);
			if(configuration == null){
				errorData.add(new MRNErrorData((long)i, "is selfcareuser configuration not found"));
            }else if(configuration.isEnabled()){
            	clientObject.put("email",Calendar.getInstance().getTimeInMillis()+"@gmail.com");
            }
			
			clientObject.put("addressNo", currentLineData[3]);
			
			if(!currentLineData[4].isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(currentLineData[4]);
				if(addressData !=null) {
					clientObject.put("city", currentLineData[4]);
					clientObject.put("state", addressData.getState());
					clientObject.put("country", addressData.getCountry());
				}else {
					errorData.add(new MRNErrorData((long)i, "city is invalid"));
					return null;
				}
			}else {
				errorData.add(new MRNErrorData((long)i, "city is not vailable"));
				return null;
			}
			
			clientObject.put("locale", "en");
			clientObject.put("active", "true");//hardcoded
			clientObject.put("dateFormat", dateFormat);
			clientObject.put("flag","false");
			
			array.put(clientObject);
			
			return array;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}
	
	private JSONArray prepareClientServiceJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i){
		try{	
			JSONArray array = new JSONArray();
			JSONObject clientserviceObject = new JSONObject(); 
			clientserviceObject.put("serviceId",currentLineData[5]);
		 
			clientserviceObject.put("clientServiceDetails",String.valueOf(this.prepareclientServiceDetailsJsonForSimpleActivation(currentLineData,errorData,i)));
			
			array.put(clientserviceObject);
			return array;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}
	
	private JSONArray prepareclientServiceDetailsJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i) throws ParseException{
		try{
			JSONArray array = new JSONArray();
			 JSONObject clientServiceDetailsObject = new JSONObject(); 
			//adding required fields to clientserviceJson Object
			  clientServiceDetailsObject.put("status","new");
			 clientServiceDetailsObject.put("parameterId",currentLineData[6]); // Hardcoded for NE
			 clientServiceDetailsObject.put("parameterValue",currentLineData[7]); //Hardcoded for ABV
			
			 array.put(clientServiceDetailsObject);
			 
			 return array;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}
	

	private JSONArray preparedeviceDataJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i){
		try{	
			JSONArray array = new JSONArray(); 
			JSONObject deviceObject = new JSONObject(); 
			//adding required fields to deviceJson Object
			deviceObject.put("locale", "en");
			deviceObject.put("dateFormat", dateFormat);
			
			deviceObject.put("officeId",officeId);
			deviceObject.put("itemId", currentLineData[9]); // Hardcoded for SC
			deviceObject.put("chargeCode", "OTC");
			deviceObject.put("unitPrice", currentLineData[10]);
			deviceObject.put("quantity", String.valueOf(1));
			deviceObject.put("discountId", String.valueOf(1));
			deviceObject.put("totalPrice", currentLineData[10]);
			deviceObject.put("saleType", "NEWSALE");
			deviceObject.put("saleDate", date);
			deviceObject.put("serialNumber",String.valueOf(this.prepareserialNumberJsonForSimpleActivation(currentLineData,errorData,i, true))); 
			if(!currentLineData[11].isEmpty()) {
				deviceObject.put("isPairing", "Y");
				deviceObject.put("pairableItemDetails",String.valueOf(this.preparepairableItemDetailsJsonForSimpleActivation(currentLineData,errorData,i))); 
			}else {
				deviceObject.put("isPairing", "N");
			}
		
			array.put(deviceObject);
			
			return array;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}
	
	private JSONArray prepareserialNumberJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i,boolean isStb) throws ParseException{
	try{
		JSONArray array = new JSONArray();
		JSONObject serialNumberObject = new JSONObject(); 
		//adding required fields to deviceJson Object
		if(isStb) {
			serialNumberObject.put("serialNumber", currentLineData[8]);
			serialNumberObject.put("status","allocated");
			serialNumberObject.put("itemMasterId", currentLineData[9]);	
			serialNumberObject.put("isNewHw","Y");
			serialNumberObject.put("itemType", "STB");
		}else {
			serialNumberObject.put("serialNumber", currentLineData[11]);
			serialNumberObject.put("status","allocated");
			serialNumberObject.put("itemMasterId", currentLineData[9]);	
			serialNumberObject.put("isNewHw","Y");
			serialNumberObject.put("itemType", "SC"); 
		}
		
		array.put(serialNumberObject);
		
		return array;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}
	private JSONObject preparepairableItemDetailsJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i)throws ParseException {
		try{	
			JSONObject ItemDetailsObject = new JSONObject(); 
			//adding required fields to deviceJson Object
			ItemDetailsObject.put("locale", "en");
			ItemDetailsObject.put("dateFormat", dateFormat);
			ItemDetailsObject.put("officeId",officeId);
			ItemDetailsObject.put("itemId", currentLineData[12]); 
			ItemDetailsObject.put("chargeCode", "OTC");
			ItemDetailsObject.put("unitPrice", currentLineData[13]);
			ItemDetailsObject.put("quantity", String.valueOf(1));
			ItemDetailsObject.put("discountId", String.valueOf(1));
			ItemDetailsObject.put("totalPrice", currentLineData[13]);
			ItemDetailsObject.put("saleType", "NEWSALE");
			ItemDetailsObject.put("saleDate", date);
			ItemDetailsObject.put("serialNumber",String.valueOf(this.prepareserialNumberJsonForSimpleActivation(currentLineData,errorData,i, false))); 
			ItemDetailsObject.put("isPairing", "N"); 
	         
			
			return ItemDetailsObject;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}



//plan
	private JSONArray prepareplanDataJsonForSimpleActivation(String[] currentLineData,ArrayList<MRNErrorData> errorData, int i){
		try{	
			JSONArray array = new JSONArray();
			JSONObject planObject = new JSONObject(); 
			//adding required fields to planJson Object
			  planObject.put("billAlign", "false");
			  planObject.put("autoRenew", "");
			  planObject.put("planCode", currentLineData[14]);
			  planObject.put("contractPeriod","2");
			  planObject.put("paytermCode", "Monthly");
			  planObject.put("isNewplan", "true");
			  planObject.put("locale", "en");
			  planObject.put("dateFormat", dateFormat);
			  planObject.put("start_date", date);
	
			  array.put(planObject);
			  return array;
		}catch(Exception e){
			errorData.add(new MRNErrorData((long)i, e.getMessage()));
			return null;
		}
	}
}
	
	