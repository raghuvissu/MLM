package org.mifosplatform.obrm.service;

import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public interface ObrmReadPlatformService {

	ClientData retriveClientTotalData(String key,String value);

	String retrivePlanData(String key, String value );

	boolean checkPlanData(String planPoid, String dealPoid, String productPoid);
	
	
}
