package org.mifosplatform.portfolio.product.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.product.domain.ProductDetailData;
import org.mifosplatform.portfolio.provisioncodemapping.data.ProvisionCodeMappingData;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;

public class ProductData {
    
	private Long      id;
	private String    productCode;
	private String    productDescription;
	private String   productCategory;
	private Long      serviceId;
	private String    serviceCode;
	/*private Long      provisionId;
	private Date      validityStart;
	private Date      validityEnd;*/
	private String    productStatus;
	
	//for template purpose
		private List<ProvisionCodeMappingData> provisionCodeMappingDatas;
		private List<ServiceData> serviceDatas;
		private List<EnumOptionData> status;
		private Collection<MCodeData> serviceParamsData;
		private Collection<ProductDetailData> productDetailDatas;
		private List<ServiceData> serviceCategorys;
		private Collection<ServiceDetailData> serviceDetailDatas;

	
	public ProductData(){
		
	}
	
	
	public ProductData(Long id, String productCode, String productDescription, String productCategory, Long serviceId,
			/*Long provisionId, Date validityStart, Date validityEnd,*/ String serviceCode, String status) {
		
		this.id = id;
		this.productCode = productCode;
		this.productDescription = productDescription;
		this.productCategory = productCategory;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		/*this.provisionId = provisionId;
		this.validityStart = validityStart;
		this.validityEnd = validityEnd;*/
		this.productStatus = status;
	
	}
	
	

	public ProductData(List<ProvisionCodeMappingData> provisionCodeMappingDatas, List<ServiceData> serviceDatas,
		   final List<EnumOptionData> status,final Collection<ProductDetailData> productDetailDatas,
		   final  List<ServiceData> serviceCategorys,final Collection<ServiceDetailData> serviceDetailDatas) {
		this.provisionCodeMappingDatas = provisionCodeMappingDatas;
		this.serviceDatas = serviceDatas;
		this.status = status;
		this.productDetailDatas = productDetailDatas;
		this.serviceCategorys = serviceCategorys;
		this.serviceDetailDatas = serviceDetailDatas;
	}


	public Long getId(){
		return id;
	}
	
	public String getProductCode(){
		return productCode;
	}
	
	public String getProductDescription(){
		return productDescription;
	}
	
	public String getProductCategory(){
		return productCategory;
	}

	public Long getServiceId(){
		return serviceId;
	}
	
	/*public Long getProvisionId(){
		return provisionId;
	}
	
	public Date getValidityStart(){
		return validityStart;
	}
	
	public Date getValidityEnd(){
		return validityEnd;
	}*/
	
	public String getProductStatus() {
		return productStatus;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}

	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	public void setProductDescription(final String productDescription) {
		this.productDescription = productDescription;
	}

	/*public void setServiceType(final String serviceType) {
		this.serviceType = serviceType;
	}*/

	public void setProductStatus(final String productStatus) {
		this.productStatus = productStatus;
	}

	/*public void setServiceUnitType(final String serviceUnitType) {
		this.serviceUnitType = serviceUnitType;
	}

	public void setIsOptional(final String isOptional) {
		this.isOptional = isOptional;
	}

	public void setServiceTypes(final Collection<EnumValuesData> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public void setServiceUnitTypes(final List<EnumOptionData> serviceUnitTypes) {
		this.serviceUnitTypes = serviceUnitTypes;
	}*/


	public List<ServiceData> getServiceDatas() {
		return serviceDatas;
	}
	
	public List<ProvisionCodeMappingData> getProvisionCodeMappingDatas(){
		return provisionCodeMappingDatas;
	}

	public void setProvisionCodeMappingDatas(List<ProvisionCodeMappingData> provisionCodeMappingDatas) {
		
		this.provisionCodeMappingDatas = provisionCodeMappingDatas;
	}


	public void setServiceMasterDatas(List<ServiceData> serviceDatas) {
		
		this.serviceDatas = serviceDatas;
	
	}
	
	public void setStatus(final List<EnumOptionData> status) {
		this.status = status;
	}
	
	public List<EnumOptionData> getStatus() {
		return status;
	}


	public void setServiceParamsData(Collection<MCodeData> serviceParamsData) {
		this.serviceParamsData = serviceParamsData;
	}
	
	public Collection<MCodeData> getServiceParamsData() {
		return serviceParamsData;
	}


	public void setProductDetailData(Collection<ProductDetailData> productDetailDatas) {
		this.productDetailDatas = productDetailDatas;
		
	}
	
	public Collection<ProductDetailData> getProductDetailDatas() {
		return productDetailDatas;
	}

	/*public void setServiceCategorys(List<ProductData> serviceCategorys) {
		this.serviceCategorys = serviceCategorys;
	}*/


	public void setServiceCategorys(List<ServiceData> serviceCategorys) {
		this.serviceCategorys = serviceCategorys;
		
	}


	public void setServiceDetailData(Collection<ServiceDetailData> serviceDetailDatas) {
		this.serviceDetailDatas = serviceDetailDatas;		
	}
	
	public Collection<ServiceDetailData> getServiceDetailDatas() {
		return serviceDetailDatas;
	}

	public String getServiceCode(){
		return serviceCode;
	}

	/*public String getServiceCategory() {
		return serviceCategory;
	}


	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}*/



}
