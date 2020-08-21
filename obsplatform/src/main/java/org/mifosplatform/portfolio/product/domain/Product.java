package org.mifosplatform.portfolio.product.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.service.domain.ServiceDetails;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_product", uniqueConstraints = @UniqueConstraint(name = "product_code_key", columnNames = { "product_code" }))
public class Product extends AbstractPersistable<Long>{
	
	@Column(name="product_code", nullable=false)
	private String productCode;
	
	@Column(name="product_description", nullable=false)
	private String productDescription;

	@Column(name="product_category", nullable=false)
	private String productCategory;

	@Column(name="service_id", nullable=false)
	private Long serviceId;

	/*@Column(name="provision_id", nullable=false)
	private Long provisionId;

	@Column(name="validity_start", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date validityStart;
	
	@Column(name="validity_end", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date validityEnd;*/
	
	@Column(name="status", nullable=false)
	private String status;
	
	@Column(name="is_deleted")
	private char isDeleted;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
	private List<ProductDetails> productDetails = new ArrayList<ProductDetails>();

	
	public List<ProductDetails> getProductDetails() {
		return productDetails;
	}

	public Product() {}
	
	public Product(String productCode, String productDescription, String productCategory, Long serviceId,
			/*Long provisionId, Date validityStart, Date validityEnd,*/ String status) {

		this.productCode = productCode;
		this.productDescription = productDescription;
		this.productCategory = productCategory;
		this.serviceId = serviceId;
		/*this.provisionId = provisionId;
		this.validityStart = validityStart;
		this.validityEnd = validityEnd;*/
		this.status = status;
		this.isDeleted = 'N';

	}
	
	

	public String getProductCode(){
		return productCode;
	}
	
	public void setProductCode(String productCode){
		this.productCode = productCode;
	}

	public String getProductDescription(){
		return productDescription;
	}
	
	public void setProductDescription(String productDescription){
		this.productDescription = productDescription;
	}
	
	public String getProductCategory(){
		return productCategory;
	}
	
	public void setProductCategory(String productCategory){
		this.productCategory = productCategory;
	}
	
	public Long getServiceId(){
		return serviceId;
	}
	
	public void setServiceId(Long serviceId){
		this.serviceId = serviceId;
	}
	
	/*public Long getProvisionId(){
		return provisionId;
	}
	
	public void setProvisionId(Long provisionId){
		this.provisionId = provisionId;
	}
	
	public Date getValidityStart(){
		return validityStart;
	}
	
	public void setValidityStart(Date validityStart){
		this.validityStart = validityStart;
	}
	
	public Date getValidityEnd(){
		return validityEnd;
	}
	
	public void setValidityEnd(Date validityEnd){
		this.validityEnd = validityEnd;
	}*/
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public static Product fromJson(final JsonCommand command) {
		
		final String productCode = command.stringValueOfParameterNamed("productCode");
		final String productDescription = command.stringValueOfParameterNamed("productDescription");
		final String productCategory = command.stringValueOfParameterNamed("productCategory");
		final Long serviceId = command.longValueOfParameterNamed("serviceId");
		final Long provisionId = command.longValueOfParameterNamed("provisionId");
		final Date validityStart = command.DateValueOfParameterNamed("validityStart");
		final Date validityEnd = command.DateValueOfParameterNamed("validityEnd");
		final String status = command.stringValueOfParameterNamed("status");
		
		return new Product(productCode,productDescription
		,productCategory,serviceId,/*provisionId,validityStart, validityEnd,*/status);
	}

	public Map<String, Object> update(JsonCommand command) {
		
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String productCodeNamedParamName = "productCode";
		final String productDescriptionNamedParamName = "productDescription";
		final String productCategoryNamedParamName = "productCategory";
		final String serviceIdNamedParamName = "serviceId";
		/*final String provisionIdNamedParamName = "provisionId";
		final String validityStartNamedParamName = "validityStart";
		final String validityEndNamedParamName = "validityEnd";*/
		final String statusNamedParamName = "status";
		
		if(command.isChangeInStringParameterNamed(productCodeNamedParamName, this.productCode)){
			final String newValue = command.stringValueOfParameterNamed(productCodeNamedParamName);
			actualChanges.put(productCodeNamedParamName, newValue);
			this.productCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInStringParameterNamed(productDescriptionNamedParamName, this.productDescription)){
			final String newValue = command.stringValueOfParameterNamed(productDescriptionNamedParamName);
			actualChanges.put(productDescriptionNamedParamName, newValue);
			this.productDescription = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		
		
		/*final boolean newvalueOfproductCategory = command.booleanPrimitiveValueOfParameterNamed(productCategoryNamedParamName);
		if(String.valueOf(newvalueOfproductCategory?'Y':'N').equalsIgnoreCase(String.valueOf(this.productCategory))){
			actualChanges.put(productCategoryNamedParamName, newvalueOfproductCategory);
		}
		this.productCategory = newvalueOfproductCategory?'Y':'N';*/
		
		final String productCategoryParamName = "productCategory";
        if (command.isChangeInStringParameterNamed(productCategoryParamName, this.productCategory)) {
            final String newValue = command.stringValueOfParameterNamed(productCategoryParamName);
            actualChanges.put(productCategoryParamName, newValue);
            this.productCategory = StringUtils.defaultIfEmpty(newValue, null);
        }
				
		
		if(command.isChangeInLongParameterNamed(serviceIdNamedParamName, this.serviceId)){
			final Long newValue = command.longValueOfParameterNamed(serviceIdNamedParamName);
			actualChanges.put(serviceIdNamedParamName, newValue);
			this.serviceId = newValue;
		}
		
		

		/*if(command.isChangeInLongParameterNamed(provisionIdNamedParamName, this.provisionId)){
			final Long newValue = command.longValueOfParameterNamed(provisionIdNamedParamName);
			actualChanges.put(provisionIdNamedParamName, newValue);
			this.provisionId = newValue;
		}
		
		
        if (command.isChangeInDateParameterNamed(validityStartNamedParamName, this.validityStart)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(validityStartNamedParamName);
            actualChanges.put(validityStartNamedParamName, newValue);
            this.validityStart = newValue.toDate();
        }
        
        if (command.isChangeInDateParameterNamed(validityEndNamedParamName, this.validityEnd)) {
            final LocalDate newValue = command.localDateValueOfParameterNamed(validityEndNamedParamName);
            actualChanges.put(validityStartNamedParamName, newValue);
            this.validityEnd = newValue.toDate();
        }*/
		
		if(command.isChangeInStringParameterNamed(statusNamedParamName, this.status)){
			final String newValue = command.stringValueOfParameterNamed(statusNamedParamName);
			actualChanges.put(statusNamedParamName, newValue);
			this.status = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		return actualChanges;
	
	
	}
	
	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {
	  this.isDeleted = 'Y';
	}

	public void addDetails(ProductDetails productDetail) {
		productDetail.update(this);
		this.productDetails.add(productDetail);
   }
	
	public ServiceData todata() {
		return new ServiceData(getId(),null,null,null,this.productCode,this.productDescription,null,null,null,null,null,null);
	}
	
}
