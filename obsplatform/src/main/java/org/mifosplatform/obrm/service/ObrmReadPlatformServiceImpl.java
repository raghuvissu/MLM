package org.mifosplatform.obrm.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.obrm.api.ObrmApiConstants;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ObrmReadPlatformServiceImpl implements ObrmReadPlatformService{

	private final ClientReadPlatformService clientReadPlatformService;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final PlanReadPlatformService planReadPlatformService;
	private final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService;
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public ObrmReadPlatformServiceImpl(ClientReadPlatformService clientReadPlatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			PlanReadPlatformService planReadPlatformService,
			final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService,
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {

				this.clientReadPlatformService = clientReadPlatformService;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.orderReadPlatformService =  orderReadPlatformService;
		this.planReadPlatformService=planReadPlatformService;
		this.obrmReadWriteConsiliatrService = obrmReadWriteConsiliatrService;
		this.context=context;
		this.jdbcTemplate=new JdbcTemplate(dataSource);
	}

	
	@Override
	public ClientData retriveClientTotalData(String key, String value) {
		try{
			ClientData clientData = this.clientReadPlatformService.retrieveClientForcrm(key,value);
			String result = this.obrmReadWriteConsiliatrService.processObrmRequest(ObrmApiConstants.READCLIENT_OPCODE, clientData.obrmRequestInput());
			if(result != null){
				clientData = ClientData.fromJson(result, clientData);
			
				//client service data preparation
				List<ClientServiceData> clientServiceDatas = this.clientServiceReadPlatformService.retriveClientServices(clientData.getId());
				if(!clientServiceDatas.isEmpty()){
					clientServiceDatas = ClientServiceData.fromOBRMJson(result, clientServiceDatas);
					clientData.setClientServiceData(clientServiceDatas);
				}
			
			
				//onetimesale  data preparation
				List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService.retrieveClientOneTimeSalesData(clientData.getId());
				if(!oneTimeSaleDatas.isEmpty()){
					oneTimeSaleDatas = OneTimeSaleData.fromOBRMJson(result,oneTimeSaleDatas);
					clientData.setOneTimeSaleData(oneTimeSaleDatas);
				}
			
			
			
				//	orders data preparation
				List<OrderData> orderDatas = this.orderReadPlatformService.retrieveClientOrderDetails(clientData.getId());
				if(!orderDatas.isEmpty()){
					orderDatas = OrderData.fromOBRMJson(result,orderDatas);
					clientData.setOrderData(orderDatas);
				}
				
				return clientData;
			}else{
				throw new PlatformDataIntegrityException("", "");
			}
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.jsonexception.occured",e.getMessage());
		}catch(ParseException e){
			throw new PlatformDataIntegrityException("error.msg.parse.exception.occured", e.getMessage());
		}
	}


	@Override
	public String retrivePlanData(String key, String value) {
		// TODO Auto-generated method stub
		String result = this.obrmReadWriteConsiliatrService.processObrmRequest(ObrmApiConstants.READPLAN_OPCODE, PlanData.obrmRequestInput(null, null, null));
	/*	if(result != null){
			clientData = ClientData.fromJson(result, planData);
		
	*/	return result;
	}

	
	@Override
	public boolean checkPlanData(String planPoid, String dealPoid, String productPoid) {
		
		boolean returnValue=false;
		context.authenticatedUser();
		PlanMapper rm = new PlanMapper();
		final String sql=rm.schema()+"where pm.plan_poid = '"+planPoid+"' and pd.deal_poid = '"+dealPoid+"'"+
				" and s.product_poid = '"+productPoid+"'";
			PlanData planData= this.jdbcTemplate.queryForObject(sql, rm, new Object[] { });
			if(planData!=null){
				returnValue=true;
			}	
		return returnValue;
	}


	 private static final class PlanMapper implements RowMapper<PlanData> {

	        @Override
	        public PlanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

	            final Long id = rs.getLong("id");
	            	            return new PlanData(id);
	        }
	        
	        String schema(){
	        	return "select pm.id from b_plan_master pm join b_plan_detail pd ON pm.id = pd.plan_id " +
	        			"join b_service s ON pd.service_code = s.service_code join " +
	        			"b_plan_pricing pp ON pd.id = pp.plan_id ";
	        }
	        
	}

	
	
	
}
