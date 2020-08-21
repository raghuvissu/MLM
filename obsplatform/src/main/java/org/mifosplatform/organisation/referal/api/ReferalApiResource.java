/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.referal.data.ReferalData;
import org.mifosplatform.organisation.referal.exception.ReferalNotFoundException;
import org.mifosplatform.organisation.referal.service.ReferalReadPlatformService;
import org.mifosplatform.portfolio.client.api.ClientImagesApiResource;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/referals")
@Component
@Scope("singleton")
public class ReferalApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link ReferalData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "name", "nameDecorated", "externalId",
            "openingDate", "hierarchy", "parentId", "parentName", "allowedParents","officeTypes", "actualLevel", "rate"));

    private final String resourceNameForPermissions = "REFERAL";
    
    private final PlatformSecurityContext context;
    private final ReferalReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<ReferalData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    public static final String OFFICE_TYPE="Office Type";
    public final ClientImagesApiResource clientImagesApiResource;
    public final ClientReadPlatformService clientReadPlatformService;
    private final ReferalReadPlatformService  referalReadPlatformService;

    @Autowired
    public ReferalApiResource(final PlatformSecurityContext context, final ReferalReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<ReferalData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService, final ClientImagesApiResource clientImagesApiResource,
            final ClientReadPlatformService clientReadPlatformService, final ReferalReadPlatformService  referalReadPlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.clientImagesApiResource = clientImagesApiResource;
        this.clientReadPlatformService = clientReadPlatformService;
        this.referalReadPlatformService = referalReadPlatformService;
    }

    @GET 
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReferals(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        final Collection<ReferalData> referals = this.readPlatformService.retrieveAllReferals();
        Iterator<ReferalData> itr = referals.iterator();
        
        while(itr.hasNext()){
        	ReferalData ref = itr.next();
        	ClientData client = this.clientReadPlatformService.retrieveOne(Long.valueOf(ref.getExternalId()));
        	System.out.println(client);
        	if(!(client.imageKeyDoesNotExist())){
        		ref.setClientImageData(this.clientImagesApiResource.retrieveClientImage(Long.valueOf(ref.getExternalId())));
        	}
		}
        
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, referals, RESPONSE_DATA_PARAMETERS);
    }
    
    @GET 
    @Path("{clientId}/hierarchy")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReferalsByClient(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        
        ReferalData referalData =  this.referalReadPlatformService.retrieveReferalWithExternal(clientId.toString());
    	if(referalData == null){
    		throw new ReferalNotFoundException(clientId);
    	}
    	
        final Collection<ReferalData> referals = this.readPlatformService.retrieveAllReferals(clientId, referalData.getHierarchy());
        Iterator<ReferalData> itr = referals.iterator();
        
        while(itr.hasNext()){
        	ReferalData ref = itr.next();
        	ClientData client = this.clientReadPlatformService.retrieveOne(Long.valueOf(ref.getExternalId()));
        	System.out.println(client);
        	if(!(client.imageKeyDoesNotExist())){
        		ref.setClientImageData(this.clientImagesApiResource.retrieveClientImage(Long.valueOf(ref.getExternalId())));
        	}
		}
        
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, referals, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveReferalTemplate(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        ReferalData referal = this.readPlatformService.retrieveNewReferalTemplate();
        final Collection<ReferalData> allowedParents = this.readPlatformService.retrieveAllReferalsForDropdown();
        final Collection<CodeValueData> officeTypes=this.codeValueReadPlatformService.retrieveCodeValuesByCode(OFFICE_TYPE);
        referal = ReferalData.appendedTemplate(referal, allowedParents,officeTypes);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, referal, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createReferal(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createReferal() //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{referalId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveReferal(@PathParam("referalId") final Long referalId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        ReferalData referal = this.readPlatformService.retrieveReferal(referalId);
        
        if (settings.isTemplate()) {
            final Collection<ReferalData> allowedParents = this.readPlatformService.retrieveAllowedParents(referalId);
            final Collection<CodeValueData> codeValueDatas=this.codeValueReadPlatformService.retrieveCodeValuesByCode(OFFICE_TYPE);
            referal = ReferalData.appendedTemplate(referal, allowedParents,codeValueDatas);
        }

        return this.toApiJsonSerializer.serialize(settings, referal, RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{referalId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateReferal(@PathParam("referalId") final Long referalId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateReferal(referalId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @GET
    @Path("cafId/{externalCode}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveReferalWithcode(@PathParam("externalCode") final String externalCode, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        ReferalData referal = this.readPlatformService.retrieveReferalWithExternalCode(externalCode);
        
        referal.setClientData(this.clientReadPlatformService.retrieveOne(Long.valueOf(referal.getExternalId())));

        return this.toApiJsonSerializer.serialize(settings, referal, RESPONSE_DATA_PARAMETERS);
    }
    
    
}