/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.order.data.OrderData;

/**
 * Immutable data object representing client data.
 */
final public class ClientData implements Comparable<ClientData> {

    private final Long id;
    private String accountNo;
    private final String externalId;

    private final EnumOptionData status;
    private final Boolean active;
    private final LocalDate activationDate;

    private final String firstname;
    private final String middlename;
    private final String lastname;
    private final String fullname;
    private String displayName;

    private final Long officeId;
    private final String officeName;

    private final String imageKey;
    @SuppressWarnings("unused")
    private final Boolean imagePresent;
    private final String email;
    private final String phone;
    private final String homePhoneNumber;
    private final String addressNo;
    private final String street;
    private final String city;
    private final String state;
    private final String country;
    private final String zip;
    private final BigDecimal balanceAmount;
    private final BigDecimal walletAmount;
    private final String hwSerialNumber;
    private final String taxExemption;
    private final String groupName;
    private final BigDecimal paidAmount;
    private final BigDecimal lastBillAmount;
    private final Date lastPaymentDate;
    // associations
    private final Collection<GroupGeneralData> groups;

    // template
    private final Collection<OfficeData> officeOptions;
    private final Collection<ClientCategoryData> clientCategoryDatas;
	private final String categoryType;
	private AddressData addressTemplateData;
    private final List<String> hardwareDetails;
    private PaymentGatewayConfiguration configurationProperty;
    private Configuration loginConfigurationProperty;
    private PaymentGatewayConfiguration configurationPropertyforIos;
	private  final String currency;

	private final Collection<GroupData> groupNameDatas;
    private final Collection<CodeValueData> closureReasons;
    private Boolean balanceCheck;
    private final String  entryType;
    private SelfCare selfcare;
    private final String userName;
    private final String clientPassword;
    private final String title;
    private final String parentId;
    private String officePOID;
	private ClientAdditionalData clientAdditionalData;
	private List<ClientServiceData> clientServiceData;
   	private List<OrderData> orderData;
   	private List<OneTimeSaleData> oneTimeSaleData;
   	private String poId;
    Collection<MCodeData> businessTypes;
	private Collection<MCodeData> preferences;
    private String billMode;
    private Long orderId;
    private String stbId;
    private Date startDate;
    private Date endDate;
	private String externalCode;
    
	public static ClientData template(final Long officeId, final LocalDate joinedDate, final Collection<OfficeData> officeOptions,
    		Collection<ClientCategoryData> categoryDatas,Collection<GroupData> groupDatas,List<CodeValueData> closureReasons) {
        return new ClientData(null, null,null, officeId, null, null, null, null, null, null, null, null, joinedDate, null, officeOptions, null,
        		categoryDatas,null,null,null, null, null, null, null, null, null, null,null,null,null,null,groupDatas,closureReasons,null,null,null,null,
        		 null,null,null,null,null,null,null, null,null,null,null, null);
        }
	
    public static ClientData template(final Long officeId, final LocalDate joinedDate, final Collection<OfficeData> officeOptions, Collection<ClientCategoryData> categoryDatas,
    		List<CodeValueData> closureReasons) {
        return new ClientData(null, null,null, officeId, null, null, null, null, null, null, null, null, joinedDate, null, officeOptions, null,
        		categoryDatas,null,null,null, null, null, null, null, null, null, null,null,null,null,null,null,closureReasons,null,null,null,null,null,null,
        		null,null,null,null,null,null,null,null,null, null);
    }

    public static ClientData templateOnTop(final ClientData clientData, final List<OfficeData> allowedOffices, Collection<ClientCategoryData> categoryDatas,
    		Collection<GroupData> groupDatas, List<String> allocationDetailsDatas, String balanceCheck) {


        return new ClientData(clientData.accountNo,clientData.groupName, clientData.status, clientData.officeId, clientData.officeName, clientData.id,
                clientData.firstname, clientData.middlename, clientData.lastname, clientData.fullname, clientData.displayName,
                clientData.externalId, clientData.activationDate, clientData.imageKey, allowedOffices, clientData.groups,
                categoryDatas,clientData.categoryType,clientData.email,clientData.phone,clientData.homePhoneNumber,clientData.addressNo,clientData.street,
                clientData.city,clientData.state,clientData.country,clientData.zip,clientData.balanceAmount,allocationDetailsDatas,clientData.hwSerialNumber,
                clientData.currency, groupDatas,null,balanceCheck,clientData.taxExemption,clientData.entryType,clientData.walletAmount,null,null,null,
                clientData.title,clientData.paidAmount,clientData.lastBillAmount,clientData.lastPaymentDate,null,null,null,null, clientData.externalCode);
    }

    public static ClientData setParentGroups(final ClientData clientData, final Collection<GroupGeneralData> parentGroups) {
        return new ClientData(clientData.accountNo,clientData.groupName, clientData.status, clientData.officeId, clientData.officeName, clientData.id,

                clientData.firstname, clientData.middlename, clientData.lastname, clientData.fullname, clientData.displayName,
                clientData.externalId, clientData.activationDate, clientData.imageKey, clientData.officeOptions, parentGroups,
                clientData.clientCategoryDatas,clientData.categoryType,clientData.email,clientData.phone,clientData.homePhoneNumber,
                clientData.addressNo,clientData.street,clientData.city,clientData.state,clientData.country,clientData.zip,clientData.balanceAmount,
                clientData.hardwareDetails,clientData.hwSerialNumber,clientData.currency, clientData.groupNameDatas,null,null,clientData.taxExemption,clientData.entryType,
                clientData.walletAmount,null,null,null,clientData.title,clientData.paidAmount,clientData.lastBillAmount,clientData.lastPaymentDate,null,null,null,null,
                clientData.externalCode);

    }

    public static ClientData clientIdentifier(final Long id, final String accountNo, final EnumOptionData status, final String firstname,
            final String middlename, final String lastname, final String fullname, final String displayName, final Long officeId,
            final String officeName) {

        return new ClientData(accountNo,null, status, officeId, officeName, id, firstname, middlename, lastname, fullname, displayName, null,
                null, null, null, null,null,null,null,null, null,null, null,null, null, null,null,null,null,null,null, null,null,null,null,null,
                null,null,null,null,null,null,null,null,null,null,null,null, null);
    }

    public static ClientData lookup(final Long id, final String displayName, final Long officeId, final String officeName) {
        return new ClientData(null,null, null, officeId, officeName, id, null, null, null, null, displayName, null, null, null, null, null,null,null,null,null,
        		null,null,null, null,null,null,null,null,null,null,null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null, null);

    }
    
    public static ClientData walletAmount(final Long id, final String accountNo, final BigDecimal walletAmount,final String hwSerialNumber) {
    	return new ClientData(accountNo,null, null, null, null, id, null, null, null, null, null, null, null, null, null, null,null,null,null,null,
    			null,null,null, null,null,null,null,null,null,hwSerialNumber,null, null,null,null,null,null,walletAmount,null,null,null,null,null,null,
    			null,null,null,null,null, null);
    	
    }

    
    
    public static ClientData instance(final String accountNo, final String groupName, final EnumOptionData status, final Long officeId, final String officeName,final Long id, 
    		final String firstname, final String middlename, final String lastname, final String fullname,final String displayName, final String externalId,
    		final LocalDate activationDate, final String imageKey,final String categoryType,final String email,final String phone,final String homePhoneNumber,final String addrNo,final String street,
    		final String city,final String state,final String country,final String zip,final BigDecimal balanceAmount,final String hwSerialNumber,final String currency,final String taxExemption,
    		String entryType,final BigDecimal walletAmount,final String userName,final String clientPassword,final String parentId, String title,final BigDecimal paidAmount,
    		final BigDecimal lastBillAmount,final Date lastPaymentDate, final String externalCode) {
    	
        return new ClientData(accountNo,groupName, status, officeId, officeName, id, firstname, middlename, lastname, fullname, displayName,
                externalId, activationDate, imageKey, null, null,null,categoryType,email,phone,homePhoneNumber,addrNo,street,city,state,country,zip,
                balanceAmount,null,hwSerialNumber,currency, null,null,null,taxExemption,entryType,walletAmount,userName,clientPassword,parentId,title,paidAmount,
                lastBillAmount,lastPaymentDate,null,null,null,null, externalCode);

    }
    
    public static ClientData temperory(final String groupName, final EnumOptionData status, final Long officeId, final String officeName,final Long id, 
    		final LocalDate activationDate, final String imageKey,final String poId,String accountNo) {
    	
    	ClientData data = new ClientData(accountNo,groupName, status, officeId, officeName, id, null, null, null, null, null, null, activationDate, imageKey, null, null,null,null,null,null,
        		null,null,null,null,null,null,null, null,null,null,null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null, null);
    	data.setPoId(poId);
    	
        return data;
    }
    
    
    public static ClientData searchClient(final Long id) {
    	return new ClientData(null,null, null, null, null, id, null, null, null, null, null, null, null, null, null, null,null,null,null,null,
    			null,null,null, null,null,null,null,null,null,null,null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null, null);
    	
    }
    
    public static ClientData lcoClient(final Long id,final String accountNo,final String displayName,final String phone,
    		final BigDecimal clientBalance,final Long orderId,final String stbId,
			final Date startDate,final Date endDate) {
    	return new ClientData(accountNo,null, null, null, null, id, null, null, null, null, displayName, null, null, null, null, null,null,null,null,phone,
    			null,null,null, null,null,null,null,clientBalance,null,null,null, null,null,null,null,null,null,null,null,null,null,null,null,null,
    			orderId, stbId, startDate, endDate, null);
    	
    }
    private ClientData(final String accountNo,final String groupName, final EnumOptionData status, final Long officeId, final String officeName, final Long id,final String firstname,
    		final String middlename, final String lastname, final String fullname, final String displayName,final String externalId, final LocalDate activationDate, 
    		final String imageKey, final Collection<OfficeData> allowedOffices,final Collection<GroupGeneralData> groups, Collection<ClientCategoryData> clientCategoryDatas,
    		final String categoryType,final String email,final String phone,final String homePhoneNumber,final String addrNo,final String street,final String city,final String state,
    		final String country,final String zip, BigDecimal balanceAmount,final List<String> hardwareDetails,final String hwSerialNumber,final String currency, Collection<GroupData> groupNameDatas, 
    		List<CodeValueData> closureReasons, String balanceCheck,final String taxExemption, String entryType,final BigDecimal walletAmount,final String userName,final String clientPassword,
    		final String parentId,final String title,final BigDecimal paidAmount,final BigDecimal lastBillAmount,final Date lastPaymentDate,
    		final Long orderId ,final String stbId, final Date startDate, final Date endDate, final String externalCode) {
    	
    	

        this.accountNo = accountNo;
        this.groupName=groupName;
        this.status = status;
        if (status != null) {
            active = status.getId().equals(300L);
        } else {
            active = null;
        }
        this.officeId = officeId;
        this.officeName = officeName;
        this.id = id;
        this.firstname = StringUtils.defaultIfEmpty(firstname, null);
        this.middlename = StringUtils.defaultIfEmpty(middlename, null);
        this.lastname = StringUtils.defaultIfEmpty(lastname, null);
        this.fullname = StringUtils.defaultIfEmpty(fullname, null);
        this.displayName = StringUtils.defaultIfEmpty(displayName, null);
        this.externalId = StringUtils.defaultIfEmpty(externalId, null);
        this.activationDate = activationDate;
        this.walletAmount=walletAmount;
        this.imageKey = imageKey;
        this.title = title;
        this.paidAmount=paidAmount;
        this.lastBillAmount=lastBillAmount;
        this.lastPaymentDate=lastPaymentDate;
        if (imageKey != null) {
            this.imagePresent = Boolean.TRUE;
        } else {
            this.imagePresent = null;
        }
        this.closureReasons=closureReasons;

        // associations
        this.groups = groups;

        // template
        this.officeOptions = allowedOffices;
        this.clientCategoryDatas=clientCategoryDatas;
        this.groupNameDatas = groupNameDatas;
        this.categoryType=categoryType;
        this.email=email;
        this.phone=phone;
        this.homePhoneNumber=homePhoneNumber;
        this.addressNo= StringUtils.defaultIfEmpty(addrNo, null);
        this.street= StringUtils.defaultIfEmpty(street, null);
        this.city= StringUtils.defaultIfEmpty(city, null);
        this.state= StringUtils.defaultIfEmpty(state, null);
        this.country= StringUtils.defaultIfEmpty(country, null);
        this.zip= StringUtils.defaultIfEmpty(zip, null);
        if(balanceAmount==null){
        	balanceAmount=BigDecimal.ZERO;
		}
        this.balanceAmount=balanceAmount!=null?balanceAmount:BigDecimal.ZERO;
        this.hardwareDetails=hardwareDetails;
        this.hwSerialNumber=hwSerialNumber;
        this.currency=currency;
        this.taxExemption=taxExemption;
        this.entryType=entryType;
        if(balanceCheck !=null && balanceCheck.equalsIgnoreCase("Y")){
    	   this.setBalanceCheck(true);
        }
        else{
    	   this.setBalanceCheck(false);
        }
        this.userName = userName;
        this.clientPassword = clientPassword;
        this.parentId = parentId;
        this.preferences =preferences;
        this.orderId=orderId;
        this.stbId=stbId;
        this.startDate=startDate;
        this.endDate=endDate;
        this.externalCode = externalCode;
    }
      
	public Long id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String officeName() {
        return this.officeName;
    }

    public String imageKey() {
        return this.imageKey;
    }

    public boolean imageKeyDoesNotExist() {
        return !imageKeyExists();
    }

    
    public Boolean isActive() {
		return active;
	}

	private boolean imageKeyExists() {
        return StringUtils.isNotBlank(this.imageKey);
    }

    @Override
    public int compareTo(final ClientData obj) {
        if (obj == null) { return -1; }
        return new CompareToBuilder() //
                .append(this.id, obj.id) //
                .append(this.displayName, obj.displayName) //
                .toComparison();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        ClientData rhs = (ClientData) obj;
        return new EqualsBuilder() //
                .append(this.id, rhs.id) //
                .append(this.displayName, rhs.displayName) //
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37) //
                .append(this.id) //
                .append(this.displayName) //
                .toHashCode();
    }

    // TODO - kw - look into removing usage of the getters below
    public String getExternalId() {
        return this.externalId;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }

	public void setAddressTemplate(AddressData data) {
		this.setAddressTemplateData(data);
		
	}

	public PaymentGatewayConfiguration getConfigurationProperty() {
		return configurationProperty;
	}

	public void setConfigurationProperty(PaymentGatewayConfiguration paypalconfigurationProperty) {
		this.configurationProperty = paypalconfigurationProperty;
	}
	public void setConfigurationPropertyForIos(PaymentGatewayConfiguration paypalconfigurationPropertyForIos) {
		this.setConfigurationPropertyforIos(paypalconfigurationPropertyForIos);
	}

	public void setBalanceCheck(boolean isEnabled) {

		this.balanceCheck=isEnabled;
	}

	public SelfCare getSelfcare() {
		return selfcare;
	}

	public void setSelfcare(SelfCare selfcare) {
		this.selfcare = selfcare;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public AddressData getAddressTemplateData() {
		return addressTemplateData;
	}

	public void setAddressTemplateData(AddressData addressTemplateData) {
		this.addressTemplateData = addressTemplateData;
	}

	public PaymentGatewayConfiguration getConfigurationPropertyforIos() {
		return configurationPropertyforIos;
	}

	public void setConfigurationPropertyforIos(
			PaymentGatewayConfiguration paypalconfigurationPropertyForIos) {
		this.configurationPropertyforIos = paypalconfigurationPropertyForIos;
	}

	public Collection<CodeValueData> getClosureReasons() {
		return closureReasons;
	}

	public Boolean getBalanceCheck() {
		return balanceCheck;
	}

	public void setBalanceCheck(Boolean balanceCheck) {
		this.balanceCheck = balanceCheck;
	}

	public void setConfigurationProperty(Configuration configurationProperty) {
		this.loginConfigurationProperty=configurationProperty;
		
	}

	public void setClientAdditionalData(ClientAdditionalData clientAdditionalData) {

		  this.clientAdditionalData = clientAdditionalData;
	}
	
	

	public String getGroupName() {
		return groupName;
	}
	
	public Long getId() {
		return id;
	}

	
	public Long getOfficeId() {
		return officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public EnumOptionData getStatus() {
		return status;
	}

	public String getOfficePOID() {
		return officePOID;
	}

	
	public Configuration getLoginConfigurationProperty() {
		return loginConfigurationProperty;
	}

	public void setLoginConfigurationProperty(Configuration loginConfigurationProperty) {
		this.loginConfigurationProperty = loginConfigurationProperty;
	}

	public Boolean getActive() {
		return active;
	}

	public String getMiddlename() {
		return middlename;
	}

	public String getFullname() {
		return fullname;
	}

	public String getImageKey() {
		return imageKey;
	}

	public Boolean getImagePresent() {
		return imagePresent;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getHomePhoneNumber() {
		return homePhoneNumber;
	}

	public String getAddressNo() {
		return addressNo;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getZip() {
		return zip;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public BigDecimal getWalletAmount() {
		return walletAmount;
	}

	public String getHwSerialNumber() {
		return hwSerialNumber;
	}

	public String getTaxExemption() {
		return taxExemption;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public BigDecimal getLastBillAmount() {
		return lastBillAmount;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public Collection<GroupGeneralData> getGroups() {
		return groups;
	}

	public Collection<OfficeData> getOfficeOptions() {
		return officeOptions;
	}

	public Collection<ClientCategoryData> getClientCategoryDatas() {
		return clientCategoryDatas;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public List<String> getHardwareDetails() {
		return hardwareDetails;
	}

	public String getCurrency() {
		return currency;
	}

	public Collection<GroupData> getGroupNameDatas() {
		return groupNameDatas;
	}

	public String getEntryType() {
		return entryType;
	}

	public String getUserName() {
		return userName;
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public String getTitle() {
		return title;
	}

	public String getParentId() {
		return parentId;
	}

	public ClientAdditionalData getClientAdditionalData() {
		return clientAdditionalData;
	}

	public void setOfficePOID(String officePOID) {
		this.officePOID = officePOID;
	}
	
	public List<ClientServiceData> getClientServiceData() {
		return clientServiceData;
	}

	public void setClientServiceData(List<ClientServiceData> clientServiceData) {
		this.clientServiceData = clientServiceData;
	}
	
	public List<OrderData> getOrderData() {
			return orderData;
		}

	public void setOrderData(List<OrderData> orderData) {
			this.orderData = orderData;
		}
		

	public List<OneTimeSaleData> getOneTimeSaleData() {
			return oneTimeSaleData;
		}

	public void setOneTimeSaleData(List<OneTimeSaleData> oneTimeSaleData) {
			this.oneTimeSaleData = oneTimeSaleData;
		}
	
	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getBillMode() {
		return billMode;
	}

	public void setBillMode(String billMode) {
		this.billMode = billMode;
	}
	

	public String obrmRequestInput() {
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		stringBuffer.append("<MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>");
		stringBuffer.append("<ACCOUNT_NO>"+this.accountNo+"</ACCOUNT_NO>");
		//stringBuffer.append("<ACCOUNT_NO>CR-"+1080534620+"</ACCOUNT_NO>");
		stringBuffer.append("<FLAGS>0</FLAGS>");
		stringBuffer.append("<PROGRAM_NAME>OAP|csradmin</PROGRAM_NAME>");
		//stringBuffer.append("<POID>0.0.0.1 / "+this.poId+" 0</POID>");
		stringBuffer.append("<POID>0.0.0.1 / 0 0</POID>");
		
		//stringBuffer.append("<USERID>"+userId+"</USERID>");
		stringBuffer.append("<USERID>0.0.0.1 /account 416152 8</USERID>");
		stringBuffer.append("</MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>");
		return stringBuffer.toString();
	}
 
	public static ClientData fromJson(String result,ClientData clientData) throws JSONException {
		
		String firstname=null,middlename=null,lastname =null,email=null ,
		phone=null, homePhoneNumber=null , addressNo=null , city=null ,
		state=null ,country=null,zipcode =null,fullname=null ,displayName=null,
		entryType =null,title = null;BigDecimal balanceAmount = null;
		
		try{
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist");
			JSONObject nameInfoObj = null;
			if(object.optJSONArray("brm:NAMEINFO")!=null){
				nameInfoObj = object.optJSONArray("brm:NAMEINFO").optJSONObject(0);
			}else{
				JSONArray array = new JSONArray("["+object.getString("brm:NAMEINFO")+"]");
				nameInfoObj = array.optJSONObject(0);
			}
			JSONObject creditProfInfoObject  = object.optJSONObject("brm:MSO_FLD_CREDIT_PROFILE");
			JSONObject officeInfoObject = object.optJSONObject("brm:MSO_FLD_ORG_STRUCTURE");
			
			//from client
			final String groupName=clientData.getGroupName(); 
			final Long id = clientData.getId(); 
			final EnumOptionData status = clientData.getStatus();
			final LocalDate activationDate = clientData.getActivationDate();
    		final String imageKey =clientData.getImageKey();
    		final String userName = clientData.getUserName();
    		final String clientpassword = clientData.getClientPassword();
    		final String parentId = clientData.getParentId();
    		
    		Long officeId=Long.valueOf(0);String officeName = null;
			/*if(clientData.getOfficePOID().equalsIgnoreCase(object.getString("brm:PARENT"))){
				officeId = clientData.getOfficeId(); 
				officeName = clientData.getOfficeName();
			}*/
    		
    		if(officeInfoObject!=null){
				if("0.0.0.1 /account 104790 7".equalsIgnoreCase(officeInfoObject.optString("brm:PARENT"))){
					officeId = clientData.getOfficeId(); 
					officeName = clientData.getOfficeName();
				}
    		}
			//from object
			final String accountNo =object.optString("brm:ACCOUNT_NO"); 
			final String externalId =object.optString("brm:POID");
			final String categoryType = object.optString("brm:BUSINESS_TYPE");
			
			
			if(nameInfoObj !=null){
	    		firstname = nameInfoObj.optString("brm:FIRST_NAME");
	    		middlename = nameInfoObj.optString("brm:MIDDLE_NAME") ;
	    		lastname = nameInfoObj.optString("brm:LAST_NAME");
	    		email = nameInfoObj.optString("brm:EMAIL_ADDR");
	    		phone = nameInfoObj.optJSONObject("brm:PHONES").getString("brm:PHONE");
	    		homePhoneNumber = phone;
	    		addressNo = nameInfoObj.optString("brm:ADDRESS");
	    		city = nameInfoObj.optString("brm:CITY");
	    		state = nameInfoObj.optString("brm:STATE");
	    		country = nameInfoObj.optString("brm:COUNTRY");
	    		zipcode = nameInfoObj.optString("brm:ZIP");
	    		fullname =  (new StringBuilder(firstname+" "+lastname)).toString();
	    		displayName = fullname;
	    		entryType =nameInfoObj.optString("brm:COUNTRY");
	    		title = nameInfoObj.optString("brm:SALUTATION");
			}
    		
    		final String street = city;
    	
    		if(creditProfInfoObject!=null){
    			balanceAmount = BigDecimal.valueOf(creditProfInfoObject.optLong("brm:CURRENT_BAL"));
    		}
    			
    		final String hwSerialNumber = null;
    		final String currency = null;
    		final String taxExemption = null;
    		final BigDecimal walletAmount = null;
    		final BigDecimal paidAmount = null;
    		final BigDecimal lastBillAmount = null;
    		final Date lastPaymentDate = null;
    		final String externalCode = null;
			
			return ClientData.instance(accountNo,groupName, status, officeId, officeName, id, firstname, middlename, lastname, fullname, displayName,
                    externalId, activationDate, imageKey,categoryType,email,phone,homePhoneNumber, addressNo, street, city, state, country, zipcode,
                    balanceAmount,hwSerialNumber,currency,taxExemption,entryType,walletAmount,userName,clientpassword,parentId,title,paidAmount,
                    lastBillAmount,lastPaymentDate, externalCode);
		}catch(Exception e){
			return null;
		}
	}



	
	
	/*public Collection<MCodeData> getPreferences() {
		return preferences;
	}*/

	public void setPreferences(Collection<MCodeData> prefernces) {
		this.preferences = prefernces;
	}
}
	
