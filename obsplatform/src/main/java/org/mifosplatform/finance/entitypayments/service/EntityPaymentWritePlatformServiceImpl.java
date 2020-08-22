
package org.mifosplatform.finance.entitypayments.service;

import org.mifosplatform.finance.entitypayments.domain.EntityPayment;
import org.mifosplatform.finance.entitypayments.domain.EntityPaymentRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EntityPaymentWritePlatformServiceImpl implements EntityPaymentWritePlatformService {

	private final PlatformSecurityContext context;
	private final EntityPaymentRepository entityPaymentRepository;

	@Autowired
	public EntityPaymentWritePlatformServiceImpl(final PlatformSecurityContext context,
			final EntityPaymentRepository entityPaymentRepository) {
		
		this.context = context;
		this.entityPaymentRepository = entityPaymentRepository;
		
	}

	@Transactional
   	@Override
   	public CommandProcessingResult createEntityPayment(final JsonCommand command) {
   		
		try {
           
           final EntityPayment entityPayment = EntityPayment.fromJson(command);
            
            this.entityPaymentRepository.save(entityPayment);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(entityPayment.getId()) //
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

        throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

}



