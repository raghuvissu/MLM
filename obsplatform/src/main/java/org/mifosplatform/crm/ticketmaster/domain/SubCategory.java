package org.mifosplatform.crm.ticketmaster.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "b_sub_category")
public class SubCategory {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "main_category", length = 65536)
	private Long mainCategory;

	@Column(name = "sub_category")
	private String subCategory;

	public SubCategory(final Long id , final Long mainCategory , final String subCategory)
	{
		this.id=id;
		this.mainCategory=mainCategory;
		this.subCategory=subCategory;
	}
	
	
	public Long getId() {
		return id;
	}
	
	public Long getMainCategory() {
		return mainCategory;
	}

	public String getSubCategory() {
		return subCategory;
	}

	
	
	
}
