/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.referal.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAddressRepository;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.referal.data.ReferalData;
import org.mifosplatform.organisation.referal.domain.Referal;
import org.mifosplatform.organisation.referal.domain.ReferalRepository;
import org.mifosplatform.organisation.referal.exception.ReferalNotFoundException;
import org.mifosplatform.organisation.referal.serialization.ReferalCommandFromApiJsonDeserializer;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferalWritePlatformServiceJpaRepositoryImpl implements ReferalWritePlatformService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReferalWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final ReferalCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final ReferalRepository referalRepository;
    private final OfficeAddressRepository addressRepository;
    private final OfficeRepository officeRepository;
    private final ReferalReadPlatformService  referalReadPlatformService;

    @Autowired
    public ReferalWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final ReferalCommandFromApiJsonDeserializer fromApiJsonDeserializer, final ReferalRepository referalRepository, 
            final OfficeAddressRepository addressRepository, final OfficeRepository officeRepository,
            final ReferalReadPlatformService  referalReadPlatformService) {
    	
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.referalRepository = referalRepository;
        this.addressRepository = addressRepository;
        this.officeRepository = officeRepository;
        this.referalReadPlatformService = referalReadPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult createReferal(final JsonCommand command) {

        try {
            final AppUser currentUser = context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            Long parentId = null;
            if (command.parameterExists("parentId")) {
                parentId = command.longValueOfParameterNamed("parentId");
            }
            
            Referal parent = null;
            
            if(parentId != null){
            	Long officeId = command.longValueOfParameterNamed("officeId");
            	parent = validateUserPriviledgeOnReferalAndRetrieve(officeId, parentId);
            }
            
           // Referal parent = validateUserPriviledgeOnReferalAndRetrieve(currentUser, parentId);
            final Referal referal = Referal.fromJson(parent, command);
            
            // pre save to generate id for use in referal hierarchy
            this.referalRepository.save(referal);

            referal.generateHierarchy();

            this.referalRepository.save(referal);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(referal.getId()) //
                    .build();
        } catch (DataIntegrityViolationException dve) {
            handleOfficeDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateReferal(final Long referalId, final JsonCommand command) {

        try {
            final AppUser currentUser = context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            Long parentId = null;
            if (command.parameterExists("parentId")) {
                parentId = command.longValueOfParameterNamed("parentId");
            }

            Long officeId = command.longValueOfParameterNamed("officeId");
            final Referal referal = validateUserPriviledgeOnReferalAndRetrieve(officeId, referalId);

            final Map<String, Object> changes = referal.update(command);

            if (changes.containsKey("parentId")) {
                final Referal parent = validateUserPriviledgeOnReferalAndRetrieve(officeId, parentId);
                referal.update(parent);
            }
            
            if (!changes.isEmpty()) {
                this.referalRepository.saveAndFlush(referal);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(referal.getId()) //
                    .with(changes) //
                    .build();
        } catch (DataIntegrityViolationException dve) {
            handleOfficeDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

   
    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleOfficeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("externalid_refrl")) {
            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.referal.duplicate.externalId", "Referal with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
        } else if (realCause.getMessage().contains("name_refrl")) {
            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("error.msg.referal.duplicate.name", "Referal with name `" + name + "` already exists",
                    "name", name);
        }

        LOGGER.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    /*
     * used to restrict modifying operations to office that are either the users
     * office or lower (child) in the office hierarchy
     */
    public Referal validateUserPriviledgeOnReferalAndRetrieve(final Long officeId, final Long referalId) {

        ReferalData parentReferalData = this.referalReadPlatformService.retrieveParentReferals(officeId);
        final Referal userReferal = this.referalRepository.findOne(parentReferalData.getId());
        if (userReferal == null) { throw new ReferalNotFoundException(parentReferalData.getId()); }

        if (userReferal.doesNotHaveAnReferalInHierarchyWithId(referalId)) { throw new NoAuthorizationException(
                "User does not have sufficient priviledges to act on the provided office."); }

        Referal referalToReturn = userReferal;
        if (!userReferal.identifiedBy(referalId)) {
        	referalToReturn = this.referalRepository.findOne(referalId);
            if (referalToReturn == null) { throw new ReferalNotFoundException(referalId); }
        }

        return referalToReturn;
    }
    
}