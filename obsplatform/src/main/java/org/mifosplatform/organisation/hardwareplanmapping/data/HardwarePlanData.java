package org.mifosplatform.organisation.hardwareplanmapping.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public class HardwarePlanData {

	private Long id;
	private Long itemClass;
	private String itemClassName;
	private Long provisioningId;
	private String provisioningValue;

	private Collection<EnumValuesData> itemClassData;
	private List<McodeData> provisioning;

	
	
	public HardwarePlanData() {
	}

	public HardwarePlanData(final Collection<EnumValuesData> itemClassData,final List<McodeData> provisioning) {
 
		this.itemClassData=itemClassData;
		
		this.provisioning = provisioning;

	}

	public HardwarePlanData(final Long id,final Long itemClass, final String itemClassName,final Long provisioningId,final String provisioningValue) {

		this.id = id;
		this.itemClass = itemClass;
		this.itemClassName = itemClassName;
		this.provisioningId = provisioningId;
		this.provisioningValue = provisioningValue;

	}

	public Long getId() {
		return id;
	}


	public Long getItemCode() {
		return itemClass;
	}


	public List<McodeData> getProvisioning() {
		return provisioning;
	}

	public void setProvisioning(List<McodeData> provisioning) {
		this.provisioning = provisioning;
	}

	public Long getProvisioningId() {
		return provisioningId;
	}

	public void setProvisioningId(Long provisioningId) {
		this.provisioningId = provisioningId;
	}

	public String getProvisioningValue() {
		return provisioningValue;
	}

	public void setProvisioningValue(String provisioningValue) {
		this.provisioningValue = provisioningValue;
	}

	public void addProvisioning(List<McodeData> provisioning) {
		this.provisioning = provisioning;
		
	}
	
	public Collection<EnumValuesData> getItemClassData() {
		return itemClassData;
	}

	public void addData(Collection<EnumValuesData> itemClassdata) {
		this.itemClassData = itemClassdata;
		
	}

	public void setItemClassData(Collection<EnumValuesData> itemClassData) {
		this.itemClassData = itemClassData;
	}

	public Long getItemClass() {
		return itemClass;
	}

	public void setItemClass(Long itemClass) {
		this.itemClass = itemClass;
	}

	
	

}
