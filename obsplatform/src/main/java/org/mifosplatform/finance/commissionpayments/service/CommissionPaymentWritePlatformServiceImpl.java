
package org.mifosplatform.finance.commissionpayments.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.commissionpayments.domain.CommissionPayment;
import org.mifosplatform.finance.commissionpayments.domain.CommissionPaymentRepository;
import org.mifosplatform.finance.commissionpayments.serialization.CommissionPaymentCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistributionRepository;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefund;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefundRepository;
import org.mifosplatform.finance.payments.domain.ChequePayment;
import org.mifosplatform.finance.payments.domain.ChequePaymentRepository;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.domain.PaymentRepository;
import org.mifosplatform.finance.payments.domain.PaypalEnquireyRepository;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfoRepository;
import org.mifosplatform.organisation.partner.domain.OfficeControlBalance;
import org.mifosplatform.organisation.partner.domain.PartnerBalanceRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class CommissionPaymentWritePlatformServiceImpl implements CommissionPaymentWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(CommissionPaymentWritePlatformServiceImpl.class);
    private final String Paypal_method="paypal";
    private final String CreditCard_method="credit_card";
    private final String CreditCard="creditCard";
    private final String CreditCardToken="creditCardToken";
	
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private final CommissionPaymentRepository commissionPaymentRepository;
	private final BillItemRepository billItemRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final ChequePaymentRepository chequePaymentRepository;
	private final PaypalEnquireyRepository paypalEnquireyRepository;
	private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
	private final CommissionPaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService; 
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	private final ClientRepository clientRepository;
	private final PartnerBalanceRepository partnerBalanceRepository;
	private final OfficeAdditionalInfoRepository infoRepository;
	private final OrderWritePlatformService orderWritePlatformService;
	private final DepositAndRefundRepository depositAndRefundRepository;
	private final CreditDistributionRepository creditDistributionRepository;

	@Autowired
	public CommissionPaymentWritePlatformServiceImpl(final PlatformSecurityContext context,final CommissionPaymentRepository commissionPaymentRepository,
			final CommissionPaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer,final ClientBalanceRepository clientBalanceRepository,
			final ChequePaymentRepository chequePaymentRepository,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final BillItemRepository billItemRepository,
			final ConfigurationRepository globalConfigurationRepository,final PaypalEnquireyRepository paypalEnquireyRepository,
			final FromJsonHelper fromApiJsonHelper,final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository,
			final ClientRepository clientRepository,final PartnerBalanceRepository partnerBalanceRepository,
			final OfficeAdditionalInfoRepository infoRepository, final OrderWritePlatformService orderWritePlatformService,
			final DepositAndRefundRepository depositAndRefundRepository,
			final CreditDistributionRepository creditDistributionRepository) {
		
		this.context = context;
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.billItemRepository=billItemRepository;
		this.paymentGatewayConfigurationRepository=paymentGatewayConfigurationRepository;
		this.commissionPaymentRepository = commissionPaymentRepository;
		this.clientBalanceRepository=clientBalanceRepository;
		this.chequePaymentRepository=chequePaymentRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.paypalEnquireyRepository=paypalEnquireyRepository;
		this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService=actiondetailsWritePlatformService; 
		this.clientRepository = clientRepository;
		this.partnerBalanceRepository = partnerBalanceRepository;
		this.infoRepository = infoRepository;
		this.orderWritePlatformService = orderWritePlatformService;
		this.depositAndRefundRepository = depositAndRefundRepository;
		this.creditDistributionRepository = creditDistributionRepository;	
		
	}

	@Transactional
	@Override
	public CommandProcessingResult createPayment(final JsonCommand command) {
		
		try {
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromApiJsonHelper.parse(command.json());
			CommissionPayment payment  = CommissionPayment.fromJson(command,command.entityId());
			this.commissionPaymentRepository.saveAndFlush(payment);
			
			if(command.hasParameter("paymentType")){
				final String paymentType = command.stringValueOfParameterNamed("paymentType");
				if("Deposit".equalsIgnoreCase(paymentType)){
					JsonArray depositData = fromApiJsonHelper.extractJsonArrayNamed("deposit", element);
					for (JsonElement je : depositData) {
						final Long depositId = fromApiJsonHelper.extractLongNamed("depositId", je);
						final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", je);
						DepositAndRefund deopositAndRefund = this.depositAndRefundRepository.findOne(depositId);
						
						deopositAndRefund.setPaymentId(payment.getId());
						this.depositAndRefundRepository.saveAndFlush(deopositAndRefund);
						/*final DepositAndRefund refund = DepositAndRefund.fromJson(deopositAndRefund.getClientId(), 
								deopositAndRefund.getItemId(), amount);
						this.depositAndRefundRepository.saveAndFlush(refund);*/
					}
				}
			}
			
			if(command.stringValueOfParameterNamed("isChequeSelected").equalsIgnoreCase("yes")){
				final ChequePayment chequePayment = ChequePayment.fromJson(command);
				chequePayment.setPaymentId(payment.getId());
				chequePaymentRepository.save(chequePayment);
			}
			
			ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(payment.getClientId());
			
			if(clientBalance != null){
				clientBalance.updateBalance("CREDIT",payment.getDebitAmount(),'N');
			
			}else if(clientBalance == null){
                    BigDecimal balance=BigDecimal.ZERO.subtract(payment.getDebitAmount());
				clientBalance =ClientBalance.create(payment.getClientId(),balance,'N');
			}
			this.clientBalanceRepository.saveAndFlush(clientBalance);
			

		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(payment.getId()).withClientId(command.entityId()).build();

			
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			}
		}
	

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("receipt_no")){
		          throw new ReceiptNoDuplicateException(command.stringValueOfParameterNamed("receiptNo"));
		}
		
		logger.error(dve.getMessage(), dve);
	}
	

}



