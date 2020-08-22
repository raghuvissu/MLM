package org.mifosplatform.finance.entitypayments.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.finance.entitypayments.data.EntityPaymentData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
@Service
public class EntityPaymentReadPlatformServiceImpl implements EntityPaymentReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public EntityPaymentReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public EntityPaymentData retriveEntityPaymentsData(final Long clientId, final Long officeId) {
		try{
			final String sql = "select wae.id as id, wae.client_id as clientId, wae.office_id as officeId, wae.client_name as clientName, wae.total_office_dr as totalOfficeDR, "
					+ "wae.total_office_cr as totalOfficeCR, wae.office_total_amount as totalOfficeAmount, wae.office_payment_amount as officePaymentAmount from m_wallet_amount_entity as wae "
					+ " where wae.client_id = ? and wae.office_id = ?";
			final EntityPaymentsMapper epm = new EntityPaymentsMapper();
			return jdbcTemplate.queryForObject(sql, epm, new Object[] {clientId, officeId});
		}catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class EntityPaymentsMapper implements RowMapper<EntityPaymentData>{
	  @Override
	  public EntityPaymentData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		  final Long id = rs.getLong("id");
		  final Long clientId = rs.getLong("clientId");
		  final Long officeId = rs.getLong("officeId");
		  final String clientName = rs.getString("clientName");
		  final BigDecimal totalOfficeDR = rs.getBigDecimal("totalOfficeDR");
		  final BigDecimal totalOfficeCR = rs.getBigDecimal("totalOfficeCR");
		  final BigDecimal totalOfficeAmount = rs.getBigDecimal("totalOfficeAmount");
		  final BigDecimal officePaymentAmount = rs.getBigDecimal("officePaymentAmount");
	   return new EntityPaymentData(id, clientId, officeId, clientName, totalOfficeDR,totalOfficeCR,totalOfficeAmount,officePaymentAmount);
	  }
	 }


}