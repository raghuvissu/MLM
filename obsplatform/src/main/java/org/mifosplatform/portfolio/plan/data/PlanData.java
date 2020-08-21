package org.mifosplatform.portfolio.plan.data;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;

public class PlanData {

	private  Long id;
	private  Long billRule;
	private  String planCode;
	private  String planDescription;
	private  LocalDate startDate;
	private  LocalDate endDate;
	private  Long status;
	private  EnumOptionData planstatus;
	private  String productDescription;
	private  Collection<ServiceData> products;
	private  Collection<ServiceData> selectedProducts;
	private List<String> contractPeriods;
	private List<SubscriptionData> subscriptiondata;
	private List<BillRuleData> billRuleDatas;
	private List<EnumOptionData> planStatus,volumeTypes;
	private  String contractPeriod;
	private PlanData datas;
	private long statusname;
	private Collection<MCodeData> provisionSysData;
	private String provisionSystem;
	private String isPrepaid;
	private String allowTopup;
	private String isHwReq;
	private String volume;
	private String units;
	private String unitType;
	private Long contractId;
	private Boolean isActive=false;
	//private Integer planCount = 0;
	private List<PlanData> data = null;
	private int planCount;
	private boolean ordersFlag;
	
	private Collection<MCodeData> planTypeData;
	private Long planType;
	private String planTypeName;
	//private ApplicationCurrencyConfigurationData currencyData;
	private ApplicationCurrencyConfigurationData currencydata;
	private Long currencyId;
	private String currencyCode;
	private String serviceCode;
	
	private String planPoid;
	private String productPoid;
	private String dealPoid;
	
	public PlanData() {
	}


	
	
	public PlanData(final String planCode, final String planPoid) {
		this.planCode = planCode;
		this.planPoid = planPoid;
	}




	public PlanData(Collection<ServiceData> data, List<BillRuleData> billData,List<SubscriptionData> contractPeriod, List<EnumOptionData> status,
			PlanData datas, Collection<ServiceData> selectedProducts,Collection<MCodeData> provisionSysData, List<EnumOptionData> volumeType,
			Collection<MCodeData> planTypeData,ApplicationCurrencyConfigurationData currencydata) {
	
		if(datas!=null){
		this.id = datas.getId();
		this.planCode = datas.getplanCode();
		this.subscriptiondata = contractPeriod;
		this.startDate = datas.getStartDate();
		this.status = datas.getStatus();
		this.billRule = datas.getBillRule();
		this.endDate = datas.getEndDate();
		this.planDescription = datas.getplanDescription();
		this.provisionSystem=datas.getProvisionSystem();
		this.isPrepaid=datas.getIsPrepaid();
		this.allowTopup=datas.getAllowTopup();
		this.volume=datas.getVolume();
		this.units=datas.getUnits();
		this.unitType=datas.getUnitType();
		this.contractPeriod=datas.getPeriod();
		this.isHwReq=datas.getIsHwReq();
		this.planType=datas.getPlanType();
		this.planTypeName=datas.getPlanTypeName();
		this.currencyId = datas.getCurrencyId();
		this.currencyCode = datas.getCurrencyCode();
		
		}
		this.products = data;
        this.provisionSysData=provisionSysData;
		this.selectedProducts = selectedProducts;
		this.billRuleDatas = billData;
		this.subscriptiondata=contractPeriod;
		this.planStatus = status;
		this.productDescription = null;
		
		this.datas = datas;
		//this.datas = null;
		this.volumeTypes=volumeType;
		this.planTypeData = planTypeData;
		this.currencydata = currencydata;

	}

	
	public PlanData(Long id, String planCode, LocalDate startDate,LocalDate endDate, Long bill_rule, String contractPeriod,
			long status, String planDescription,String provisionSys,EnumOptionData enumstatus, String isPrepaid,
			String allowTopup, String volume, String units, String unitType, Collection<ServiceData> products, Long contractId, String isHwReq,Long count, Long planType,
			String planTypeName,Long currencyId,String currencyCode) {

		this.id = id;
		this.planCode = planCode;
		this.productDescription = null;
		this.startDate = startDate;
		this.status = status;
		this.billRule = bill_rule;
		this.endDate = endDate;
		this.planDescription = planDescription;
		//this.services = null;
		this.billRuleDatas = null;
		this.contractPeriod = contractPeriod;
        this.provisionSystem=provisionSys;  
		this.selectedProducts = null;
		this.planstatus = enumstatus;
		this.isPrepaid=isPrepaid;
		this.allowTopup=allowTopup;
		this.volume=volume;
		this.units=units;
		this.unitType=unitType;
		this.isHwReq=isHwReq;
		this.products=products;
		this.contractId=contractId;
		this.ordersFlag=(count>0)?true:false;
		this.planType=planType;
		this.planTypeName=planTypeName;
		this.currencyId=currencyId;
		this.currencyCode=currencyCode;
	}

	
	
	public PlanData(Long id, String planCode, LocalDate startDate,LocalDate endDate, Long bill_rule, 
			long status, String planDescription,String provisionSys, String isHwReq, Long planType,
			String planPoid,String serviceCode, String dealPoid, String productPoid) {

		this.id = id;
		this.planCode = planCode;
		this.startDate = startDate;
		this.status = status;
		this.billRule = bill_rule;
		this.endDate = endDate;
		this.planDescription = planDescription;
		this.provisionSystem=provisionSys;  
		this.isHwReq=isHwReq;
		this.planType=planType;
		this.planPoid=planPoid;
		this.serviceCode=serviceCode;
		this.dealPoid=dealPoid;
		this.productPoid=productPoid;
	}

	public PlanData(final Long id, final String planCode, final String planDescription) {
		this.id = id;
		this.planCode = planCode;
		this.planDescription = planDescription;
	}

	public PlanData(final Long id){
		this.id=id;
	}
	
	public Long getPlanType() {
		return planType;
	}


	public List<String> getContractPeriods() {
		return contractPeriods;
	}


	public String getIsHwReq() {
		return isHwReq;
	}


	public Long getContractId() {
		return contractId;
	}


	public List<PlanData> getData() {
		return data;
	}


	public PlanData(List<PlanData> datas) {
		this.data = datas;
	}

	public PlanData(Long id, String planCode, String planDescription, String isPrepaid) {
              this.id=id;
              this.planCode=planCode;
              this.planDescription=planDescription;
              this.isPrepaid = isPrepaid;
	}


	public String getProvisionSystem() {
		return provisionSystem;
	}


	public EnumOptionData getPlanstatus() {
		return planstatus;
	}

	public PlanData getDatas() {
		return datas;
	}

	public Collection<ServiceData> getSelectedProducts() {
		return selectedProducts;
	}

	public long getStatusname() {
		return statusname;
	}

	public List<EnumOptionData> getPlanStatus() {
		return planStatus;
	}

	public String getPeriod() {
		return contractPeriod;
	}

	public void setContractPeriod(List<String> contractPeriod) {
		this.contractPeriods = contractPeriod;
	}

	public List<BillRuleData> getBillRuleData() {
		return billRuleDatas;
	}

	public Long getId() {
		return id;
	}

	public String getplanCode() {
		return planCode;
	}

	public String getplanDescription() {
		return planDescription;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public Long getStatus() {
		return status;
	}

	public Collection<ServiceData> getServicedata() {
		return products;
	}

	public Long getBillRule() {
		return billRule;
	}

	public List<String> getContractPeriod() {
		return contractPeriods;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public List<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}


	/**
	 * @return the planCode
	 */
	public String getPlanCode() {
		return planCode;
	}


	/**
	 * @return the planDescription
	 */
	public String getPlanDescription() {
		return planDescription;
	}


	/**
	 * @return the serviceDescription
	 */
	public String getProductDescriptionn() {
		return productDescription;
	}


	/**
	 * @return the services
	 */
	public Collection<ServiceData> getServices() {
		return products;
	}


	/**
	 * @return the billRuleDatas
	 */
	public List<BillRuleData> getBillRuleDatas() {
		return billRuleDatas;
	}


	/**
	 * @return the volumeTypes
	 */
	public List<EnumOptionData> getVolumeTypes() {
		return volumeTypes;
	}


	/**
	 * @return the provisionSysData
	 */
	public Collection<MCodeData> getProvisionSysData() {
		return provisionSysData;
	}


	public Collection<MCodeData> getPlanTypeData() {
		return planTypeData;
	}


	/**
	 * @return the isPrepaid
	 */
	public String getIsPrepaid() {
		return isPrepaid;
	}


	/**
	 * @return the allowTopup
	 */
	public String getAllowTopup() {
		return allowTopup;
	}


	/**
	 * @return the volume
	 */
	public String getVolume() {
		return volume;
	}


	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}


	/**
	 * @return the unitType
	 */
	public String getUnitType() {
		return unitType;
	}


	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}


	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}




	public void setProducts(List<ServiceData> products) {
		
		this.products=products;
	}


	public void setPlanCount(int size) {
		this.planCount=size;
		
	}


	public String getPlanTypeName() {
		return planTypeName;
	}


	public void setPlanTypeName(String planTypeName) {
		this.planTypeName = planTypeName;
	}


	public Long getCurrencyId() {
		return currencyId;
	}


	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}


	public String getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public void setSelectedProducts(Collection<ServiceData> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setBillRule(Long billRule) {
		this.billRule = billRule;
	}


	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}


	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}


	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}


	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public void setStatus(Long status) {
		this.status = status;
	}


	public void setPlanstatus(EnumOptionData planstatus) {
		this.planstatus = planstatus;
	}


	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}


	public void setProducts(Collection<ServiceData> products) {
		this.products = products;
	}


	public void setContractPeriods(List<String> contractPeriods) {
		this.contractPeriods = contractPeriods;
	}


	public void setSubscriptiondata(List<SubscriptionData> subscriptiondata) {
		this.subscriptiondata = subscriptiondata;
	}


	public void setBillRuleDatas(List<BillRuleData> billRuleDatas) {
		this.billRuleDatas = billRuleDatas;
	}


	public void setPlanStatus(List<EnumOptionData> planStatus) {
		this.planStatus = planStatus;
	}


	public void setVolumeTypes(List<EnumOptionData> volumeTypes) {
		this.volumeTypes = volumeTypes;
	}


	public void setContractPeriod(String contractPeriod) {
		this.contractPeriod = contractPeriod;
	}


	public void setDatas(PlanData datas) {
		this.datas = datas;
	}


	public void setStatusname(long statusname) {
		this.statusname = statusname;
	}


	public void setProvisionSysData(Collection<MCodeData> provisionSysData) {
		this.provisionSysData = provisionSysData;
	}


	public void setProvisionSystem(String provisionSystem) {
		this.provisionSystem = provisionSystem;
	}


	public void setIsPrepaid(String isPrepaid) {
		this.isPrepaid = isPrepaid;
	}


	public void setAllowTopup(String allowTopup) {
		this.allowTopup = allowTopup;
	}


	public void setIsHwReq(String isHwReq) {
		this.isHwReq = isHwReq;
	}


	public void setVolume(String volume) {
		this.volume = volume;
	}


	public void setUnits(String units) {
		this.units = units;
	}


	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}


	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}


	public void setData(List<PlanData> data) {
		this.data = data;
	}


	public void setOrdersFlag(boolean ordersFlag) {
		this.ordersFlag = ordersFlag;
	}


	public void setPlanTypeData(Collection<MCodeData> planTypeData) {
		this.planTypeData = planTypeData;
	}


	public void setPlanType(Long planType) {
		this.planType = planType;
	}


	public void setCurrencydata(ApplicationCurrencyConfigurationData currencydata) {
		this.currencydata = currencydata;
	}
	
	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public String getPlanPoid() {
		return planPoid;
	}


	public void setPlanPoid(String planPoid) {
		this.planPoid = planPoid;
	}


	public String getProductPoid() {
		return productPoid;
	}


	public void setProductPoid(String productPoid) {
		this.productPoid = productPoid;
	}


	public String getDealPoid() {
		return dealPoid;
	}


	public void setDealPoid(String dealPoid) {
		this.dealPoid = dealPoid;
	}

	
	
public static String obrmRequestInput(String accountNo,String poId,String userId) {
		
	StringBuffer s = new StringBuffer("<MSO_OP_CUST_GET_PLAN_DETAILS_inputFlist>"); 
    s.append("<MSO_FLD_DEVICE_TYPE>0</MSO_FLD_DEVICE_TYPE>");
    s.append("<MSO_FLD_PAYMENT_TYPE>0</MSO_FLD_PAYMENT_TYPE>");
    s.append("<MSO_FLD_PLAN_CATEGORY>1</MSO_FLD_PLAN_CATEGORY>");
    s.append("<MSO_FLD_PLAN_SUB_TYPE/>");
    s.append("<MSO_FLD_PLAN_TYPE>2</MSO_FLD_PLAN_TYPE>");
	s.append("<CITY>DORAHA</CITY>");
	s.append("<POID>0.0.0.1 /plan -1 0</POID>");
	s.append("<PROGRAM_NAME>OAP|csradmin</PROGRAM_NAME>");
	s.append("<SEARCH_KEY>0</SEARCH_KEY>");
	s.append("<SEARCH_TYPE>3</SEARCH_TYPE>");
	s.append("<TYPE_OF_SERVICE>1</TYPE_OF_SERVICE>");
	s.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
	s.append("<VALUE>%</VALUE>");
	s.append("</MSO_OP_CUST_GET_PLAN_DETAILS_inputFlist>");
	
		return s.toString();
	}
	public static PlanData fromJson(String result, PlanData planData){
		try{
			JSONObject object = new JSONObject(result);
			object=object.getJSONObject("brm:MSO_OP_CUST_GET_PLAN_DETAILS_outputFlist");
			
		}
		catch(Exception e ){
			return null;
		}
		
		
		return null;
	}

}
