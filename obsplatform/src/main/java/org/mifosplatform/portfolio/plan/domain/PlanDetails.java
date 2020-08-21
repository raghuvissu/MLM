package org.mifosplatform.portfolio.plan.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

/**
 * @author hugo
 *
 */
@Entity
@Table(name = "b_plan_detail")
public class PlanDetails {

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@ManyToOne
    @JoinColumn(name="plan_id")
    private Plan plan;

	@Column(name ="product_id", length=50)
    private Long productId;


	@Column(name = "is_deleted", nullable = false)
	private char isDeleted = 'n';

	/*@Column(name ="deal_poid", length=100)
    private String dealPoid;
*/
	public PlanDetails()
	{
		  // This constructor is intentionally empty. Nothing special is needed here.
	}
	public PlanDetails(final Long productId)
	{

		this.productId=productId;
		this.plan=null;

	}
	public PlanDetails(final String serviceCode, Plan plan)
	{

		this.plan=plan;
		}

	public Long getId() {
		return id;
	}
	public char getIsDeleted() {
		return isDeleted;
	}
	public Long getProductId() {
		return productId;
	}


	public char isIsDeleted() {
		return isDeleted;
	}

	public Plan getPlan() {
		return plan;
	}
	
	/*public String getDealPoid(){
		return dealPoid;
	}*/
	
	public void update(final Plan plan){
		this.plan=plan;
	}
	
	public void delete() {
		this.isDeleted='y';
	}
	
	public static PlanDetails fromJson(final JsonCommand command) {
		
		    final Long productId = command.longValueOfParameterNamed("productId");
		    return new PlanDetails(productId);
	}

	public Map<String, Object> update(final Plan plan, final JsonCommand command){
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		/*final String serviceCodeName = "serviceCode";
			if (command.isChangeInStringParameterNamed(serviceCodeName, this.serviceCode)) {
				final String newValue = command.stringValueOfParameterNamed(serviceCodeName);
	            actualChanges.put(serviceCodeName, newValue);
	            this.serviceCode = StringUtils.defaultIfEmpty(newValue, null);
	        }*/
	        return actualChanges;
	}
}