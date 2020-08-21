package org.mifosplatform.finance.commissionpayments.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public class CommissionPaymentCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(
			Arrays.asList("id", "clientId", "paymentDate", "paymentCode","debitAmount", "creditAmount", "statmentId", "externalId", "dateFormat",
					"locale", "remarks","receiptNo","chequeNo","chequeDate","bankName","branchName","ispaymentEnable","renewalPeriod",
					"isChequeSelected","txn_id","cancelRemark","invoiceId","isWalletPayment","isSubscriptionPayment",
					"paymentType","deposit", "rate", "clientName", "officeId"));
	
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public CommissionPaymentCommandFromApiJsonDeserializer(
			final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public void validateForCreate(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("payment");

		final JsonElement element = fromApiJsonHelper.parse(json);
		
		final String paymentCode = fromApiJsonHelper.extractStringNamed("paymentCode", element);
		final BigDecimal debitAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("debitAmount", element);
		//String receiptNo = fromApiJsonHelper.extractStringNamed("receiptNo", element);
		
		baseDataValidator.reset().parameter("paymentCode").value(paymentCode).notBlank().notExceedingLengthOf(100);
		baseDataValidator.reset().parameter("debitAmount").value(debitAmount).notBlank().notExceedingLengthOf(100);
		//baseDataValidator.reset().parameter("receiptNo").value(receiptNo).notBlank().notExceedingLengthOf(50);
		
		if(fromApiJsonHelper.parameterExists("isChequeSelected", element)){
			String isChequeSelected = fromApiJsonHelper.extractStringNamed("isChequeSelected", element);
			if(isChequeSelected.equalsIgnoreCase("yes")){
				if(fromApiJsonHelper.parameterExists("chequeNo", element)){
					String chequeNo = fromApiJsonHelper.extractStringNamed("chequeNo", element);
					baseDataValidator.reset().parameter("chequeNo").value(chequeNo).notBlank().notExceedingLengthOf(20);
				}
				if(fromApiJsonHelper.parameterExists("chequeDate", element)){
					LocalDate chequeDate = fromApiJsonHelper.extractLocalDateNamed("chequeDate", element);
					baseDataValidator.reset().parameter("chequeDate").value(chequeDate).notBlank();
				}
				if(fromApiJsonHelper.parameterExists("bankName", element)){
					String bankName = fromApiJsonHelper.extractStringNamed("bankName", element);
					baseDataValidator.reset().parameter("bankName").value(bankName).notBlank().notExceedingLengthOf(100);
				}
				if(fromApiJsonHelper.parameterExists("branchName", element)){
					String branchName = fromApiJsonHelper.extractStringNamed("branchName", element);
					baseDataValidator.reset().parameter("branchName").value(branchName).notBlank().notExceedingLengthOf(100);
				}
			}
		}

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(
			final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}


}
