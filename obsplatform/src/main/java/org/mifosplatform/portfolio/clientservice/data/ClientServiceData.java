package org.mifosplatform.portfolio.clientservice.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;

public class ClientServiceData {
	
	private Long id;
	private Long serviceId;
	private String serviceCode;
	private String serviceDescription;
	private String status;
	
	private String poid;
	
	private List<ServiceData> serviceData;
	List<ServiceParameterData> ServiceParameterData;
	private String accountNumber;
	private List<PlanData> planData;
	private String stbNo;
	private String scNo;

	
	public ClientServiceData() {
	}


	public ClientServiceData(Long id, Long serviceId, String serviceCode, String serviceDescription, String status) {
		this.id = id;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.status = status;
	}


	
	//its used for the purpose of obrm functionality
	public ClientServiceData(String accountNumber, List<PlanData> planData, String stbNo, String scNo) {
		this.accountNumber = accountNumber;
		this.planData = planData;
		this.stbNo = stbNo;
		this.scNo = scNo;
	}


	public ClientServiceData(final List<org.mifosplatform.provisioning.provisioning.data.ServiceParameterData> serviceParameterData) {
		ServiceParameterData = serviceParameterData;
	}

	public static ClientServiceData instance(Long id, Long serviceId, String serviceCode, String serviceDescription, String status){
		return new ClientServiceData(null,null,serviceCode,null,status);
	
	}
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getServiceId() {
		return serviceId;
	}


	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}


	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public String getServiceDescription() {
		return serviceDescription;
	}


	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public List<ServiceData> getServiceData() {
		return serviceData;
	}


	public void setServiceData(List<ServiceData> serviceData) {
		this.serviceData = serviceData;
	}


	public List<ServiceParameterData> getServiceParameterData() {
		return ServiceParameterData;
	}


	public void setServiceParameterData(List<ServiceParameterData> serviceParameterData) {
		ServiceParameterData = serviceParameterData;
	}

	public String getPoid() {
		return poid;
	}


	public void setPoid(String poid) {
		this.poid = poid;
	}

	//converting obrm given result to clientService Object
	public static List<ClientServiceData> fromOBRMJson(String result, List<ClientServiceData> clientServiceDatas) throws JSONException{
		List<ClientServiceData> clientserviceDatas = new ArrayList<ClientServiceData>();
		ClientServiceData clientServiceData = null;
		JSONObject object = new JSONObject(result);
		JSONObject serviceObj = null;
		object = object.optJSONObject("brm:MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist");
		JSONObject serviceInfo = object.optJSONObject("brm:SERVICE_INFO");
		JSONArray servicesArray = null;
		if(serviceInfo !=null){
			servicesArray = serviceInfo.optJSONArray("brm:SERVICES");
			if(servicesArray ==null){
				servicesArray = new JSONArray( "["+serviceInfo.optString("brm:SERVICES")+"]");
			}
		}
		for(int i=0;i<servicesArray.length();i++){
			serviceObj = servicesArray.optJSONObject(i);
			clientServiceData = new ClientServiceData();
			for(ClientServiceData cs:clientServiceDatas){
				if(/*cs.getPoid()*/"0.0.0.1 /service/catv 475006 11".equalsIgnoreCase(serviceObj.getString("brm:POID"))){
					clientServiceData.setId(cs.getId());
					clientServiceData.setServiceId(cs.getServiceId());
					clientServiceData.setServiceDescription(serviceObj.optString("brm:NAME"));
					clientServiceData.setServiceCode(serviceObj.optString("brm:NAME"));
					
					if("10100".equalsIgnoreCase(serviceObj.optString("brm:STATUS"))){
						clientServiceData.setStatus("ACTIVE");	
					}else{
						clientServiceData.setStatus("TERMINATED");	
					}
					
					clientserviceDatas.add(clientServiceData);
				}
				
			}
		}
		return clientserviceDatas;
	}
	

	//preparing input payload for obrm process request
	public String obrmRequestInput() {
		int i=0;
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		sb.append("<MSO_OP_CUST_ACTIVATE_CUSTOMER_inputFlist><ACCOUNT_NO>"+this.accountNumber+"</ACCOUNT_NO>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account 460915 0</ACCOUNT_OBJ><BILLINFO elem=\"0\"><BILL_WHEN>1</BILL_WHEN>");
		sb.append("</BILLINFO><BUSINESS_TYPE>99001100</BUSINESS_TYPE><PAYINFO elem=\"0\">");
		sb.append("<INHERITED_INFO><INV_INFO elem=\"0\"><INDICATOR>0</INDICATOR></INV_INFO></INHERITED_INFO></PAYINFO>");
		sb.append("<PLAN_LIST_CODE>");
		for(PlanData plan:this.planData){
			sb.append("<PLAN elem=\""+i+"\"><CODE>"+plan.getplanCode()+"</CODE><PLAN_OBJ>0.0.0.1 /plan "+plan.getPlanPoid()+" 2</PLAN_OBJ>");
			sb.append("<PURCHASE_START_T>1519534358</PURCHASE_START_T> </PLAN>");
			i=i+1;
		}
		i=0;
		sb.append("</PLAN_LIST_CODE>");
		sb.append("<POID>0.0.0.1 /plan -1 0</POID><PROGRAM_NAME>BULK|OAP|testcsrone</PROGRAM_NAME>");
		sb.append("<SERVICES elem=\"0\"><MSO_FLD_CATV_INFO>");
		sb.append("<MSO_FLD_BOX_INSTALLED_T>0</MSO_FLD_BOX_INSTALLED_T><MSO_FLD_CARF_RECEIVED>0</MSO_FLD_CARF_RECEIVED>");
		sb.append("<MSO_FLD_LCO_BOX_RECV_T>0</MSO_FLD_LCO_BOX_RECV_T><MSO_FLD_SALESMAN_OBJ/></MSO_FLD_CATV_INFO>");
		if(this.stbNo !=null){
			sb.append("<DEVICES elem=\""+i+"\"><MSO_FLD_STB_ID>"+this.stbNo+"</MSO_FLD_STB_ID></DEVICES>");
			i++;
		}
		if(this.scNo !=null){
			sb.append("<DEVICES elem=\""+i+"\"><MSO_FLD_VC_ID>"+this.scNo+"</MSO_FLD_VC_ID></DEVICES>");
		}
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/catv -1 0</SERVICE_OBJ>");
		sb.append("</SERVICES><USERID>0.0.0.1 /account 452699 0</USERID></MSO_OP_CUST_ACTIVATE_CUSTOMER_inputFlist>");
		
		return sb.toString();
	}


	public static ClientServiceData fromJsonForOBRM(JsonCommand command) {
		Map<String,String> serialNo = new HashMap<String,String>();
		try {
		
			JSONArray clientServiceArray = new JSONArray(command.arrayOfParameterNamed("clientServiceData").toString());
			JSONObject clientService = clientServiceArray.getJSONObject(0);
			String accountNo = clientService.optString("accountNo");
			
			JSONArray deviceArray = new JSONArray(command.arrayOfParameterNamed("deviceData").toString());
			JSONObject deviceData = deviceArray.getJSONObject(0);
			serialNo = deviceDatafromJson(deviceData,serialNo);
			serialNo = deviceDatafromJson(deviceData.optJSONObject("pairableItemDetails"),serialNo);
			
			JSONArray planArray = new JSONArray(command.arrayOfParameterNamed("planData").toString());
			List<PlanData> planDatas = new ArrayList<PlanData>();
			PlanData planData = null;
			for(int i=0;i<planArray.length();i++){
				JSONObject plan = planArray.getJSONObject(i);
				planData = new PlanData(plan.optString("planName"),plan.optString("planpoId"));
				
				planDatas.add(planData);
				
			}
			return new ClientServiceData(accountNo, planDatas, serialNo.get("stbNo"),serialNo.get("scNo"));
		
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("error.msg.parse.exception", e.getMessage(), e.getMessage(), e.getMessage());
		}
	}


	private static Map<String,String> deviceDatafromJson(JSONObject deviceData,Map<String,String> serialNo) throws JSONException {
			if(deviceData != null){
				JSONArray serialNoArray = new JSONArray(deviceData.getString("serialNumber"));
				JSONObject serialNoObject = serialNoArray.getJSONObject(0);
				if("STB".equalsIgnoreCase(serialNoObject.optString("itemType"))){
					serialNo.put("stbNo",serialNoObject.optString("serialNumber"));
				}else{
					serialNo.put("scNo",serialNoObject.optString("serialNumber"));
				}
			}	
			return serialNo;
	}


	
	
	
	
	
	
	
	


	/*public static ClientServiceData fromJson(final JsonCommand command) {
		
		final String accountNumber = command.stringValueOfParameterNamed("accountNumber");
		final String stbId = command.stringValueOfParameterNamed("stbId");
		final String vcId = command.stringValueOfParameterNamed("vcId");
		
		return new ClientServiceData(accountNumber,stbId,vcId);
		
	
	}*/


	/*public String obrmRequestInput() {
		StringBuilder cs = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><MSO_OP_CUST_ACTIVATE_CUSTOMER_inputFlist>");
		cs.append("<ACCOUNT_NO>"+this.accountNumber+"<ACCOUNT_NO>");
		cs.append("<MSO_FLD_STB_ID>"+this.stbId+"</MSO_FLD_STB_ID>");
		cs.append("<MSO_FLD_VC_ID>"+this.vcId+"<MSO_FLD_VC_ID>");
		System.out.println(cs.toString());
		return cs.toString();
	}*/
	

}
