package org.mifosplatform.portfolio.client.service;

import org.mifosplatform.portfolio.client.data.ClientBillInfoData;

public interface ClientBillInfoReadPlatformService {

	//Collection<ClientBillInfoData> retrieveClientBillInfoDetails();
	ClientBillInfoData retrieveClientBillInfoDetails(Long clientId);

}
