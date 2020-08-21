/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.referal.data.ReferalData;

public interface ReferalReadPlatformService {

    Collection<ReferalData> retrieveAllReferals();
    
    Collection<ReferalData> retrieveAllReferals(Long clientId, String hierarchy);

    Collection<ReferalData> retrieveAllReferalsForDropdown();

    ReferalData retrieveReferal(Long referalId);
    
    ReferalData retrieveReferalWithExternal(String externalId);
    
    ReferalData retrieveReferalWithExternalCode(String externalCode);
    
    Collection<ReferalData> retrieveReferalWithParentIds(Long parentId);

    ReferalData retrieveNewReferalTemplate();

    Collection<ReferalData> retrieveAllowedParents(Long referalId);

	List<ReferalData> retrieveAgentTypeData();

	Collection<ReferalData> retrieveAllReferals(Long officeId);

	ReferalData retrieveParentReferals(Long officeId);

}