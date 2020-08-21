package org.mifosplatform.logistics.agent.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.data.AgentItemSaleData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author hugo
 *
 */
@Service
public class ItemSaleReadPlatformServiceImpl implements ItemSaleReadPlatformService{
	
private final JdbcTemplate jdbcTemplate;
private final PlatformSecurityContext context;
	
@Autowired	
public ItemSaleReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource){
	
	this.jdbcTemplate=new JdbcTemplate(dataSource);
	this.context=context;
		
	}


/* (non-Javadoc)
 * @see #retrieveAllData()
 */
@Transactional
@Override
public List<AgentItemSaleData> retrieveAllData() {

	 try{
		 
		 this.context.authenticatedUser();
		 final  ItemSaleMapper mapper=new ItemSaleMapper();
		 final String sql="select "+mapper.schema();
		 return this.jdbcTemplate.query(sql,mapper,new Object[]{});
		 
	 }catch(EmptyResultDataAccessException exception){
		 return null;
	 }

	
	
}

private static final class ItemSaleMapper implements RowMapper<AgentItemSaleData> {

	public String schema() {
		return "  it.id as id,it.item_id as itemId,it.agent_id as agentId,im.item_description AS itemName,o.name AS agentName," +
				" it.purchase_date AS purchaseDate,it.order_quantity AS orderQunatity,i.charge_amount AS chargeAmount, " +
				" i.tax_percantage AS tax,i.invoice_amount AS invoiceAmount FROM b_itemsale it, m_invoice i,b_item_master im," +
				" m_office o WHERE it.item_id = im.id AND it.agent_id = o.id AND it.id = i.sale_id";

	}

	@Override
	public AgentItemSaleData mapRow(final ResultSet rs, final int rowNum)throws SQLException {

		final Long id = rs.getLong("id");
		final Long itemId=rs.getLong("itemId");
		final Long agentId=rs.getLong("agentId");
		final Long orderQunatity=rs.getLong("orderQunatity");
		final String itemName = rs.getString("itemName");
		final String agentName = rs.getString("agentName");
		final BigDecimal chargeAmount=rs.getBigDecimal("chargeAmount");
	    final BigDecimal tax=rs.getBigDecimal("tax");
	    final BigDecimal invoiceAmount=rs.getBigDecimal("invoiceAmount");
	    
		return new AgentItemSaleData(id,itemId,agentId,itemName,agentName,orderQunatity,chargeAmount,tax,invoiceAmount,null,null,null);

	}
}

/* (non-Javadoc)
 * @see #retrieveSingleItemSaleData(java.lang.Long)
 */
@Override
public AgentItemSaleData retrieveSingleItemSaleData(Long id) {
 try{
		 
		 this.context.authenticatedUser();
		 final ItemSaleMapper mapper=new ItemSaleMapper();
		 final String sql="select "+mapper.schema()+" AND it.id=? ";
		 
		 return this.jdbcTemplate.queryForObject(sql,mapper,new Object[]{id});
		 
	 }catch(EmptyResultDataAccessException exception){
		 return null;
	 }

}

/* (non-Javadoc)
 * @see #retriveItemsaleIds()
 */
public List<MRNDetailsData> retriveItemsaleIds() {
	
	final String sql = "select id as itemsaleId,(select item_description from b_item_master where id=item_id) as itemDescription,item_id as itemMasterId  from b_itemsale where order_quantity>0 order by purchase_date desc";//"select id as mrnId,(select item_description from b_item_master where id=item_master_id) as itemDescription, item_master_id as itemMasterId from b_mrn order by requested_date desc";
	final ItemSaleDetailsMrnIDsMapper rowMapper = new ItemSaleDetailsMrnIDsMapper();
	
	return jdbcTemplate.query(sql,rowMapper);
}

private static final class ItemSaleDetailsMrnIDsMapper implements RowMapper<MRNDetailsData>{
	
	@Override
	public MRNDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		final String itemsaleId = rs.getString("itemsaleId");
		final Long itemMasterId = rs.getLong("itemMasterId");
		final String itemDescription = rs.getString("itemDescription");
		
		return new MRNDetailsData(null,itemDescription,itemMasterId,itemsaleId);
	}
}

@Override
public MRNDetailsData retrieveSingleItemSale(Long viewitemId) {
 try{
		 
		 this.context.authenticatedUser();
		 final ItemSaleMappers mapper=new ItemSaleMappers();
		 final String sql="select "+mapper.schema()+" where its.id = ? ";
		 
		 return this.jdbcTemplate.queryForObject(sql,mapper,new Object[]{viewitemId});
		 
	 }catch(EmptyResultDataAccessException exception){
		 return null;
	 }

}

private static final class ItemSaleMappers implements RowMapper<MRNDetailsData> {

	public String schema() {
		return  " its.id as id,its.purchase_date as requestedDate,(select item_description from b_item_master where id = its.item_id) as item," +
			    " (select name from m_office where id = 1) as fromOffice,(select name from m_office where id = its.purchase_by) as toOffice," +
			    " its.order_quantity as orderdQuantity,its.received_quantity as receivedQuantity, its.status as status from b_itemsale its ";
			           

	}

	@Override
	public MRNDetailsData mapRow(final ResultSet rs, final int rowNum)throws SQLException {
		final String id = rs.getString("id");
		final LocalDate requestedDate =JdbcSupport.getLocalDate(rs,"requestedDate");
		final String fromOffice = rs.getString("fromOffice");
		final String toOffice = rs.getString("toOffice");
		final Long orderdQuantity = rs.getLong("orderdQuantity");
		final Long receivedQuantity = rs.getLong("receivedQuantity");
		final String status = rs.getString("status");
		final String itemDescription = rs.getString("item");
		
		return new MRNDetailsData(id, requestedDate, fromOffice, toOffice, orderdQuantity, receivedQuantity, status,itemDescription);

	}
}



}
