package org.mifosplatform.organisation.salescataloge.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.salescatalogemapping.data.SalesCatalogeMappingData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public class SalesCatalogeData {
	private Long   id;
	private String name;
	
	private Collection<SalesCatalogeMappingData> salesCatalogeMappingData ;
	private String planDescription;
	private String planCode;
	private List<PlanData> plans;
	private List<PlanData> selectedPlans;
	private SalesCatalogeData salesCatalogeData;
	public SalesCatalogeData() {
	}

	public SalesCatalogeData(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public SalesCatalogeData(List<PlanData> data, SalesCatalogeData salesCatalogeData,
			List<PlanData> plans) {
		
		if(plans!=null){
			this.id = id;
			this.planCode = planCode;
			this.planDescription = planDescription;
			}
           this.plans = data;
           this.salesCatalogeData = salesCatalogeData;
           this.selectedPlans = plans;
           
           
	}

	public Long getId() {
		return id;
	}

	public String getname() {
		return name;
	}

	public String getplanCode() {
		return planCode;
	}

	public String getplanDescription() {
		return planDescription;
	}
	
	public Collection<SalesCatalogeMappingData> getSalesCatalogeMappingData() {
		return salesCatalogeMappingData;
	}

	public void setSalesCatalogeMappingData(Collection<SalesCatalogeMappingData> salesCatalogeMappingData) {
		this.salesCatalogeMappingData = salesCatalogeMappingData;
	}


	
}
