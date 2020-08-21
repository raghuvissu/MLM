/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.service;

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.referal.data.ReferalData;
import org.mifosplatform.organisation.referal.exception.ReferalNotFoundException;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ReferalReadPlatformServiceImpl implements ReferalReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final OfficeRepository officeRepository;
    private final static String NAMEDECORATEDBASEON_HIERARCHY = "concat(substring('........................................', 1, ((LENGTH(o.hierarchy) - LENGTH(REPLACE(o.hierarchy, '.', '')) - 1) * 4)), o.name)";

    @Autowired
    public ReferalReadPlatformServiceImpl(final PlatformSecurityContext context, final OfficeRepository officeRepository,
    		final CurrencyReadPlatformService currencyReadPlatformService, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.officeRepository = officeRepository;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class ReferalMapper implements RowMapper<ReferalData> {

        public String referalSchema() {
            return "o.id AS id,o.name AS name,"
            	       +NAMEDECORATEDBASEON_HIERARCHY+
            	       "AS nameDecorated,o.external_id AS externalId,o.opening_date AS openingDate,o.hierarchy AS hierarchy," +
            	       "parent.id AS parentId,parent.name AS parentName,c.code_value as officeType, o.actual_level as actualLevel, o.rate as rate, "+
            	       "o.total_amount as totalAmount, o.total_payments as totalPayments, o.total_payments_by_referals as totalPaymentsByReferals, o.referal_count as referalCount, o.external_code as externalCode, o.flag as flag, ifnull(b.balance_amount,0 )as balance, " +
            	       "od.state as state,od.city as city,od.country as country,od.phone_number as phoneNumber,od.office_number as officeNumber,od.email_id as email " +
            	       " FROM m_referal_master o LEFT JOIN m_referal_master AS parent ON parent.id = o.parent_id join m_code_value c on c.id=o.office_type " +
            	       " Left join m_office_balance b on o.id = b.office_id  Left join b_office_address od ON o.id = od.office_id  ";
        }

        @Override
        public ReferalData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String name = resultSet.getString("name");
            final String nameDecorated = resultSet.getString("nameDecorated");
            final String externalId = resultSet.getString("externalId");
            final LocalDate openingDate = JdbcSupport.getLocalDate(resultSet, "openingDate");
            final String hierarchy = resultSet.getString("hierarchy");
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
        	final Long actualLevel = resultSet.getLong("actualLevel");
        	final Long rate = resultSet.getLong("rate");
        	final BigDecimal totalAmount = resultSet.getBigDecimal("totalAmount");
        	final BigDecimal totalPayments = resultSet.getBigDecimal("totalPayments");
        	final BigDecimal totalPaymentsByReferals = resultSet.getBigDecimal("totalPaymentsByReferals");
        	final Integer flag = resultSet.getInt("flag");
        	final Long referalCount = JdbcSupport.getLong(resultSet, "referalCount");
        	final String externalCode =resultSet.getString("externalCode");
        	
            return new ReferalData(id, name, nameDecorated, externalId, openingDate, hierarchy, parentId, parentName, null, null, officeType,balance,city,state,country,email,phoneNumber,officeNumber, actualLevel, rate, totalAmount, flag, totalPayments, totalPaymentsByReferals, referalCount, externalCode);
        }
    }
    
    private static final class NewReferalMapper implements RowMapper<ReferalData> {

        public String referalSchema() {
            return "o.id AS id,o.name AS name,"
            	       +NAMEDECORATEDBASEON_HIERARCHY+
            	       "AS nameDecorated,o.external_id AS externalId,o.opening_date AS openingDate,o.hierarchy AS hierarchy," +
            	       "parent.id AS parentId,parent.name AS parentName,c.code_value as officeType, o.actual_level as actualLevel, o.rate as rate, o.total_amount as totalAmount, o.flag as flag, o.total_payments as totalPayments, o.total_payments_by_referals as totalPaymentsByReferals, o.referal_count as referalCount, o.external_code as externalCode  " +
            	       " FROM m_referal_master o LEFT JOIN m_referal_master AS parent ON parent.id = o.parent_id join m_code_value c on c.id=o.office_type";
        }

        @Override
        public ReferalData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String name = resultSet.getString("name");
            final String nameDecorated = resultSet.getString("nameDecorated");
            final String externalId = resultSet.getString("externalId");
            final LocalDate openingDate = JdbcSupport.getLocalDate(resultSet, "openingDate");
            final String hierarchy = resultSet.getString("hierarchy");
            final Long parentId = JdbcSupport.getLong(resultSet, "parentId");
            final String parentName = resultSet.getString("parentName");
            final String officeType = resultSet.getString("officeType");
        	final Long actualLevel = resultSet.getLong("actualLevel");
        	final Long rate = resultSet.getLong("rate");
        	final BigDecimal totalAmount = resultSet.getBigDecimal("totalAmount");
        	final Integer flag = resultSet.getInt("flag");
        	final BigDecimal totalPayments = resultSet.getBigDecimal("totalPayments");
        	final BigDecimal totalPaymentsByReferals = resultSet.getBigDecimal("totalPaymentsByReferals");
        	final Long referalCount = JdbcSupport.getLong(resultSet, "referalCount");
        	final String externalCode =resultSet.getString("externalCode");

            return new ReferalData(id, name, nameDecorated, externalId, openingDate, hierarchy, parentId, parentName, null, null, null,null,null,null,null,null,null,null, actualLevel, rate, totalAmount, flag, totalPayments, totalPaymentsByReferals, referalCount, externalCode);
        }
    }

    private static final class ReferalDropdownMapper implements RowMapper<ReferalData> {

        public String schema() {
            return " o.id as id, " + NAMEDECORATEDBASEON_HIERARCHY + " as nameDecorated, o.name as name from m_referal_master o ";
        }

        @Override
        public ReferalData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

            final Long id = resultSet.getLong("id");
            final String name = resultSet.getString("name");
            final String nameDecorated = resultSet.getString("nameDecorated");

            return ReferalData.dropdown(id, name, nameDecorated);
        }
    }


    @Override
    public Collection<ReferalData> retrieveAllReferals() {

        final AppUser currentUser = context.authenticatedUser();

        String hierarchy = currentUser.getOffice().getHierarchy();
        String hierarchySearchString = hierarchy + "%";

        final ReferalMapper referalMapper = new ReferalMapper();
        final String sql = "select " + referalMapper.referalSchema() + " where o.hierarchy like ? order by o.hierarchy";

        return this.jdbcTemplate.query(sql, referalMapper , new Object[] { hierarchySearchString });
    }
    
    @Override
    public Collection<ReferalData> retrieveAllReferals(Long officeId) {

        final Office office = this.officeRepository.findOne(officeId);
        if (office == null) { throw new OfficeNotFoundException(officeId); }

        //String hierarchy = office.getHierarchy();
        String externalId = office.getExternalId();
        String hierarchySearchString = externalId + "%";

        final ReferalMapper referalMapper = new ReferalMapper();
        final String sql = "select " + referalMapper.referalSchema() + " where o.external_code like ? order by o.hierarchy";

        return this.jdbcTemplate.query(sql, referalMapper , new Object[] { hierarchySearchString });
    }
    
    @Override
    public ReferalData retrieveParentReferals(Long officeId) {

    	try{
	        final Office office = this.officeRepository.findOne(officeId);
	        if (office == null) { throw new OfficeNotFoundException(officeId); }
	
	        String externalId = office.getExternalId();
	        String hierarchySearchString = externalId + "%";
	
	        final ReferalMapper referalMapper = new ReferalMapper();
	        final String sql = "select " + referalMapper.referalSchema() + " where o.external_code like ? and o.parent_id is null";
	        ReferalData referalData = this.jdbcTemplate.queryForObject(sql, referalMapper , new Object[] { hierarchySearchString });
	
	        return referalData;
    	}catch (EmptyResultDataAccessException e) {
            throw new OfficeNotFoundException(officeId);
        }
    }
    
    @Override
    public Collection<ReferalData> retrieveAllReferals(Long clientId, String hierarchy) {
    	
    	context.authenticatedUser();

        String hierarchySearchString = hierarchy + "%";

        final ReferalMapper referalMapper = new ReferalMapper();
        final String sql = "select " + referalMapper.referalSchema() + " where o.hierarchy like ? order by o.hierarchy";

        return this.jdbcTemplate.query(sql, referalMapper , new Object[] { hierarchySearchString });
    }

    @Override
    public Collection<ReferalData> retrieveAllReferalsForDropdown() {
        final AppUser currentUser = context.authenticatedUser();

        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final ReferalDropdownMapper referalDropdownMap = new ReferalDropdownMapper();
        final String sql = "select " + referalDropdownMap.schema() + "where o.hierarchy like ? order by o.name";

        return this.jdbcTemplate.query(sql, referalDropdownMap , new Object[] { hierarchySearchString });
    }

    @Override
    public ReferalData retrieveReferal(final Long referalId) {

        try {
            context.authenticatedUser();

            final ReferalMapper referalMapper = new ReferalMapper();
            final String sql = "select " + referalMapper.referalSchema() + " where o.id = ?";

            return this.jdbcTemplate.queryForObject(sql, referalMapper , new Object[] { referalId });
        } catch (EmptyResultDataAccessException e) {
            throw new OfficeNotFoundException(referalId);
        }
    }
    
    @Override
    public ReferalData retrieveReferalWithExternal(final String externalId) {

        try {
            context.authenticatedUser();

            final NewReferalMapper referalMapper = new NewReferalMapper();
            final String sql = "select " + referalMapper.referalSchema() + " where o.external_id = ?";

            return this.jdbcTemplate.queryForObject(sql, referalMapper , new Object[] { externalId });
        } catch (EmptyResultDataAccessException e) {
            throw new ReferalNotFoundException(externalId);
        }
    }
    
    @Override
    public ReferalData retrieveReferalWithExternalCode(final String externalCode) {

        try {
            context.authenticatedUser();

            final NewReferalMapper referalMapper = new NewReferalMapper();
            final String sql = "select " + referalMapper.referalSchema() + " where o.external_code = ?";

            return this.jdbcTemplate.queryForObject(sql, referalMapper , new Object[] { externalCode });
        } catch (EmptyResultDataAccessException e) {
            throw new ReferalNotFoundException(externalCode);
        }
    }
    
    @Override
    public Collection<ReferalData> retrieveReferalWithParentIds(final Long parentId) {

        try {
            context.authenticatedUser();

            final NewReferalMapper referalMapper = new NewReferalMapper();
            final String sql = "select " + referalMapper.referalSchema() + " where o.parent_id = ?";

            return this.jdbcTemplate.query(sql, referalMapper , new Object[] { parentId });
        } catch (EmptyResultDataAccessException e) {
            throw new ReferalNotFoundException(parentId);
        }
    }

    @Override
    public ReferalData retrieveNewReferalTemplate() {

        context.authenticatedUser();

        return ReferalData.template(null, DateUtils.getLocalDateOfTenant(), null);
    }

    @Override
    public Collection<ReferalData> retrieveAllowedParents(final Long referalId) {

        context.authenticatedUser();
        final Collection<ReferalData> filterParentLookups = new ArrayList<ReferalData>();

        if (isNotHeadOffice(referalId)) {
            final Collection<ReferalData> parentLookups = retrieveAllReferalsForDropdown();

            for (final ReferalData office : parentLookups) {
                if (!office.hasIdentifyOf(referalId)) {
                    filterParentLookups.add(office);
                }
            }
        }

        return filterParentLookups;
    }

    private boolean isNotHeadOffice(final Long referalId) {
        return !Long.valueOf(1).equals(referalId);
    }

   
	@Override
	public List<ReferalData> retrieveAgentTypeData() {

        final AppUser currentUser = context.authenticatedUser();

        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final ReferalDropdownMapper referalDropdownMap = new ReferalDropdownMapper();
        final String sql = "select " + referalDropdownMap.schema() + ", m_code_value c  WHERE o.office_type = c.id AND c.code_value = 'agent' AND o.hierarchy LIKE ? " +
        		" ORDER BY o.name";

        return this.jdbcTemplate.query(sql, referalDropdownMap , new Object[] { hierarchySearchString });
    
	}
	
	
}