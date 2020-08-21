/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.portfolio.client.data.ClientData;

/**
 * Immutable data object for office data.
 */
public class ReferalData {

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
	private Long actualLevel;
	private Long rate;
	private BigDecimal totalAmount;
	private BigDecimal totalPayments;
	private BigDecimal totalPaymentsByReferals;
	private Integer flag;
	private String clientImageData;
	private Long referalCount;
	private ClientData clientData;
	private String externalCode;
    
    private final Collection<ReferalData> allowedParents;
    private final Collection<CodeValueData> officeTypes;


    public static ReferalData dropdown(final Long id, final String name, final String nameDecorated) {
    	
        return new ReferalData(id, name, nameDecorated, null, null, null, null, null, null,null,null,null,null,null,null,null,null,null, null, null, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, Long.valueOf(0), null);
    }

    public static ReferalData template(final Collection<ReferalData> parentLookups, final LocalDate defaultOpeningDate, final Collection<CodeValueData> officeTypes) {
    	
        return new ReferalData(null, null, null, null, defaultOpeningDate, null, null, null, parentLookups,officeTypes,null,null,null,null,null,null,null,null, null, null, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, Long.valueOf(0), null);
    }

    public static ReferalData appendedTemplate(final ReferalData office, final Collection<ReferalData> allowedParents, final Collection<CodeValueData> codeValueDatas) {
    	
        return new ReferalData(office.id, office.name, office.nameDecorated, office.externalId, office.openingDate, office.hierarchy,
                office.parentId, office.parentName, allowedParents,codeValueDatas,office.officeType,office.balance,office.city,office.state,office.country,office.email,office.phoneNumber,
                office.officeNumber, office.actualLevel, office.rate, office.totalAmount, office.flag, office.totalPayments, office.totalPaymentsByReferals, office.referalCount, office.externalCode);
    }

    public ReferalData(final Long id, final String name, final String nameDecorated, final String externalId, final LocalDate openingDate,
            final String hierarchy, final Long parentId, final String parentName, final Collection<ReferalData> allowedParents, 
            final Collection<CodeValueData> codeValueDatas, final String officeType, BigDecimal balance,final String city,
            final String state,final String country,final String email,final String phoneNumber,final String officeNumber,
            final Long actualLevel, final Long rate, final BigDecimal totalAmount, final Integer flag, final BigDecimal totalPayments, 
            final BigDecimal totalPaymentsByReferals, final Long referalCount, final String externalCode) {
    	
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
        this.actualLevel = actualLevel;
        this.rate = rate;
        this.totalAmount = totalAmount;
        this.flag = flag;
        this.totalPayments = totalPayments;
        this.totalPaymentsByReferals = totalPaymentsByReferals;
        this.referalCount = referalCount;
        this.externalCode = externalCode;
    }


	public boolean hasIdentifyOf(final Long officeId) {
    	
        return this.id.equals(officeId);
    }

	public Collection<ReferalData> getAllowedParents() {
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

	public Long getId() {
		return id;
	}
	
	public Long getRate() {
		return rate;
	}

	public void setRate(Long rate) {
		this.rate = rate;
	}

	public Long getParentId() {
		return parentId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getNameDecorated() {
		return nameDecorated;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getClientImageData() {
		return clientImageData;
	}

	public void setClientImageData(String clientImageData) {
		this.clientImageData = clientImageData;
	}

	public BigDecimal getTotalPayments() {
		return totalPayments;
	}

	public void setTotalPayments(BigDecimal totalPayments) {
		this.totalPayments = totalPayments;
	}

	public BigDecimal getTotalPaymentsByReferals() {
		return totalPaymentsByReferals;
	}

	public void setTotalPaymentsByReferals(BigDecimal totalPaymentsByReferals) {
		this.totalPaymentsByReferals = totalPaymentsByReferals;
	}

	public Long getReferalCount() {
		return referalCount;
	}

	public void setReferalCount(Long referalCount) {
		this.referalCount = referalCount;
	}

	public ClientData getClientData() {
		return clientData;
	}

	public void setClientData(ClientData clientData) {
		this.clientData = clientData;
	}

	public String getHierarchy() {
		return hierarchy;
	}
	
	
	
	
    
    
}