package org.mifosplatform.obrm.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.obrm.service.ObrmReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/obrm")
@Component
@Scope("singleton")
public class ObrmApiResource {
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final ObrmReadPlatformService obrmReadPlatformService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	 
	@Autowired
	public ObrmApiResource(final PlatformSecurityContext context,
			final ToApiJsonSerializer<ClientData> toApiJsonSerializer, 
			final ObrmReadPlatformService obrmReadPlatformService,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.obrmReadPlatformService = obrmReadPlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}

	@GET
	@Path("getclient360")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getClientTotaldata(@QueryParam("key") final String key,
    		@QueryParam("value") final String value) {
		
		this.context.authenticatedUser();
		final ClientData clientdata = this.obrmReadPlatformService.retriveClientTotalData(key, value);
		return this.toApiJsonSerializer.serialize(clientdata);
	}
	
	
	
	@GET
	@Path("getplan")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getplan(@QueryParam("key") final String key,
    		@QueryParam("value") final String value) {
		
		this.context.authenticatedUser();
		final String plandata = this.obrmReadPlatformService.retrivePlanData(key, value);
		
		return plandata;
	}
	
	@GET
	@Path("syncplan")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String syncPlan(@QueryParam("planPoid") final String planPoid, @QueryParam("dealPoid") final String dealPoid, 
    		@QueryParam("productPoid") final String productPoid, final String apiRequestBodyAsJson) {
		this.context.authenticatedUser();
		final boolean existOrNot = this.obrmReadPlatformService.checkPlanData(planPoid, dealPoid, productPoid);
		if(existOrNot){
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createOBRMPlan().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		}else{
			System.out.println("Bye");
		}
		return null;
    }

	@POST
	@Path("createclient")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createOBRMClient().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	@POST
	@Path("createclientsimpleactivation")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create1(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createOBRMClientSimpleActivation().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	

	@POST
	@Path("createoffice")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createOffice(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createOBRMOffice().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

}
