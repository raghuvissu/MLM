package org.mifosplatform.provisioning.provisioning.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;

@Component
public class ModelProvisionMappingCommandFromApiJsonDeserializer {

	private final FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("provisioningId","model","make","itemType","locale"));
	
	@Autowired
	public ModelProvisionMappingCommandFromApiJsonDeserializer(final FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper= fromJsonHelper;
	}
	
	 public void validateForCreate(final String json) {

	        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
	        final JsonElement element = fromJsonHelper.parse(json);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
	                .resource("ModelProvisionMapping");
	        
	        final Long provisioningId = this.fromJsonHelper.extractLongNamed("provisioningId", element);
	        baseDataValidator.reset().parameter("provisioningId").value(provisioningId).notBlank().notExceedingLengthOf(20);
	        
	        final String model = this.fromJsonHelper.extractStringNamed("model", element);
	        baseDataValidator.reset().parameter("model").value(model).notBlank().notExceedingLengthOf(100);
	        
	        /*final String make = this.fromJsonHelper.extractStringNamed("make", element);
	        baseDataValidator.reset().parameter("make").value(make);*/
	        
	        final String itemType = this.fromJsonHelper.extractStringNamed("itemType", element);
	        baseDataValidator.reset().parameter("model").value(model).notBlank();
	        
	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	    }
	    
	    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) {
	            /*throw new PlatformApiDataValidationException(dataValidationErrors);*/
	        	throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist","Validation errors exist.",dataValidationErrors); 
	        }
	    }
}
