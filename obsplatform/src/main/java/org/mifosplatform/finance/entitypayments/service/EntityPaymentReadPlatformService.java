package org.mifosplatform.finance.entitypayments.service;

import org.mifosplatform.finance.entitypayments.data.EntityPaymentData;

public interface EntityPaymentReadPlatformService {

	EntityPaymentData retriveEntityPaymentsData(Long clientId, Long officeId);


}
