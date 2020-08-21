package org.mifosplatform.organisation.mapping.data;

import java.util.List;

import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.service.data.ServiceMasterData;

public class ChannelMappingData {
	
	private Long   id;
	private Long productId;
	private Long channelId;
	private String productCode;
	private String channelName;
	
	//for template purpose
	private List<ChannelData> channelDatas;
	private List<ServiceData> productDatas;
	
    public ChannelMappingData() {
    
    }
	
	public ChannelMappingData(Long id, Long productId, Long channelId,String productCode,String channelName) {
		this.id = id;
		this.productId = productId;
		this.channelId = channelId;
		this.productCode = productCode;
		this.channelName = channelName;
	}

	public ChannelMappingData(final List<ChannelData> channelDatas,final List<ServiceData> productDatas) {
		this.channelDatas = channelDatas;
		this.productDatas = productDatas;
	}
	
	public Long getId() {
		return id;
	}
	

	public Long getProductId() {
		return productId;
	}
	

	public Long getchannelId() {
		return channelId;
	}
	

	public String getProductCode(){
		return productCode;
	}
	
	public String getchannelName(){
		return channelName;
	}
	
	public List<ServiceData> getProductDatas() {
		return productDatas;
	}
	
	public List<ChannelData> getChannelDatas() {
		return channelDatas;
	}
	
	public void setProductDatas(List<ServiceData> productDatas) {
		this.productDatas = productDatas;		
	}

	public void setChannelDatas(List<ChannelData> channelDatas) {
		this.channelDatas = channelDatas;
		
	}

	/*public void setServiceMasterDatas(List<ServiceMasterData> retrieveServiceMastersForDropdown) {
		this.serviceMasterDatas = serviceMasterDatas;
		
	}*/

	
	
}
