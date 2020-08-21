/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.referal.exception.CannotUpdateReferalWithParentReferalSameAsSelf;
import org.mifosplatform.organisation.referal.exception.RootReferalParentCannotBeUpdated;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_referal_master", uniqueConstraints = { @UniqueConstraint(columnNames = { "external_id" }, name = "externalid_refrl") })
public class Referal extends AbstractPersistable<Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1760184005999519057L;

	@OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private final List<Referal> children = new LinkedList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Referal parent;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "hierarchy", nullable = true, length = 50)
    private String hierarchy;

    @Column(name = "opening_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date openingDate;
    
    @Column(name="office_type",nullable = false)
    private Long officeType;

    @Column(name = "external_id", length = 100)
    private String externalId;
    
    @Column(name = "external_code")
    private String externalCode;
    
    @Column(name = "actual_level")
    private Long actualLevel;
    
    @Column(name = "rate")
    private Long rate;
    
    @Column(name = "total_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal totalAmount;
    
    @Column(name = "flag")
    private Integer flag;
    
    @Column(name = "total_payments", scale = 6, precision = 19, nullable = false)
   	private BigDecimal totalPayments;
    
    @Column(name = "total_payments_by_referals", scale = 6, precision = 19, nullable = false)
   	private BigDecimal totalPaymentsByReferals;
    
    @Column(name = "referal_count")
    private Long referalCount;
    

    public static Referal headReferal(final String name, final LocalDate openingDate, final String externalId) {
        return new Referal(null, name, openingDate, externalId,null, null, null, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, Long.valueOf(0), null);
    }

    public static Referal fromJson(final Referal parentReferal, final JsonCommand command) {

        final String name = command.stringValueOfParameterNamed("name");
        final LocalDate openingDate = command.localDateValueOfParameterNamed("openingDate");
        final String externalId = command.stringValueOfParameterNamed("externalId");
        final String externalCode = command.stringValueOfParameterNamed("externalCode");
        final Long officeType = command.longValueOfParameterNamed("officeType");
        final Long actualLevel = command.longValueOfParameterNamed("actualLevel");
        final Long rate = command.longValueOfParameterNamed("rate");
        final BigDecimal totalAmount = BigDecimal.ZERO;
        final BigDecimal totalPayments = BigDecimal.ZERO;
        final BigDecimal totalPaymentsByReferals = BigDecimal.ZERO;
        final Long referalCount = Long.valueOf(0);
        return new Referal(parentReferal, name, openingDate, externalId, officeType, actualLevel, rate, totalAmount, 0, totalPayments, totalPaymentsByReferals, referalCount, externalCode);
    }

    protected Referal() {
        this.openingDate = null;
        this.parent = null;
        this.name = null;
        this.externalId = null;
        this.actualLevel = null;
        this.rate = null;
        this.totalAmount = BigDecimal.ZERO;
        this.flag = 0;
        this.totalPayments = BigDecimal.ZERO;
        this.totalPaymentsByReferals = BigDecimal.ZERO;
        this.referalCount = Long.valueOf(0);
        this.externalCode = null;
    }
    
    

    public List<Referal> getChildren() {
		return children;
	}

	public Referal getParent() {
		return parent;
	}

	public Date getOpeningDate() {
		return openingDate;
	}

	public Long getOfficeType() {
		return officeType;
	}

	public String getExternalId() {
		return externalId;
	}
	
	public Long getActualLevel() {
		return actualLevel;
	}
	
	public Long getRate() {
		return rate;
	}

	private Referal(final Referal parent, final String name, final LocalDate openingDate, final String externalId, 
			final Long officeType, final Long actualLevel, final Long rate, final BigDecimal totalAmount, final Integer flag, final BigDecimal totalPayments,
			final BigDecimal totalPaymentsByReferals, final Long referalCount, final String externalCode) {
        this.parent = parent;
        this.openingDate = openingDate.toDateMidnight().toDate();
        if (parent != null) {
            this.parent.addChild(this);
        }

        if (StringUtils.isNotBlank(name)) {
            this.name = name.trim();
        } else {
            this.name = null;
        }
        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId.trim();
        } else {
            this.externalId = null;
        }
        
        this.officeType=officeType;
        this.actualLevel = actualLevel;
        this.rate = rate;
        this.totalAmount = totalAmount;
        this.totalPayments = totalPayments;
        this.totalPaymentsByReferals = totalPaymentsByReferals;
        this.referalCount = referalCount;
        this.flag = flag;
        this.externalCode = externalCode;
    }

    private void addChild(final Referal referal) {
        this.children.add(referal);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        final String parentIdParamName = "parentId";

        if (command.parameterExists(parentIdParamName) && this.parent == null) { throw new RootReferalParentCannotBeUpdated(); }

        if (this.parent != null && command.isChangeInLongParameterNamed(parentIdParamName, this.parent.getId())) {
            final Long newValue = command.longValueOfParameterNamed(parentIdParamName);
            actualChanges.put(parentIdParamName, newValue);
        }

        final String openingDateParamName = "openingDate";
        if (command.isChangeInLocalDateParameterNamed(openingDateParamName, getOpeningLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(openingDateParamName);
            actualChanges.put(openingDateParamName, valueAsInput);
            actualChanges.put("dateFormat", dateFormatAsInput);
            actualChanges.put("locale", localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(openingDateParamName);
            this.openingDate = newValue.toDate();
        }

        final String nameParamName = "name";
        if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(nameParamName);
            actualChanges.put(nameParamName, newValue);
            this.name = newValue;
        }
        
        final String officeTypeParam = "officeType";
        if(command.isChangeInLongParameterNamed(officeTypeParam, this.officeType)){
        	
        	final Long newValue = command.longValueOfParameterNamed(officeTypeParam);
        	actualChanges.put(officeTypeParam, newValue);
        	this.officeType = newValue;
        	
        }

        final String externalIdParamName = "externalId";
        if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }
        
        final String partnernameParamName = "partnerName";
        if (command.isChangeInStringParameterNamed(partnernameParamName, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(partnernameParamName);
            actualChanges.put(partnernameParamName, newValue);
            this.name = newValue;
        }

        return actualChanges;
    }

    public boolean isOpeningDateBefore(final LocalDate baseDate) {
        return getOpeningLocalDate().isBefore(baseDate);
    }

    public boolean isOpeningDateAfter(final LocalDate activationLocalDate) {
        return getOpeningLocalDate().isAfter(activationLocalDate);
    }

    private LocalDate getOpeningLocalDate() {
        LocalDate openingLocalDate = null;
        if (this.openingDate != null) {
            openingLocalDate = LocalDate.fromDateFields(this.openingDate);
        }
        return openingLocalDate;
    }

    public void update(final Referal newParent) {

        if (this.parent == null) { throw new RootReferalParentCannotBeUpdated(); }

        if (this.identifiedBy(newParent.getId())) { throw new CannotUpdateReferalWithParentReferalSameAsSelf(this.getId(), newParent.getId()); }

        this.parent = newParent;
        generateHierarchy();
    }

    public boolean identifiedBy(final Long id) {
        return getId().equals(id);
    }

    public void generateHierarchy() {

        if (parent != null) {
            this.hierarchy = this.parent.hierarchyOf(getId());
        } else {
            this.hierarchy = ".";
        }
    }

    private String hierarchyOf(final Long id) {
        return this.hierarchy + id.toString() + ".";
    }

    public String getName() {
        return this.name;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public boolean hasParentOf(final Referal office) {
        boolean isParent = false;
        if (this.parent != null) {
            isParent = this.parent.equals(office);
        }
        return isParent;
    }

    public boolean doesNotHaveAnReferalInHierarchyWithId(final Long referalId) {
        return !this.hasAnReferalInHierarchyWithId(referalId);
    }

    private boolean hasAnReferalInHierarchyWithId(final Long referalId) {

        boolean match = false;

        if (identifiedBy(referalId)) {
            match = true;
        }

        if (!match) {
            for (final Referal child : this.children) {
                final boolean result = child.hasAnReferalInHierarchyWithId(referalId);

                if (result) {
                    match = result;
                    break;
                }
            }
        }

        return match;
    }

	public static Referal fromPartner(final Referal parentOffice,final JsonCommand command) {

		final String name = command.stringValueOfParameterNamed("partnerName");
		final LocalDate openingDate = DateUtils.getLocalDateOfTenant();
		 final String externalId = command.stringValueOfParameterNamed("externalId");
		 final String externalCode = command.stringValueOfParameterNamed("externalCode");
		final Long officeType = command.longValueOfParameterNamed("officeType");
		final Long actualLevel = command.longValueOfParameterNamed("actualLevel");
		final Long rate = command.longValueOfParameterNamed("rate");
		final BigDecimal totalAmount = BigDecimal.ZERO;
		final BigDecimal totalPayments = BigDecimal.ZERO;
		final BigDecimal totalPaymentsByReferals = BigDecimal.ZERO;
		final Long referalCount = Long.valueOf(0);
		final Integer flag = 0;
		
		return new Referal(parentOffice, name, openingDate, externalId,officeType, actualLevel, rate, totalAmount, flag, totalPayments, totalPaymentsByReferals, referalCount, externalCode);
	}

	
	public void setParent(Referal parent) {
		this.parent = parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

	public void setOfficeType(Long officeType) {
		this.officeType = officeType;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public void setActualLevel(Long actualLevel) {
		this.actualLevel = actualLevel;
	}
	
	public void setRate(Long rate) {
		this.rate = rate;
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

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}
	
	
	
	

}
