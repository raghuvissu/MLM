/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.data.OfficeTransactionData;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service("employeeSearchService")
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final static String NAMEDECORATEDBASEON_HIERARCHY = "concat(substring('........................................', 1, ((LENGTH(o.hierarchy) - LENGTH(REPLACE(o.hierarchy, '.', '')) - 1) * 4)), o.name)";
    private final PaginationHelper<OfficeData> paginationHelper = new PaginationHelper<OfficeData>();
    
    @Autowired
    public OfficeReadPlatformServiceImpl(final PlatformSecurityContext context,
    		final CurrencyReadPlatformService currencyReadPlatformService, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class OfficeMapper implements RowMapper<OfficeData> {

        public String officeSchema() {
            return "o.id AS id,o.name AS name,"
            	       +NAMEDECORATEDBASEON_HIERARCHY+
            	       "AS nameDecorated,o.external_id AS externalId,o.opening_date AS openingDate,o.hierarchy AS hierarchy, o.is_enabled AS isEnabled, " +
            	       "parent.id AS parentId,parent.name AS parentName,c.code_value as officeType, ifnull(b.balance_amount,0 )as balance, " +
            	       "od.state as state,od.city as city,od.country as country,od.phone_number as phoneNumber,od.office_number as officeNumber, " +
            	       "od.email_id as email,od.address_name as addressName,od.contact_person as contactPerson,od.zip as zip,od.business_type as businessType " +
            	       " FROM m_office o LEFT JOIN m_office AS parent ON parent.id = o.parent_id join m_code_value c on c.id=o.office_type " +
            	       " Left join m_office_balance b on o.id = b.office_id  Left join b_office_address od ON o.id = od.office_id  ";
        }

        @Override
        public OfficeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String name = resultSet.getString("name");
            final String nameDecorated = resultSet.getString("nameDecorated");
            final String externalId = resultSet.getString("externalId");
            final LocalDate openingDate = JdbcSupport.getLocalDate(resultSet, "openingDate");
            final String hierarchy = resultSet.getString("hierarchy");
            final String isEnabled = resultSet.getString("isEnabled");
            final Long parentId = JdbcSupport.getLong(resultSet, "parentId");
            final String parentName = resultSet.getString("parentName");
            final String officeType = resultSet.getString("officeType");
            final BigDecimal balance =resultSet.getBigDecimal("balance");
            final String city =resultSet.getString("city");
        	final String state =resultSet.getString("state");
        	final String country =resultSet.getString("country");
        	final String email =resultSet.getString("email");
        	final String phoneNumber =resultSet.getString("phoneNumber");
        	final String officeNumber =resultSet.getString("officeNumber");
        	final String addressName =resultSet.getString("addressName");
        	final String contactPerson =resultSet.getString("contactPerson");
        	final String zip =resultSet.getString("zip");
        	final String businessType =resultSet.getString("businessType");

            return new OfficeData(id, name, nameDecorated, externalId, openingDate, hierarchy, parentId, parentName, null, null, 
            		officeType,balance,city,state,country,email,phoneNumber,officeNumber,addressName,contactPerson,zip,businessType, isEnabled);
        }
    }

    private static final class OfficeDropdownMapper implements RowMapper<OfficeData> {

        public String schema() {
            return " o.id as id, " + NAMEDECORATEDBASEON_HIERARCHY + " as nameDecorated, o.name as name, o.external_id AS externalId, o.is_enabled AS isEnabled from m_office o ";
        }

        @Override
        public OfficeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String name = resultSet.getString("name");
            final String nameDecorated = resultSet.getString("nameDecorated");
            final String externalId = resultSet.getString("externalId");
            final String isEnabled = resultSet.getString("isEnabled");

            return OfficeData.dropdown(id, name, nameDecorated, externalId, isEnabled);
        }
    }

    private static final class OfficeTransactionMapper implements RowMapper<OfficeTransactionData> {

        public String schema() {
            return " ot.id as id, ot.transaction_date as transactionDate, ot.from_office_id as fromOfficeId, fromoff.name as fromOfficeName, "
                    + " ot.to_office_id as toOfficeId, tooff.name as toOfficeName, ot.transaction_amount as transactionAmount, ot.description as description, "
                    + " ot.currency_code as currencyCode, rc.decimal_places as currencyDigits, "
                    + " rc.name as currencyName, rc.internationalized_name_code as currencyNameCode, rc.display_symbol as currencyDisplaySymbol "
                    + " from m_office_transaction ot "
                    + " left join m_office fromoff on fromoff.id = ot.from_office_id "
                    + " left join m_office tooff on tooff.id = ot.to_office_id " + " join m_currency rc on rc.`code` = ot.currency_code";
        }

        @Override
        public OfficeTransactionData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final LocalDate transactionDate = JdbcSupport.getLocalDate(resultSet, "transactionDate");
            final Long fromOfficeId = JdbcSupport.getLong(resultSet, "fromOfficeId");
            final String fromOfficeName = resultSet.getString("fromOfficeName");
            final Long toOfficeId = JdbcSupport.getLong(resultSet, "toOfficeId");
            final String toOfficeName = resultSet.getString("toOfficeName");
            final String currencyCode = resultSet.getString("currencyCode");
            final String currencyName = resultSet.getString("currencyName");
            final String currencyNameCode = resultSet.getString("currencyNameCode");
            final String currencyDisplaySymbol = resultSet.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(resultSet, "currencyDigits");

            final CurrencyData currencyData = new CurrencyData(id, currencyCode, currencyName, currencyDigits, currencyDisplaySymbol, currencyNameCode, null, null, null);

            final BigDecimal transactionAmount = resultSet.getBigDecimal("transactionAmount");
            final String description = resultSet.getString("description");

            return OfficeTransactionData.instance(id, transactionDate, fromOfficeId, fromOfficeName, toOfficeId, toOfficeName,
                    currencyData, transactionAmount, description);
        }
    }

    @Override
    public Collection<OfficeData> retrieveAllOffices(String city) {

        final AppUser currentUser = context.authenticatedUser();

        String hierarchy = currentUser.getOffice().getHierarchy();
        String hierarchySearchString = hierarchy + "%";

        final OfficeMapper officeMapper = new OfficeMapper();
        String sql = "select " + officeMapper.officeSchema() + " where o.hierarchy like ? ";
        
        if(city != null && !city.isEmpty()){
        	sql = sql + " and od.city = '"+city+"' order by o.hierarchy  ";
        }else {
        	sql = sql + " order by o.hierarchy ";
        }

        return this.jdbcTemplate.query(sql, officeMapper , new Object[] { hierarchySearchString });
    }

    @Override
    public Collection<OfficeData> retrieveAllOfficesForDropdown() {
        final AppUser currentUser = context.authenticatedUser();

        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
        final String sql = "select " + officeDropdownMap.schema() + "where o.hierarchy like ? order by o.name";

        return this.jdbcTemplate.query(sql, officeDropdownMap , new Object[] { hierarchySearchString });
    }

    @Override
    public OfficeData retrieveOffice(final Long officeId) {

        try {
            context.authenticatedUser();

            final OfficeMapper officeMapper = new OfficeMapper();
            final String sql = "select " + officeMapper.officeSchema() + " where o.id = ?";

            return this.jdbcTemplate.queryForObject(sql, officeMapper , new Object[] { officeId });
        } catch (EmptyResultDataAccessException e) {
            throw new OfficeNotFoundException(officeId);
        }
    }

    @Override
    public OfficeData retrieveNewOfficeTemplate() {

        context.authenticatedUser();

        return OfficeData.template(null, DateUtils.getLocalDateOfTenant(), null);
    }

    @Override
    public Collection<OfficeData> retrieveAllowedParents(final Long officeId) {

        context.authenticatedUser();
        final Collection<OfficeData> filterParentLookups = new ArrayList<OfficeData>();

        if (isNotHeadOffice(officeId)) {
            final Collection<OfficeData> parentLookups = retrieveAllOfficesForDropdown();

            for (final OfficeData office : parentLookups) {
                if (!office.hasIdentifyOf(officeId)) {
                    filterParentLookups.add(office);
                }
            }
        }

        return filterParentLookups;
    }

    private boolean isNotHeadOffice(final Long officeId) {
        return !Long.valueOf(1).equals(officeId);
    }

    @Override
    public Collection<OfficeTransactionData> retrieveAllOfficeTransactions() {

        final AppUser currentUser = context.authenticatedUser();

        String hierarchy = currentUser.getOffice().getHierarchy();
        String hierarchySearchString = hierarchy + "%";

        OfficeTransactionMapper officeTransactionMap = new OfficeTransactionMapper();
        String sql = "select " + officeTransactionMap.schema()
                + " where (fromoff.hierarchy like ? or tooff.hierarchy like ?) order by ot.transaction_date, ot.id";

        return this.jdbcTemplate.query(sql, officeTransactionMap , new Object[] { hierarchySearchString, hierarchySearchString });
    }

    @Override
    public OfficeTransactionData retrieveNewOfficeTransactionDetails() {
        context.authenticatedUser();

        final Collection<OfficeData> parentLookups = retrieveAllOfficesForDropdown();
        final Collection<CurrencyData> currencyOptions = currencyReadPlatformService.retrieveAllowedCurrencies();

        return OfficeTransactionData.template(DateUtils.getLocalDateOfTenant(), parentLookups, currencyOptions);
    }

	@Override
	public List<OfficeData> retrieveAgentTypeData() {

        final AppUser currentUser = context.authenticatedUser();

        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
        final String sql = "select " + officeDropdownMap.schema() + ", m_code_value c  WHERE o.office_type = c.id AND c.code_value = 'agent' AND o.hierarchy LIKE ? " +
        		" ORDER BY o.name";

        return this.jdbcTemplate.query(sql, officeDropdownMap , new Object[] { hierarchySearchString });
    
	}
	
	@Override
	public Collection<FinancialTransactionsData> retreiveOfficeFinancialTransactionsData(final Long officeId) {
		
		context.authenticatedUser();
		final OfficeFinancialTransactionMapper mapper = new OfficeFinancialTransactionMapper(); 
		final String sql = "select v.* from  office_fin_trans_vw v where v.office_id=" + officeId + " order by  transDate desc ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}
	
	private static final class OfficeFinancialTransactionMapper implements RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum)throws SQLException {
			final Long officeId = resultSet.getLong("office_id");
			final Long transactionId = resultSet.getLong("TransId");
			final String transactionType = resultSet.getString("TransType");
			final BigDecimal debitAmount = resultSet.getBigDecimal("Dr_amt");
			final BigDecimal creditAmount =resultSet.getBigDecimal("Cr_amt");
			final String userName = resultSet.getString("username");
			final String transactionCategory = resultSet.getString("tran_type");
			final boolean flag = resultSet.getBoolean("flag");
			final LocalDate transDate = JdbcSupport.getLocalDate(resultSet, "TransDate");
			final String clientName = resultSet.getString("clientName");
			final Long rate = resultSet.getLong("rate");
			final int status = resultSet.getInt("paymentStatus");
			final String note = resultSet.getString("note");

			return new FinancialTransactionsData(officeId, transactionId, transDate, transactionType, debitAmount, creditAmount, 
					null, userName, transactionCategory, flag, null, null, clientName, rate, status, note, null, null);
		}
     }

	@Override
	public Collection<OfficeData> retrieveAllOfficesForSearch(String query) {
		final AppUser currentUser = context.authenticatedUser();

        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
        final String sql = "select distinct " + officeDropdownMap.schema() + "where o.hierarchy like ? and o.external_id like '"+query+"%' order by o.name";

        return this.jdbcTemplate.query(sql, officeDropdownMap , new Object[] { hierarchySearchString });
	}
	
	@Override
	public Page<OfficeData> retrieveAllLCOs(final SearchParameters searchParameters) {
		final AppUser currentUser = context.authenticatedUser();
        final OfficeMapper officeMapper = new OfficeMapper();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        
        
        final String hierarchySearchString = hierarchy + "%";
       
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        
        sqlBuilder.append(officeMapper.officeSchema());
        sqlBuilder.append(" where a.is_deleted='N' and a.address_key in ('PRIMARY','BILLING','BILLING1') and o.hierarchy like ?");

        final String extraCriteria = buildSqlStringFromClientCriteria(searchParameters);

        if (StringUtils.isNotBlank(extraCriteria)) {
            sqlBuilder.append(" and (").append(extraCriteria).append(")");
        }

        // DONT order by default - just use database natural ordering so doesnt
        // have to scan entire database table.
        // sql += " order by c.display_name ASC, c.account_no ASC";
        sqlBuilder.append(" group by c.id ");
        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ').append(searchParameters.getSortOrder());
        }
        
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
        }

        if (searchParameters.isOffset()) {
            sqlBuilder.append(" offset ").append(searchParameters.getOffset());
        }
         
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
                new Object[] { hierarchySearchString }, officeMapper);
    }
	
	
	
	 private String buildSqlStringFromClientCriteria(final SearchParameters searchParameters) {

	        final String sqlSearch = searchParameters.getSqlSearch();
	        final Long officeId = searchParameters.getOfficeId();
	        final String externalId = searchParameters.getExternalId();
	        final String displayName = searchParameters.getName();
	        final String firstname = searchParameters.getFirstname();
	        final String lastname = searchParameters.getLastname();
	        final String hierarchy = searchParameters.getHierarchy();
	        final String groupName = searchParameters.getGroupName();
	        final String status=searchParameters.getStatus();

	        String extraCriteria = "";
	        if (sqlSearch != null) {
	            extraCriteria = " and (" + sqlSearch + ")";
	            

	        	extraCriteria = " and ( display_name like '%" + sqlSearch + "%' OR c.phone like '%" + sqlSearch + "%' OR c.account_no like '%"+sqlSearch+"%' OR c.email like '%"+sqlSearch+"%'"
	        			+ " OR IFNULL(( Select min(serial_no) from b_allocation ba where c.id=ba.client_id and ba. is_deleted = 'N'),'No Hardware') LIKE '%"+sqlSearch+"%' )";
	            
	        	//extraCriteria = " and " +  sqlCondition + " = " + sqlSearch ;
	        	
	        }

	        if (officeId != null) {
	            extraCriteria += " and office_id = " + officeId;
	        }

	        if (externalId != null) {
	            extraCriteria += " and external_id like " + ApiParameterHelper.sqlEncodeString(externalId);
	        }

	        if (displayName != null) {
	            extraCriteria += " and concat(ifnull(firstname, ''), if(firstname > '',' ', '') , ifnull(lastname, '')) like "
	                    + ApiParameterHelper.sqlEncodeString(displayName);
	        }

	        if (firstname != null) {
	            extraCriteria += " and firstname like " + ApiParameterHelper.sqlEncodeString(firstname);
	        }

	        if (lastname != null) {
	            extraCriteria += " and lastname like " + ApiParameterHelper.sqlEncodeString(lastname);
	        }

	        if (hierarchy != null) {
	            extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString(hierarchy + "%");
	        }
	        
	        if (groupName != null) {
	            extraCriteria += " and g.group_name = " + ApiParameterHelper.sqlEncodeString(groupName);
	        }
	        
	        if (StringUtils.isNotBlank(extraCriteria)) {
	            extraCriteria = extraCriteria.substring(4);
	        }
	        
	        if(status !=null){
	        	final Integer statusValue=ClientStatus.fromStatus(status); 
	        	extraCriteria += " c.status_enum like " + statusValue;
	        }

	        return extraCriteria;
	    }

}