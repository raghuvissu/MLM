/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.api;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils.IMAGE_DATA_URI_SUFFIX;
import org.mifosplatform.infrastructure.core.service.FileUtils.IMAGE_FILE_EXTENSION;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.office.service.OfficeWritePlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.exception.ImageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.pdf.codec.Base64;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/offices")
@Component
@Scope("singleton")
public class OfficesApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link OfficeData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "name", "nameDecorated", "externalId",
            "openingDate", "hierarchy", "parentId", "parentName", "allowedParents","officeTypes"));

    private final String resourceNameForPermissions = "OFFICE";
    
    private final PlatformSecurityContext context;
    private final OfficeReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<OfficeData> toApiJsonSerializer;
    private final DefaultToApiJsonSerializer<FinancialTransactionsData> toFinancialTransactionApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final AddressReadPlatformService addressReadPlatformService;
    public static final String OFFICE_TYPE="Office Type";
    final private MCodeReadPlatformService mCodeReadPlatformService;
    private final OfficeWritePlatformService officeWritePlatformService;

    @Autowired
    public OfficesApiResource(final PlatformSecurityContext context, final OfficeReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<OfficeData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService, final DefaultToApiJsonSerializer<FinancialTransactionsData> toFinancialTransactionApiJsonSerializer,
            final AddressReadPlatformService addressReadPlatformService,final MCodeReadPlatformService mCodeReadPlatformService,
            final OfficeWritePlatformService officeWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.toFinancialTransactionApiJsonSerializer = toFinancialTransactionApiJsonSerializer;
        this.addressReadPlatformService = addressReadPlatformService;
        this.mCodeReadPlatformService = mCodeReadPlatformService;
        this.officeWritePlatformService = officeWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOffices(@Context final UriInfo uriInfo, @QueryParam("city") final String city) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        final Collection<OfficeData> offices = this.readPlatformService.retrieveAllOffices(city);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, offices, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOfficeTemplate(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        OfficeData office = this.readPlatformService.retrieveNewOfficeTemplate();
        final Collection<OfficeData> allowedParents = this.readPlatformService.retrieveAllOfficesForDropdown();
        final Collection<CodeValueData> officeTypes=this.codeValueReadPlatformService.retrieveCodeValuesByCode(OFFICE_TYPE);
        office = OfficeData.appendedTemplate(office, allowedParents,officeTypes);
        office = this.handleAddressTemplateData(office);
        office.setBusinessTypes(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.BUSINESS_TYPE));
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, office, RESPONSE_DATA_PARAMETERS);
    }

    private OfficeData handleAddressTemplateData(final OfficeData officeData) {
    	
   	 final List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
        final List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
        final List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
        final List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService.addressType();
        final AddressData data=new AddressData(null,countryData,statesData,citiesData,enumOptionDatas);
        officeData.setAddressData(data);
        return officeData;
	}
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createOffice(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createOffice() //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{officeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveOffice(@PathParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        OfficeData office = this.readPlatformService.retrieveOffice(officeId);
        
        if (settings.isTemplate()) {
            final Collection<OfficeData> allowedParents = this.readPlatformService.retrieveAllowedParents(officeId);
            final Collection<CodeValueData> codeValueDatas=this.codeValueReadPlatformService.retrieveCodeValuesByCode(OFFICE_TYPE);
            office = OfficeData.appendedTemplate(office, allowedParents,codeValueDatas);
            office.setBusinessTypes(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.BUSINESS_TYPE));
          /*  office.setCountryData(this.addressReadPlatformService.retrieveCountryDetails());
        	office.setCitiesData(this.addressReadPlatformService.retrieveCityDetails());
        	office.setStatesData(this.addressReadPlatformService.retrieveStateDetails());*/
        }

        return this.toApiJsonSerializer.serialize(settings, office, RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Path("{officeId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateOffice(@PathParam("officeId") final Long officeId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateOffice(officeId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    /**
     * Office financial Transactions
     * */
    @GET
    @Path("financialtransactions/{officeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTransactionalData(@PathParam("officeId") final Long officeId, @Context final UriInfo uriInfo)	{
    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
    	final Collection<FinancialTransactionsData> transactionData = this.readPlatformService.retreiveOfficeFinancialTransactionsData(officeId);
    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toFinancialTransactionApiJsonSerializer.serialize(settings, transactionData, RESPONSE_DATA_PARAMETERS);
    }
    
    /**
     * Upload images through multi-part form upload
     */
    @POST
    @Path("upload/images/{officeId}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String addNewOfficeImages(@PathParam("officeId") final Long officeId, @HeaderParam("Content-Length") final Long fileSize,
    		final FormDataMultiPart multiPart){
    		//@FormDataParam("file") List<FormDataBodyPart> bodyParts, @FormDataParam("file") FormDataContentDisposition fileDispositions) {

        // TODO: vishwas might need more advances validation (like reading magic
        // number) for handling malicious clients
        // and clients not setting mime type
    	CommandProcessingResult result = null;
    	List<FormDataBodyPart> bodyParts = multiPart.getFields("file");
    	
    	for (FormDataBodyPart part : bodyParts) {
    	    FormDataContentDisposition file = part.getFormDataContentDisposition();
    	    InputStream is = part.getEntityAs(InputStream.class);
            
            FileUtils.validateOfficeImageNotEmpty(file.getFileName());
            FileUtils.validateImageMimeType(part.getMediaType().toString());
            //FileUtils.validateFileSizeWithinPermissibleRange(file.getSize(), file.getFileName(), ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);

             result = this.officeWritePlatformService.saveOrUpdateOfficeImage(officeId, file.getFileName(),
                    is);
    	}
    	
        return this.toApiJsonSerializer.serialize(result);
    }
    
    /**
     * Returns a base 64 encoded client image Data URI
     */
    // array of supported extensions 
    static final String[] EXTENSIONS = new String[] { "jpg", "jpeg", "gif", "png", "JPG", "JPEG", "GIF", "PNG" };

    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    
    @GET
    @Path("images/{officeId}")
    @Consumes({ MediaType.TEXT_PLAIN, MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN })
    public Response retrieveClientImage(@PathParam("officeId") final Long officeId) {
    	
    	final String folderLocation = FileUtils.generateOfficeImageParentDirectory(officeId);
    	
        File dir = new File(folderLocation);
        Map<String,String> newfiles = new HashMap<> ();
        if(dir.exists()){
        File [] files = dir.listFiles(IMAGE_FILTER);
        //Fills a list (from the more recent one, until the last known file)
        
	        for (File f : files)
	        {
	            
	            String imageLocation = folderLocation + File.separator + f.getName();
	            System.out.println("imageLocation"+imageLocation);
	
	            // TODO: Need a better way of determining image type
	            String imageDataURISuffix = IMAGE_DATA_URI_SUFFIX.JPEG.getValue();
	            if (StringUtils.endsWith(imageLocation, IMAGE_FILE_EXTENSION.GIF.getValue())) {
	                imageDataURISuffix = IMAGE_DATA_URI_SUFFIX.GIF.getValue();
	            } else if (StringUtils.endsWith(imageLocation, IMAGE_FILE_EXTENSION.PNG.getValue())) {
	                imageDataURISuffix = IMAGE_DATA_URI_SUFFIX.PNG.getValue();
	            }
	
	            String imageAsBase64Text = imageDataURISuffix + Base64.encodeFromFile(imageLocation);
	            System.out.println(f.getName());
	            newfiles.put(f.getName(), imageAsBase64Text);
	        }
        }
        
        ResponseBuilder response = Response.ok(newfiles);
        response.header("Content-Type", MediaType.APPLICATION_JSON);
        //Answers the list as a JSON array (using google-gson, but could be done manually here)
        return response.build();
        
    }
    
    @DELETE
    @Path("image/{officeId}/{fileName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteClientImage(@PathParam("officeId") final Long officeId, @PathParam("fileName") final String fileName) {
        this.officeWritePlatformService.deleteOfficeImageByName(officeId, fileName);
        return this.toApiJsonSerializer.serialize(new CommandProcessingResult(officeId));
    }
    
   
    
    
    
    
    
}