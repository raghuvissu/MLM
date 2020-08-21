package org.mifosplatform.portfolio.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientBillInfoReadPlatformServiceImpl implements ClientBillInfoReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public ClientBillInfoReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	@Override
	public ClientBillInfoData retrieveClientBillInfoDetails(Long clientId) {

		/*try {

			//final ClientBillInfoMapper mapper = new ClientBillInfoMapper();
			ClientBillInfoMapper broadcasterMapper = new ClientBillInfoMapper();
			final String sql = "select " + mapper.schema() ;
			//return this.jdbcTemplate.query(sql, mapper, new Object[] {});
			return jdbcTemplate.queryForObject(sql, ClientBillInfoMapper,new Object[]{clientId});
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}*/
		try{
			ClientBillInfoMapper clientBillInfoMapper = new ClientBillInfoMapper();
			String sql = "SELECT "+clientBillInfoMapper.schema()+" WHERE cb.client_id = ?";
			return jdbcTemplate.queryForObject(sql, clientBillInfoMapper,new Object[]{clientId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
		
		
	}
	
	private class ClientBillInfoMapper implements RowMapper<ClientBillInfoData> {
		
	    @Override
		public ClientBillInfoData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long clientId = rs.getLong("clientId");
			final Long billDayOfMonth = rs.getLong("billDayOfMonth");
			final Long billCurrency = rs.getLong("billCurrency");
		    final Long billFrequency = rs.getLong("billFrequency");
		    final String billSegment = rs.getString("billSegment");
		    final LocalDate nextBillDay=JdbcSupport.getLocalDate(rs,"nextBillDay");
			final LocalDate lastBillDay=JdbcSupport.getLocalDate(rs,"lastBillDay");
			final Long lastBillNo = rs.getLong("lastBillNo");
			final Long paymentType = rs.getLong("paymentType");	
			final Boolean billSuppressionFlag = rs.getBoolean("billSuppressionFlag");
			final Long billSuppressionId = rs.getLong("billSuppressionId");
		    final Boolean firstBill = rs.getBoolean("firstBill");
		    final Boolean hotBill = rs.getBoolean("hotBill");
			
			return new ClientBillInfoData(clientId, billDayOfMonth, billCurrency, billFrequency, billSegment, nextBillDay, lastBillDay,lastBillNo,paymentType,billSuppressionFlag,billSuppressionId,firstBill,hotBill);
		}
	    
		public String schema() {
			
			return " cb.client_id AS clientId, cb.bill_day_of_month AS billDayOfMonth, cb.bill_currency AS billCurrency, cb.bill_frequency AS billFrequency, " +
				   " cb.bill_segment AS billSegment, cb.next_bill_day AS nextBillDay, cb.last_bill_day AS lastBillDay, cb.last_bill_no AS lastBillNo, " +
				   " cb.payment_type AS paymentType, cb.bill_suppression_flag AS billSuppressionFlag, cb.bill_suppression_id AS billSuppressionId,cb.first_bill AS firstBill,cb.hot_bill AS hotBill FROM m_client_billprofile cb ";
			
		}
	}
	
	
	
}