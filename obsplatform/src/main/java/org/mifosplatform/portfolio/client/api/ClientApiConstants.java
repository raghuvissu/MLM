package org.mifosplatform.portfolio.client.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mifosplatform.portfolio.client.data.ClientData;

public class ClientApiConstants {

    public static final String CLIENT_RESOURCE_NAME = "client";
    public static final String CLIENT_CLOSURE_REASON = "ClientClosureReason";
    // general
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w\\-]+\\.)+[\\w]+[\\w]$";

    // request parameters
    public static final String idParamName = "id";
    public static final String titleParamName = "title";
    public static final String groupIdParamName = "groupId";
    public static final String accountNoParamName = "accountNo";
    public static final String externalIdParamName = "externalId";
    public static final String firstnameParamName = "firstname";
    public static final String middlenameParamName = "middlename";
    public static final String lastnameParamName = "lastname";
    public static final String fullnameParamName = "fullname";
    public static final String officeIdParamName = "officeId";
    public static final String activeParamName = "active";
    public static final String activationDateParamName = "activationDate";
    public static final String clientCategoryParamName ="clientCategory";
    public static final String addressNoParamName="addressNo";
    public static final String cityParamName="city";
    public static final String countryParamName="country";
    public static final String emailParamName="email";
    public static final String phoneParamName="phone";
    public static final String homePhoneNumberParamName="homePhoneNumber";
    public static final String stateParamName="state";
    public static final String streetParamName="street";
    public static final String zipCodeParamName="zipCode";
    public static final String balanceParamName="balanceAmount";
    public static final String loginParamName="login";
    public static final String passwordParamName="password";
    public static final String flagParamName="flag";
    public static final String groupParamName = "groupId";
    public static final String entryTypeParamName = "entryType";
    public static final String deviceParamName = "device";
    public static final String userNameParamName = "userName";
   
    // response parameters
    public static final String statusParamName = "status";
    public static final String hierarchyParamName = "hierarchy";
    public static final String displayNameParamName = "displayName";
    public static final String officeNameParamName = "officeName";
    public static final String imageKeyParamName = "imageKey";
    public static final String imagePresentParamName = "imagePresent";
    public static final String addressTemplateParamName="addressTemplateData";

    // associations related part of response
    public static final String groupsParamName = "groups";

    // template related part of response
    public static final String officeOptionsParamName = "officeOptions";
    public static final String clientCategoryDatas="clientCategoryDatas";
    public static final String currencyParamName="currency";
    public static final String closureDateParamName = "closureDate";
    public static final String closureReasonIdParamName = "closureReasonId";
    public static final String balanceCheckParam="balanceCheck";
    
    // Client additional data 
    public static final String jobTitleParamName ="jobTitle";
    public static final String fiananceIdParamName ="financeId";
    public static final String utsCustomerIdParamName ="utsCustomerId";
    public static final String genderParamName ="gender";
    public static final String dateOfBirthParamName ="dateOfBirth";
    public static final String nationalityParamName ="nationality";
    public static final String idTypeParamName ="idType";
    public static final String idNumberParamName ="idNumber";
    public static final String remarksParamName ="remarks";
    public static final String preferredLangParamName ="preferredLang";
    public static final String ageGroupParamName ="ageGroup";
    public static final String preferredCommunicationParamName ="preferredCommunication";
    public static final String CLIENT_SERVICE_RESOURCE_NAME = "clientservice";
    public static final String billModeParamName="billMode";
    
    public static final String cafIdParamName = "cafId";
    //client bill profile info
 /*  public static String billCurrencyParamName="billCurrency";
   public static String billDayOfMonthParamName="billDayOfMonth";
   public static String billFrequencyParamName="billFrequency";*/
   
    
    
    
    
    public static final Set<String> CLIENT_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,titleParamName,
            dateFormatParamName, groupIdParamName, accountNoParamName, externalIdParamName, firstnameParamName, middlenameParamName,
            lastnameParamName, fullnameParamName, officeIdParamName, activeParamName, activationDateParamName,clientCategoryParamName,
            addressNoParamName,cityParamName,countryParamName,emailParamName,phoneParamName,homePhoneNumberParamName,stateParamName,streetParamName,
            zipCodeParamName,loginParamName,passwordParamName,flagParamName,groupParamName,entryTypeParamName,deviceParamName,
            jobTitleParamName,fiananceIdParamName,utsCustomerIdParamName,genderParamName,dateOfBirthParamName,nationalityParamName,idTypeParamName,
            idNumberParamName,remarksParamName,preferredLangParamName,ageGroupParamName,preferredCommunicationParamName,"serviceId","parameterId","parameterValue","billMode",
            cafIdParamName));


    public static final Set<String> CLIENT_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,titleParamName,
            dateFormatParamName, accountNoParamName, externalIdParamName, firstnameParamName, middlenameParamName,clientCategoryParamName,
            lastnameParamName, fullnameParamName, activeParamName, activationDateParamName,clientCategoryParamName,phoneParamName,homePhoneNumberParamName,
            emailParamName,officeIdParamName,groupParamName,entryTypeParamName,userNameParamName,passwordParamName,
            jobTitleParamName,fiananceIdParamName,utsCustomerIdParamName,genderParamName,dateOfBirthParamName,nationalityParamName,idTypeParamName,
            idNumberParamName,remarksParamName,preferredLangParamName,ageGroupParamName,preferredCommunicationParamName));
    
    public static final Set<String> CLIENT_CLOSE_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, closureDateParamName, closureReasonIdParamName));

    /**
     * These parameters will match the class level parameters of
     * {@link ClientData}. Where possible, we try to get response parameters to
     * match those of request parameters.
     */ 
    public static final Set<String> CLIENT_RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(idParamName, accountNoParamName,
            externalIdParamName, statusParamName, activeParamName, activationDateParamName, firstnameParamName, middlenameParamName,
            lastnameParamName, fullnameParamName, displayNameParamName, officeIdParamName, officeNameParamName, hierarchyParamName,currencyParamName,
            imageKeyParamName, imagePresentParamName, groupsParamName, officeOptionsParamName,clientCategoryDatas,addressTemplateParamName,balanceParamName,
            entryTypeParamName));


    public static final Set<String> ACTIVATION_REQUEST_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(localeParamName,
            dateFormatParamName, activationDateParamName));
	
	
    
	
	
}