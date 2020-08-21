package org.mifosplatform.organisation.lco.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/lco")
@Component
@Scope("singleton")
public class LCOApiResource {
	
	private final String resourceNameForPermissions = "LCO";
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final ClientReadPlatformService clientReadPlatformService;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<ChannelData> apiJsonSerializer;
	
	@Autowired
	public LCOApiResource(PlatformSecurityContext context,
			ToApiJsonSerializer<ClientData> toApiJsonSerializer,
			ClientReadPlatformService clientReadPlatformService,
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<ChannelData> apiJsonSerializer){
		this.context=context;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.clientReadPlatformService=clientReadPlatformService;
		this.portfolioCommandSourceWritePlatformService=portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer=apiJsonSerializer;
	}
	
	@GET
	@Path("/clients")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAll(@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("officeId") final Long officeId,
	            @QueryParam("externalId") final String externalId, @QueryParam("displayName") final String displayName,
	            @QueryParam("firstName") final String firstname, @QueryParam("lastName") final String lastname,
	            @QueryParam("underHierarchy") final String hierarchy, @QueryParam("offset") final Integer offset,
	            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
	            @QueryParam("sortOrder") final String sortOrder,@QueryParam("groupName") final String groupName,
	            @QueryParam("status") final String status) {

	        this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        final SearchParameters searchParameters = SearchParameters.forClients(sqlSearch, officeId, externalId, displayName, firstname,
	                lastname, hierarchy, offset, limit, orderBy, sortOrder,groupName,status);
	        final Page<ClientData> clientData = this.clientReadPlatformService.retrieveAllClientsForLCO(searchParameters);
	        return this.toApiJsonSerializer.serialize(clientData);
	 }
	
	
	@PUT
	@Path("/renewal")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String renewal(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().renewalLCOclients().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
			
	}
	
	
}
