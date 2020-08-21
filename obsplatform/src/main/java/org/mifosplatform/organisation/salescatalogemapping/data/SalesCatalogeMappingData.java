package org.mifosplatform.organisation.salescatalogemapping.data;

import java.util.List;

import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public class SalesCatalogeMappingData {
	
	private Long  id;
	private Long  catalogeId;
	private Long  planId;
	private String planCode;
	private String planDescription;
	private String name;
	
	//for template purpose
	private List<SalesCatalogeData> salesCatalogeDatas;
	private List<PlanData> planDatas;
	

	public SalesCatalogeMappingData() {
	}
	
	public SalesCatalogeMappingData(Long id, Long catalogeId, Long planId,String planCode,String planDescription,String name) {
		this.id = id;
		this.catalogeId = catalogeId;
		this.planId = planId;
		this.planCode = planCode;
		this.planDescription = planDescription;
		this.name = name;
	}
	
	public SalesCatalogeMappingData(final List<SalesCatalogeData> salesCatalogeDatas,final List<PlanData> planDatas) {
		this.salesCatalogeDatas = salesCatalogeDatas;
		this.planDatas = planDatas;
	}

	public Long getId() {
		return id;
	}

	public Long getCatalogeId() {
		return catalogeId;
	}

	public Long getPlanId() {
		return planId;
	}
	
	public String getPlanCode() {
		return planCode;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public String getName() {
		return name;
	}

	public List<SalesCatalogeData> getSalesCatalogeDatas() {
		return salesCatalogeDatas;
	}
	
	public void setSalesCatalogeDatas(List<SalesCatalogeData> salesCatalogeDatas) {
		this.salesCatalogeDatas = salesCatalogeDatas;		
	}

	public List<PlanData> getPlanDatas() {
		return planDatas;
	}
	
	public void setPlanDatas(List<PlanData> planDatas) {
		this.planDatas = planDatas;	
		
	}

	
	
	

}
