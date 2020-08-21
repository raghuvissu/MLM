/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.data.OfficeTransactionData;
import org.mifosplatform.portfolio.group.service.SearchParameters;

public interface OfficeReadPlatformService {

    Collection<OfficeData> retrieveAllOffices(String city);

    Collection<OfficeData> retrieveAllOfficesForDropdown();

    OfficeData retrieveOffice(Long officeId);

    OfficeData retrieveNewOfficeTemplate();

    Collection<OfficeData> retrieveAllowedParents(Long officeId);

    Collection<OfficeTransactionData> retrieveAllOfficeTransactions();

    OfficeTransactionData retrieveNewOfficeTransactionDetails();

	List<OfficeData> retrieveAgentTypeData();

	Collection<FinancialTransactionsData> retreiveOfficeFinancialTransactionsData(Long officeId);
	
	Collection<OfficeData> retrieveAllOfficesForSearch(String query);

	Page<OfficeData> retrieveAllLCOs(SearchParameters searchParameters);
}