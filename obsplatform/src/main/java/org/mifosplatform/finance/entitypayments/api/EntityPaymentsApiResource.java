package org.mifosplatform.finance.entitypayments.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.entitypayments.data.EntityPaymentData;
import org.mifosplatform.finance.entitypayments.service.EntityPaymentReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/entitypayments")
@Component
@Scope("singleton")
public class EntityPaymentsApiResource {

	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "clientId", "officeId", "clientName",
					"totalOfficeDR", "totalOfficeCR", "totalOfficeAmount", "officePaymentAmount"));
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<EntityPaymentData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final EntityPaymentReadPlatformService entityPaymentReadPlatformService;

	@Autowired
	public EntityPaymentsApiResource(final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<EntityPaymentData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService writePlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper, final EntityPaymentReadPlatformService entityPaymentReadPlatformService) {
		
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.writePlatformService = writePlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.entityPaymentReadPlatformService = entityPaymentReadPlatformService;
	}

	/**
	 * This method is using for posting data to create payment
	 */
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createPayment(@PathParam("clientId") final Long clientId,	final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createEntityPayment(clientId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{clientId}/{officeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllDetailsForPayments(@PathParam("clientId") final Long clientId, @PathParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {
		final EntityPaymentData entityPaymentData = this.entityPaymentReadPlatformService.retriveEntityPaymentsData(clientId, officeId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, entityPaymentData, RESPONSE_DATA_PARAMETERS);

	}
}
