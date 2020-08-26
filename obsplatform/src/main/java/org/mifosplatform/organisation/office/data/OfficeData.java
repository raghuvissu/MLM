/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

/**
 * Immutable data object for office data.
 */
public class OfficeData {

    private final Long id;
    private final String name;
    private final String nameDecorated;
    private final String externalId;
    private final LocalDate openingDate;
    private final String hierarchy;
    private final Long parentId;
    private final String parentName;
    private final String officeType;
    private final BigDecimal balance;
    
    private List<String> countryData;
	private List<String> statesData;
	private List<String> citiesData;
	private String city; 
	private String state; 
	private String country; 
	private String email; 
	private String phoneNumber;
	private String officeNumber;
	private String addressName;
	private String contactPerson;
	private String zip;
	private String businessType;
	private String isEnabled;
    
    private final Collection<OfficeData> allowedParents;
    private final Collection<CodeValueData> officeTypes;
    private AddressData addressData;
    Collection<MCodeData> businessTypes;
    

    public static OfficeData dropdown(final Long id, final String name, final String nameDecorated, final String externalId, final String isEnabled) {
    	

        return new OfficeData(id, name, nameDecorated, externalId, null, null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null, isEnabled);
    }

    public static OfficeData template(final Collection<OfficeData> parentLookups, final LocalDate defaultOpeningDate, final Collection<CodeValueData> officeTypes) {
    	
        return new OfficeData(null, null, null, null, defaultOpeningDate, null, null, null, parentLookups,officeTypes,null,null,null,null,null,null,null,null,null,null,null,null, null);
    }

    public static OfficeData appendedTemplate(final OfficeData office, final Collection<OfficeData> allowedParents, final Collection<CodeValueData> codeValueDatas) {
    	
        return new OfficeData(office.id, office.name, office.nameDecorated, office.externalId, office.openingDate, office.hierarchy,
                office.parentId, office.parentName, allowedParents,codeValueDatas,office.officeType,office.balance,office.city,office.state,office.country,office.email,office.phoneNumber,
                office.officeNumber,office.addressName,office.contactPerson,office.zip,office.businessType, office.isEnabled);
    }

    public OfficeData(final Long id, final String name, final String nameDecorated, final String externalId, final LocalDate openingDate,
            final String hierarchy, final Long parentId, final String parentName, final Collection<OfficeData> allowedParents, 
            final Collection<CodeValueData> codeValueDatas, final String officeType, BigDecimal balance,final String city,
            final String state,final String country,final String email,final String phoneNumber,final String officeNumber,final String addressName,
            final String contactPerson,final String zip,final String businessType, final String isEnabled) {
    	
        this.id = id;
        this.name = name;
        this.nameDecorated = nameDecorated;
        this.externalId = externalId;
        this.openingDate = openingDate;
        this.hierarchy = hierarchy;
        this.parentName = parentName;
        this.parentId = parentId;
        this.allowedParents = allowedParents;
        this.officeTypes = codeValueDatas;
        this.officeType = officeType;
        this.balance=balance;
        this.city=city;
        this.state=state;
        this.country=country;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.officeNumber=officeNumber;
        this.addressName=addressName;
        this.contactPerson=contactPerson;
        this.zip=zip;
        this.businessType=businessType;
        this.isEnabled = isEnabled;
        
    }

	public Long getId() {
		return id;
	}

	public boolean hasIdentifyOf(final Long officeId) {
    	
        return this.id.equals(officeId);
    }

	public Collection<OfficeData> getAllowedParents() {
		return allowedParents;
	}

	public Collection<CodeValueData> getOfficeTypes() {
		return officeTypes;
	}

	public void setCountryData(List<String> countryData) {
		this.countryData = countryData;
	}

	public void setStatesData(List<String> statesData) {
		this.statesData = statesData;
	}

	public void setCitiesData(List<String> citiesData) {
		this.citiesData = citiesData;
	}

	public void setAddressData(AddressData addressData) {
		this.addressData = addressData;
	}
	
	public Collection<MCodeData> getBussinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(Collection<MCodeData> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public String getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
    
}