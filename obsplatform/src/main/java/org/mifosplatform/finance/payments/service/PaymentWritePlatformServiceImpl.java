
package org.mifosplatform.finance.payments.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.commissionpayments.service.CommissionPaymentWritePlatformService;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistribution;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistributionRepository;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefund;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefundRepository;
import org.mifosplatform.finance.entitypayments.data.EntityPaymentData;
import org.mifosplatform.finance.entitypayments.domain.EntityPayment;
import org.mifosplatform.finance.entitypayments.domain.EntityPaymentRepository;
import org.mifosplatform.finance.entitypayments.service.EntityPaymentReadPlatformService;
import org.mifosplatform.finance.payments.domain.ChequePayment;
import org.mifosplatform.finance.payments.domain.ChequePaymentRepository;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.domain.PaymentRepository;
import org.mifosplatform.finance.payments.domain.PaypalEnquirey;
import org.mifosplatform.finance.payments.domain.PaypalEnquireyRepository;
import org.mifosplatform.finance.payments.exception.PaymentDetailsNotFoundException;
import org.mifosplatform.finance.payments.exception.PaymentOTPNotFoundException;
import org.mifosplatform.finance.payments.exception.PaymentamountNotEqualToMinimumAmount;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.finance.payments.exception.WalletAmountExceedsException;
import org.mifosplatform.finance.payments.serialization.PaymentCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfoRepository;
import org.mifosplatform.organisation.partner.domain.OfficeControlBalance;
import org.mifosplatform.organisation.partner.domain.PartnerBalanceRepository;
import org.mifosplatform.organisation.referal.data.ReferalData;
import org.mifosplatform.organisation.referal.domain.Referal;
import org.mifosplatform.organisation.referal.domain.ReferalRepository;
import org.mifosplatform.organisation.referal.service.ReferalReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
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
import com.google.gson.JsonObject;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;


@Service
public class PaymentWritePlatformServiceImpl implements PaymentWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(PaymentWritePlatformServiceImpl.class);
    private final String Paypal_method="paypal";
    private final String CreditCard_method="credit_card";
    private final String CreditCard="creditCard";
    private final String CreditCardToken="creditCardToken";
	
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private final PaymentRepository paymentRepository;
	private final BillItemRepository billItemRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final ChequePaymentRepository chequePaymentRepository;
	private final PaypalEnquireyRepository paypalEnquireyRepository;
	private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
	private final PaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService; 
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	private final ClientRepository clientRepository;
	private final PartnerBalanceRepository partnerBalanceRepository;
	private final OfficeAdditionalInfoRepository infoRepository;
	private final OrderWritePlatformService orderWritePlatformService;
	private final DepositAndRefundRepository depositAndRefundRepository;
	private final CreditDistributionRepository creditDistributionRepository;
	private final PortfolioCommandSourceWritePlatformService  portfolioCommandSourceWritePlatformService;
	private final ReferalReadPlatformService  referalReadPlatformService;
	private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
	private final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer;
	private final ReferalRepository referalRepository;
	private final ClientReadPlatformService clientReadPlatformService;
	private final CommissionPaymentWritePlatformService commissionPaymentWritePlatformService;
	private final ConfigurationRepository configurationRepository;
	private final CodeValueReadPlatformService  codeValueReadPlatformService;
	private final EntityPaymentRepository entityPaymentRepository;
	private final EntityPaymentReadPlatformService entitypaymentReadPlatformService;

	@Autowired
	public PaymentWritePlatformServiceImpl(final PlatformSecurityContext context,final PaymentRepository paymentRepository,
			final PaymentCommandFromApiJsonDeserializer fromApiJsonDeserializer,final ClientBalanceRepository clientBalanceRepository,
			final ChequePaymentRepository chequePaymentRepository,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final BillItemRepository billItemRepository,
			final ConfigurationRepository globalConfigurationRepository,final PaypalEnquireyRepository paypalEnquireyRepository,
			final FromJsonHelper fromApiJsonHelper,final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository,
			final ClientRepository clientRepository,final PartnerBalanceRepository partnerBalanceRepository,
			final OfficeAdditionalInfoRepository infoRepository, final OrderWritePlatformService orderWritePlatformService,
			final DepositAndRefundRepository depositAndRefundRepository,
			final CreditDistributionRepository creditDistributionRepository,
			final PortfolioCommandSourceWritePlatformService  portfolioCommandSourceWritePlatformService,
			final ReferalReadPlatformService  referalReadPlatformService,
			final ReadWriteNonCoreDataService readWriteNonCoreDataService, final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer,
			final ReferalRepository referalRepository, final ClientReadPlatformService clientReadPlatformService,
			final CommissionPaymentWritePlatformService commissionPaymentWritePlatformService,
			final ConfigurationRepository configurationRepository, final CodeValueReadPlatformService  codeValueReadPlatformService,
			final EntityPaymentRepository entityPaymentRepository, final EntityPaymentReadPlatformService entitypaymentReadPlatformService) {
		
		this.context = context;
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.billItemRepository=billItemRepository;
		this.paymentGatewayConfigurationRepository=paymentGatewayConfigurationRepository;
		this.paymentRepository = paymentRepository;
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
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.referalReadPlatformService = referalReadPlatformService;
		this.readWriteNonCoreDataService = readWriteNonCoreDataService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.referalRepository = referalRepository;
		this.clientReadPlatformService = clientReadPlatformService;
		this.commissionPaymentWritePlatformService = commissionPaymentWritePlatformService;
		this.configurationRepository = configurationRepository;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.entityPaymentRepository = entityPaymentRepository;
		this.entitypaymentReadPlatformService = entitypaymentReadPlatformService;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult createPayment(final JsonCommand command) {
		
		try {
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final JsonElement element = fromApiJsonHelper.parse(command.json());
			
			//ClientData clientData = this.clientReadPlatformService.retrieveOne(command.entityId());
			
			final Long officeId=command.longValueOfParameterNamed("officeId");
			if(officeId != null){
				JSONArray row = retriveBasicValidationTableData(officeId);
	
				BigDecimal minimumAmount = (row == null) ? BigDecimal.ZERO : new BigDecimal(row.getDouble(3));
				BigDecimal totalPayment = command.bigDecimalValueOfParameterNamed("amountPaid");
				final boolean useWalletAmount = command.booleanPrimitiveValueOfParameterNamed("useWalletAmount");
				if(useWalletAmount){
					final BigDecimal walletAmount = command.bigDecimalValueOfParameterNamed("walletAmount");
					totalPayment = totalPayment.add(walletAmount);
				}
				
				if(totalPayment.compareTo(minimumAmount) == -1 ){
					throw new PaymentamountNotEqualToMinimumAmount(minimumAmount);
				}
			}
			
			Payment payment  = Payment.fromJson(command,command.entityId());
			this.paymentRepository.saveAndFlush(payment);
			
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
				clientBalance.updateBalance("CREDIT",payment.getAmountPaid(),payment.isWalletPayment());
			}else if(clientBalance == null){
                    BigDecimal balance=BigDecimal.ZERO.subtract(payment.getAmountPaid());
				clientBalance =ClientBalance.create(payment.getClientId(),balance,payment.isWalletPayment());
			}
			this.clientBalanceRepository.saveAndFlush(clientBalance);
			
			if(payment.getUseWalletAmount() == 'Y'){
				BigDecimal amountPaid = payment.getAmountPaid().add(payment.getWalletAmount());
				payment.setAmountPaid(amountPaid);
				this.paymentRepository.saveAndFlush(payment);
			}
			
			//Update BillItem Amount
			if(payment.getInvoiceId() != null){
				
				final BillItem invoice=this.billItemRepository.findOne(payment.getInvoiceId());
				invoice.updateAmount(payment.getAmountPaid());
				this.billItemRepository.save(invoice);
				
			}
			
			// Notify Payment Details to Clients. written by ashok
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_PAYMENT, payment.getClientId(), payment.getAmountPaid().toString(), null);
			
			//Add New Action 
			final List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_SEND_PAYMENT);
				if(actionDetaislDatas.size() != 0){
					this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,command.entityId(),payment.getId().toString(),null);
				}
				
				final Client client=this.clientRepository.findOne(payment.getClientId());
				//final CodeValue codeValue=this.codeValueRepository.findOne(client.getOffice().getOfficeType());
				final OfficeAdditionalInfo officeAdditionalInfo=this.infoRepository.findoneByoffice(client.getOffice());
				if(officeAdditionalInfo !=null){
					if(officeAdditionalInfo.getIsCollective())
					{
					this.updatePartnerBalance(client.getOffice(),payment);
				   }
				}

				return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(payment.getId()).withClientId(command.entityId()).build();

			
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			}catch (JSONException e) {
	        	   return CommandProcessingResult.empty();
			}
		}
	
	@Transactional
	@Override
	public CommandProcessingResult createReferalWithdrawPayment(final JsonCommand command, final Long clientId) {
		
		try {
			
			this.context.authenticatedUser();
			
			final BigDecimal debitAmount = command.bigDecimalValueOfParameterNamed("debitAmount");
			
			ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(clientId);
			
			if(debitAmount == null || (debitAmount.compareTo(clientBalance.getWalletAmount())) == 1 || debitAmount.equals(BigDecimal.ZERO)){
				throw new WalletAmountExceedsException(debitAmount);
			}
			
			this.commissionPaymentWritePlatformService.createPayment(command);
			
			ReferalData referalData =  this.referalReadPlatformService.retrieveReferalWithExternal(clientId.toString());
			Referal referalRecord = this.referalRepository.findOne(referalData.getId());
			
			if(referalData != null && referalRecord != null){
				
					BigDecimal totalAmount = referalRecord.getTotalAmount();
					totalAmount = totalAmount.subtract(debitAmount);
					referalRecord.setTotalAmount(totalAmount);
					this.referalRepository.saveAndFlush(referalRecord);
				
			}
			
			final Long officeId=command.longValueOfParameterNamed("officeId");
			EntityPaymentData retrieveEntityPayment = this.entitypaymentReadPlatformService.retriveEntityPaymentsData(clientId, officeId);
			if(retrieveEntityPayment != null){
				EntityPayment entityPayment = this.entityPaymentRepository.findOne(retrieveEntityPayment.getId());
				BigDecimal totalPayments = retrieveEntityPayment.getOfficePaymentAmount();
				totalPayments = totalPayments.add(debitAmount);
				entityPayment.setOfficePaymentAmount(totalPayments);
				
				this.entityPaymentRepository.save(entityPayment);
			}
			
			
			if(clientBalance != null){
				clientBalance.updateBalance("CREDIT",debitAmount,'Y');
			}
			
			this.clientBalanceRepository.saveAndFlush(clientBalance);
			
				return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(clientId).withClientId(command.entityId()).build();

			
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			} 
		}
	
	private void commissionAmountSave(final Long paymentCode, Long clientId, Payment payment, BigDecimal commissionFixedAmount, int level, ClientData fromClient, boolean useWalletAmount, BigDecimal walletAmount) throws JSONException{
		ReferalData referalData =  this.referalReadPlatformService.retrieveReferalWithExternal(clientId.toString());
		ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
		
		if(referalData != null){
			
			JSONArray row = retriveCommissionTableData(payment.getOfficeId(), level-1);
			
			Long rowVal = (row == null) ? Long.valueOf(0) : row.getLong(4);
			
			BigDecimal commissionAmount = commissionFixedAmount.multiply((BigDecimal.valueOf(rowVal).divide(BigDecimal.valueOf(Long.valueOf(100)))));
			
			if(commissionAmount.signum() == -1){
				commissionAmount = BigDecimal.ZERO;
			}
			
			if(commissionAmount.compareTo(BigDecimal.ZERO) == 1 || useWalletAmount){
		
			final JSONObject commissionCreation = new JSONObject();
			commissionCreation.put("paymentDate", DateUtils.formatToSqlDate(payment.getCreatedDate().toDate()));
			commissionCreation.put("paymentCode", paymentCode);
			if(useWalletAmount && (level == Long.valueOf(1))){
				commissionCreation.put("debitAmount", walletAmount);
			}else commissionCreation.put("debitAmount", BigDecimal.ZERO);
			commissionCreation.put("creditAmount", commissionAmount);
			commissionCreation.put("rate", rowVal);
			commissionCreation.put("clientName", fromClient.getDisplayName());
			commissionCreation.put("remarks", payment.getRemarks());
			commissionCreation.put("receiptNo", payment.getReceiptNo());
			commissionCreation.put("dateFormat", "yyyy-MM-dd");
			commissionCreation.put("locale", "en");
			commissionCreation.put("officeId", payment.getOfficeId());
        	
        	final CommandWrapper commandRequest = new CommandWrapperBuilder().createCommissionPayment(clientId)
					.withJson(commissionCreation.toString()).build();
			this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
			
			}
			
			Referal referalRecord = this.referalRepository.findOne(referalData.getId());
			
			if(Long.valueOf(referalRecord.getExternalId()) == payment.getClientId()){
				BigDecimal totalPayments = referalRecord.getTotalPayments();
				totalPayments = totalPayments.add(payment.getAmountPaid());
				referalRecord.setTotalPayments(totalPayments);
			}
			
			if(referalRecord != null){
				BigDecimal totalAmount = referalRecord.getTotalAmount();
				totalAmount = totalAmount.add(commissionAmount);
				
				if(useWalletAmount && (level == Long.valueOf(1))){
					totalAmount = totalAmount.subtract(walletAmount);
				}
				
				referalRecord.setTotalAmount(totalAmount);
				if(level == Long.valueOf(1)){
					referalRecord.setFlag(1);
				}else{
					
					BigDecimal totalPaymentsByReferal = referalRecord.getTotalPaymentsByReferals();
					totalPaymentsByReferal = totalPaymentsByReferal.add(payment.getAmountPaid());
					referalRecord.setTotalPaymentsByReferals(totalPaymentsByReferal);
					referalRecord.setFlag(2);
				}
				this.referalRepository.saveAndFlush(referalRecord);
				
				ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(clientData.getId());
				
				if(clientBalance != null){
					clientBalance.updateBalance("ADD",commissionAmount,'Y');
					this.clientBalanceRepository.saveAndFlush(clientBalance);
				}
				
				
			}
			
			EntityPaymentData retrieveEntityPayment = this.entitypaymentReadPlatformService.retriveEntityPaymentsData(clientId, payment.getOfficeId());
			if(retrieveEntityPayment == null){
				BigDecimal debitAmnt = BigDecimal.ZERO;
				if(useWalletAmount && (level == Long.valueOf(1))){
					debitAmnt = walletAmount;
				}
				EntityPayment entityPaymentData = new EntityPayment(clientId, payment.getOfficeId(), fromClient.getDisplayName(), debitAmnt, commissionAmount,
						commissionAmount.subtract(debitAmnt), BigDecimal.ZERO);
				this.entityPaymentRepository.save(entityPaymentData);
			}else if(retrieveEntityPayment != null){
				EntityPayment entityPayment = this.entityPaymentRepository.findOne(retrieveEntityPayment.getId());
				BigDecimal drAmount = retrieveEntityPayment.getTotalOfficeDR();
				if(useWalletAmount && (level == Long.valueOf(1))){
					drAmount = drAmount.add(walletAmount);
				}
				BigDecimal crAmount = retrieveEntityPayment.getTotalOfficeCR();
				crAmount = crAmount.add(commissionAmount);
				BigDecimal totalOfcAmount = (crAmount).subtract(drAmount);
				entityPayment.setTotalOfficeDR(drAmount);
				entityPayment.setTotalOfficeCR(crAmount);
				entityPayment.setTotalOfficeAmount(totalOfcAmount);
				
				if(Long.valueOf(referalRecord.getExternalId()) == payment.getClientId()){
					BigDecimal totalPayments = retrieveEntityPayment.getOfficePaymentAmount();
					totalPayments = totalPayments.add(payment.getAmountPaid());
					entityPayment.setOfficePaymentAmount(totalPayments);
				}
				
				this.entityPaymentRepository.save(entityPayment);
			}
			
			if(referalData.getParentId() != null){
				Referal referal = this.referalRepository.findOne(referalData.getParentId());
				if(referal != null){
					level = level + 1;
					commissionAmountSave(paymentCode, Long.valueOf(referal.getExternalId()), payment, commissionFixedAmount, level, fromClient, false, BigDecimal.ZERO);
				}
			}
			 
		
		}
		
	}
	
   private JSONArray retriveCommissionAmountTableData(final Long officeId) throws JSONException{
    	
    	final GenericResultsetData results = this.readWriteNonCoreDataService
				.retrieveDataTableGenericResultSet("Commission_amount", officeId);
    	
		String json = this.toApiJsonSerializer.serializePretty(true, results);
		JSONArray dataArr = new JSONObject(json).getJSONArray("data");
		JSONObject jsonObj = null;
		 if(dataArr.length() > 0){
			 jsonObj = new JSONObject(json).getJSONArray("data").getJSONObject(0);
		 }
		
		JSONArray row = null;
		if(jsonObj != null) row = jsonObj.getJSONArray("row");
		
		return row;
    	
    }
   
   private JSONArray retriveCommissionTableData(final Long officeId, final int level) throws JSONException{
   	
   	final GenericResultsetData results = this.readWriteNonCoreDataService
				.retrieveDataTableGenericResultSet("commission", officeId);
   	
		String json = this.toApiJsonSerializer.serializePretty(true, results);
		
		JSONArray array = new JSONObject(json).getJSONArray("data");
		
		JSONArray row = null;
		if(array != null && array.length() > level) 
		row = new JSONObject(json).getJSONArray("data").getJSONObject(level).getJSONArray("row");
		
		return row;
   	
   }
   
   private JSONArray retriveBasicValidationTableData(final Long officeId) throws JSONException{
   	
   	final GenericResultsetData results = this.readWriteNonCoreDataService
				.retrieveDataTableGenericResultSet("Basic Validation", officeId);
   	
		String json = this.toApiJsonSerializer.serializePretty(true, results);
		
		JSONArray dataArr = new JSONObject(json).getJSONArray("data");
		JSONObject jsonObj = null;
		 if(dataArr.length() > 0){
			 jsonObj = new JSONObject(json).getJSONArray("data").getJSONObject(0);
		 }
		
		JSONArray row = null;
		if(jsonObj != null) row = jsonObj.getJSONArray("row");
   	
		return row;
   	
   }
   
    @Transactional
	@Override
	public CommandProcessingResult confirmPayment(final Long paymentId, final String otp) {
		
		try {
			
			this.context.authenticatedUser();
			Payment payment = this.paymentRepository.findOne(paymentId);
			if(payment == null){
				throw new PaymentDetailsNotFoundException(paymentId.toString());
			}
			
			if(payment.getOtp() != null){
				if(!payment.getOtp().equals(otp)){
					throw new PaymentOTPNotFoundException(otp);
				}
			}
			
			payment.setStatus(1);
			this.paymentRepository.saveAndFlush(payment);
			
			List<Referal> allReferals = this.referalRepository.findAll();
			
			ListIterator<Referal> itr = allReferals.listIterator();
			while(itr.hasNext()){
				Referal ref = itr.next();
				ref.setFlag(0);
				this.referalRepository.saveAndFlush(ref);
			}
			
			ClientData clientData = this.clientReadPlatformService.retrieveOne(payment.getClientId());
			
			boolean useWallet = payment.getUseWalletAmount() == 'Y';
			BigDecimal walletAmount = BigDecimal.ZERO;
			if(useWallet){
				walletAmount = payment.getWalletAmount();
			}
			
			JSONArray row = retriveCommissionAmountTableData(payment.getOfficeId());
			
			if(row != null){
				CodeValueData codeValueData = this.codeValueReadPlatformService.retrieveCodeValue(row.getLong(1));
				
				if("Percentage".equalsIgnoreCase(codeValueData.getName())){
					BigDecimal commissionAmount = payment.getAmountPaid().multiply((BigDecimal.valueOf(row.getDouble(2)).divide(BigDecimal.valueOf(Long.valueOf(100)))));
					commissionAmountSave(Long.valueOf(payment.getPaymodeCode()), payment.getClientId(), payment, commissionAmount, 1, clientData, useWallet, walletAmount);
				}else if("Flat".equalsIgnoreCase(codeValueData.getName())){
					commissionAmountSave(Long.valueOf(payment.getPaymodeCode()), payment.getClientId(), payment, new BigDecimal(row.getDouble(2)), 1, clientData, useWallet, walletAmount);
				}
			}else{
				EntityPaymentData retrieveEntityPayment = this.entitypaymentReadPlatformService.retriveEntityPaymentsData(payment.getClientId(), payment.getOfficeId());
				if(retrieveEntityPayment != null){
					EntityPayment entityPayment = this.entityPaymentRepository.findOne(retrieveEntityPayment.getId());
					BigDecimal drAmount = retrieveEntityPayment.getTotalOfficeDR();
					if(useWallet){
						drAmount = drAmount.add(walletAmount);
					}
					BigDecimal crAmount = retrieveEntityPayment.getTotalOfficeCR();
					BigDecimal totalOfcAmount = (crAmount).subtract(drAmount);
					entityPayment.setTotalOfficeDR(drAmount);
					entityPayment.setTotalOfficeAmount(totalOfcAmount);
					
					BigDecimal totalPayments = retrieveEntityPayment.getOfficePaymentAmount();
					totalPayments = totalPayments.add(payment.getAmountPaid());
					entityPayment.setOfficePaymentAmount(totalPayments);
					
					this.entityPaymentRepository.save(entityPayment);
				}
			}
			
			ClientBalance clientBalance=this.clientBalanceRepository.findByClientId(payment.getClientId());
			
			if(useWallet){
				
				if(clientBalance != null){
					clientBalance.updateBalance("CREDIT",walletAmount,'Y');
					this.clientBalanceRepository.saveAndFlush(clientBalance);
				}
			}

			return new CommandProcessingResult(payment.getId(),payment.getClientId());

			
			
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
			} catch (JSONException e) {
			// TODO Auto-generated catch block
				return CommandProcessingResult.empty();
			}
		}
    
   
	private void updatePartnerBalance(final Office office,final Payment payment) {

		final String accountType = "PAYMENTS";
		OfficeControlBalance partnerControlBalance = this.partnerBalanceRepository.findOneWithPartnerAccount(office.getId(), accountType);
		if (partnerControlBalance != null) {
			partnerControlBalance.update(payment.getAmountPaid(), office.getId());

		} else {
			partnerControlBalance = OfficeControlBalance.create(payment.getAmountPaid(), accountType,office.getId());
		}

		this.partnerBalanceRepository.save(partnerControlBalance);
	}

	@Override
	public CommandProcessingResult cancelPayment(final JsonCommand command,final Long paymentId) {
		
		try{
			this.fromApiJsonDeserializer.validateForCancel(command.json());
			final Payment payment=this.paymentRepository.findOne(paymentId);
			if(payment == null){
				throw new PaymentDetailsNotFoundException(paymentId.toString());
			}
           final Payment cancelPay=new Payment(payment.getClientId(), null, null, payment.getAmountPaid(), null, DateUtils.getLocalDateOfTenant(),payment.getRemarks(), 
					   payment.getPaymodeId(),null,payment.getReceiptNo(),null,payment.isWalletPayment(),payment.getIsSubscriptionPayment(), payment.getId());
			cancelPay.cancelPayment(command);
			this.paymentRepository.save(cancelPay);
			payment.cancelPayment(command);
			this.paymentRepository.save(payment);
			payment.cancelPayment(command);
			this.paymentRepository.save(payment);
			final ClientBalance clientBalance = clientBalanceRepository.findByClientId(payment.getClientId());
			clientBalance.setBalanceAmount(payment.getAmountPaid(),payment.isWalletPayment());
			this.clientBalanceRepository.save(clientBalance);
			
			List<CreditDistribution> listOfInvoices = creditDistributionRepository.findOneByPaymentId(paymentId);
			if(listOfInvoices != null){
				
				for(CreditDistribution crd :listOfInvoices){
					
					Long invoiceId = crd.getInvoiceId();
					BigDecimal amount = crd.getAmount();
					
					BillItem invoiceData = this.billItemRepository.findOne(invoiceId);
					BigDecimal dueAmount = invoiceData.getDueAmount();
					BigDecimal updateAmount = dueAmount.add(amount);
					invoiceData.setDueAmount(updateAmount);
					this.billItemRepository.saveAndFlush(invoiceData);
					
				}
				
			}
				
			
			return new CommandProcessingResult(cancelPay.getId(),payment.getClientId());
			
			
		}catch(DataIntegrityViolationException exception){
			handleDataIntegrityIssues(command, exception);
			return CommandProcessingResult.empty();
		}
		
	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("receipt_no")){
		          throw new ReceiptNoDuplicateException(command.stringValueOfParameterNamed("receiptNo"));
		}
		
		logger.error(dve.getMessage(), dve);
	}
	
	private void DataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve, final String paymentid) {
		final Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("payment_id")){
		 throw new PlatformDataIntegrityException("error.msg.payments.paypalEnquirey.duplicate.paymentId", "A code with paymentId '"
                  + paymentid + "' already exists", "displayName", paymentid);
	}
		
		logger.error(dve.getMessage(), dve);
	}

	@Transactional
	@Override
	/*public CommandProcessingResult paypalEnquirey(final JsonCommand command) {

		try{
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForpaypalEnquirey(command.json());
					
			final JSONObject obj=new JSONObject(command.json()).getJSONObject("response");
			final SimpleDateFormat pattern= new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");	
			
			final String state=obj.getString("state");
			final String paymentid=obj.getString("id");
			final String create_time=obj.getString("create_time");
			final Date date= pattern.parse(create_time);
			
			final PaypalEnquirey  paypalEnquirey = new PaypalEnquirey(command.entityId(),state,paymentid,date);			
			this.paypalEnquireyRepository.save(paypalEnquirey);
			
			final JsonObject paymentobject = new JsonObject();
			final Map<String, Object> changes = new HashMap<String, Object>();
			 ClientBalance clientBalance = clientBalanceRepository.findByClientId(paypalEnquirey.getClientId());
			
			final PaypalEnquirey paypalEnquireyUpdate = this.paypalEnquireyRepository.findOne(paypalEnquirey.getId());
			
			try{
				
				 sendingDataToPaypal(paypalEnquirey.getId());
				 
			}catch (PayPalRESTException e) {
				
				final PaypalEnquirey paypalexceptionupdate=this.paypalEnquireyRepository.findOne(paypalEnquirey.getId());
				paypalexceptionupdate.setDescription(e.getLocalizedMessage());
				paypalexceptionupdate.setStatus("Fail");
				this.paypalEnquireyRepository.save(paypalexceptionupdate);
				changes.put("paymentId", new Long(-1));
				changes.put("paymentStatus", "Fail");
				changes.put("totalBalance", clientBalance == null?0:clientBalance.getBalanceAmount());
				changes.put("paypalException", e.getMessage());
				return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paypalEnquirey.getId()).with(changes).build();
			} 
			 
			final String remarks=paypalEnquireyUpdate.getPayerEmailId()!=null?paypalEnquireyUpdate.getPayerEmailId():" "+
			                          paypalEnquireyUpdate.getCardNumber()!=null?paypalEnquireyUpdate.getCardNumber():" ";
				
			    final String paymentdate = new SimpleDateFormat("dd MMMM yyyy").format(paypalEnquireyUpdate.getPaymentDate());
				
				paymentobject.addProperty("txn_id", paypalEnquireyUpdate.getPaymentId());
				paymentobject.addProperty("dateFormat", "dd MMMM yyyy");
				paymentobject.addProperty("locale", "en");
				paymentobject.addProperty("paymentDate", paymentdate);
				paymentobject.addProperty("amountPaid", paypalEnquireyUpdate.getTotalAmount());
				paymentobject.addProperty("isChequeSelected", "no");
				paymentobject.addProperty("receiptNo", paypalEnquireyUpdate.getPaymentId());
				paymentobject.addProperty("remarks", remarks);
				paymentobject.addProperty("paymentCode", 27);
				final String entityName = "PAYMENT";
				
				final JsonElement element1 = fromApiJsonHelper.parse(paymentobject.toString());
				JsonCommand comm = new JsonCommand(null,paymentobject.toString(), element1,fromApiJsonHelper, entityName, 
						paypalEnquirey.getClientId(),null, null, null, null, null, null, null, null,null,null);

				final CommandProcessingResult result = createPayment(comm);
				 clientBalance =clientBalanceRepository.findByClientId(paypalEnquirey.getClientId());
				if (result.resourceId() != null) {
					final int i = new Long(0).compareTo(result.resourceId());
					if (i == -1) {
						paypalEnquireyUpdate.setStatus("Success");
						paypalEnquireyUpdate.setObsPaymentId(result.resourceId());
						changes.put("paymentId", result.resourceId());
						changes.put("paymentStatus", "Success");
						changes.put("totalBalance",clientBalance.getBalanceAmount());
						changes.put("paypalException", "");
						changes.put("Errordescription", "");

					} else {
						paypalEnquireyUpdate.setStatus("Fail");
						paypalEnquireyUpdate.setObsPaymentId(result.resourceId());
						changes.put("paymentId", result.resourceId());
						changes.put("paymentStatus", "Fail");
						changes.put("totalBalance",clientBalance.getBalanceAmount());
						changes.put("paypalException", "");
						changes.put("Errordescription", "Payment Error");
					}
				} else {
					paypalEnquireyUpdate.setStatus("Failure");
					changes.put("paymentId", "");
					changes.put("paymentStatus", "Failure");
					changes.put("totalBalance",clientBalance.getBalanceAmount());
					changes.put("paypalException", "");
					changes.put("Errordescription", "Payment Not Processed");
				}
				
				this.paypalEnquireyRepository.save(paypalEnquireyUpdate);
			
	        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paypalEnquirey.getId()).with(changes).build();
	        
		} catch (ParseException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (JSONException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (IOException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (DataIntegrityViolationException dve){
			
			try {
				final JSONObject obj=new JSONObject(command.json()).getJSONObject("response");
				final String paymentid=obj.getString("id");
				DataIntegrityIssues(command, dve, paymentid);
				return CommandProcessingResult.empty();
			} catch (JSONException e) {
				return CommandProcessingResult.empty();
			}
			
		}
		
		
	}*/
	
	public CommandProcessingResult paypalEnquirey(final JsonCommand command) {

		try{
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForpaypalEnquirey(command.json());
					
			final JSONObject obj=new JSONObject(command.json()).getJSONObject("response");
			final SimpleDateFormat pattern= new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");	
			
			final String state=obj.getString("state");
			final String paymentid=obj.getString("id");
			final String create_time=obj.getString("create_time");
			final Date date= pattern.parse(create_time);
			
			final PaypalEnquirey  paypalEnquirey = new PaypalEnquirey(command.entityId(),state,paymentid,date);			
			this.paypalEnquireyRepository.save(paypalEnquirey);
			
			final JsonObject paymentobject = new JsonObject();
			final Map<String, Object> changes = new HashMap<String, Object>();
			 ClientBalance clientBalance = clientBalanceRepository.findByClientId(paypalEnquirey.getClientId());
			
			final PaypalEnquirey paypalEnquireyUpdate = this.paypalEnquireyRepository.findOne(paypalEnquirey.getId());
			
			try{
				
				 sendingDataToPaypal(paypalEnquirey.getId());
				 
			}catch (PayPalRESTException e) {
				
				final PaypalEnquirey paypalexceptionupdate=this.paypalEnquireyRepository.findOne(paypalEnquirey.getId());
				paypalexceptionupdate.setDescription(e.getLocalizedMessage());
				paypalexceptionupdate.setStatus("Fail");
				this.paypalEnquireyRepository.save(paypalexceptionupdate);
				changes.put("paymentId", new Long(-1));
				changes.put("paymentStatus", "Fail");
				changes.put("totalBalance", clientBalance == null?0:clientBalance.getBalanceAmount());
				changes.put("paypalException", e.getMessage());
				return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paypalEnquirey.getId()).with(changes).build();
			} 
			 
			final String remarks=paypalEnquireyUpdate.getPayerEmailId()!=null?paypalEnquireyUpdate.getPayerEmailId():" "+
			                          paypalEnquireyUpdate.getCardNumber()!=null?paypalEnquireyUpdate.getCardNumber():" ";
				
			    final String paymentdate = new SimpleDateFormat("dd MMMM yyyy").format(paypalEnquireyUpdate.getPaymentDate());
				
				paymentobject.addProperty("txn_id", paypalEnquireyUpdate.getPaymentId());
				paymentobject.addProperty("dateFormat", "dd MMMM yyyy");
				paymentobject.addProperty("locale", "en");
				paymentobject.addProperty("paymentDate", paymentdate);
				paymentobject.addProperty("amountPaid", paypalEnquireyUpdate.getTotalAmount());
				paymentobject.addProperty("isChequeSelected", "no");
				paymentobject.addProperty("receiptNo", paypalEnquireyUpdate.getPaymentId());
				paymentobject.addProperty("remarks", remarks);
				paymentobject.addProperty("paymentCode", 27);
				final String entityName = "PAYMENT";
				
				final JsonElement element1 = fromApiJsonHelper.parse(paymentobject.toString());
				JsonCommand comm = new JsonCommand(null,paymentobject.toString(), element1,fromApiJsonHelper, entityName, 
						paypalEnquirey.getClientId(),null, null, null, null, null, null, null, null,null,null);

				final CommandProcessingResult result = createPayment(comm);
				 clientBalance =clientBalanceRepository.findByClientId(paypalEnquirey.getClientId());
				if (result.resourceId() != null) {
					final int i = new Long(0).compareTo(result.resourceId());
					if (i == -1) {
						paypalEnquireyUpdate.setStatus("Success");
						paypalEnquireyUpdate.setObsPaymentId(result.resourceId());
						changes.put("paymentId", result.resourceId());
						changes.put("paymentStatus", "Success");
						changes.put("totalBalance",clientBalance.getBalanceAmount());
						changes.put("paypalException", "");
						changes.put("Errordescription", "");

					} else {
						paypalEnquireyUpdate.setStatus("Fail");
						paypalEnquireyUpdate.setObsPaymentId(result.resourceId());
						changes.put("paymentId", result.resourceId());
						changes.put("paymentStatus", "Fail");
						changes.put("totalBalance",clientBalance.getBalanceAmount());
						changes.put("paypalException", "");
						changes.put("Errordescription", "Payment Error");
					}
				} else {
					paypalEnquireyUpdate.setStatus("Failure");
					changes.put("paymentId", "");
					changes.put("paymentStatus", "Failure");
					changes.put("totalBalance",clientBalance.getBalanceAmount());
					changes.put("paypalException", "");
					changes.put("Errordescription", "Payment Not Processed");
				}
				
				this.paypalEnquireyRepository.save(paypalEnquireyUpdate);
			
	        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(paypalEnquirey.getId()).with(changes).build();
	        
		} catch (ParseException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (JSONException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (IOException e) {
			
			return new CommandProcessingResultBuilder().withResourceIdAsString(e.getMessage()).build();
			
		} catch (DataIntegrityViolationException dve){
			
			try {
				final JSONObject obj=new JSONObject(command.json()).getJSONObject("response");
				final String paymentid=obj.getString("id");
				DataIntegrityIssues(command, dve, paymentid);
				return CommandProcessingResult.empty();
			} catch (JSONException e) {
				return CommandProcessingResult.empty();
			}
			
		}
		
		
	}
	
	

	private void sendingDataToPaypal(final Long paypalEnquireyId) throws PayPalRESTException, IOException, JSONException {
		
		   try {
			   final Properties prop = new Properties();
			   final InputStream is = this.getClass().getClassLoader().getResourceAsStream("sdk_config.properties");
				prop.load(is);
				final PaypalEnquirey paypalEnquirey = this.paypalEnquireyRepository.findOne(paypalEnquireyId);
				com.paypal.api.payments.Payment.initConfig(prop);
				final PaymentGatewayConfiguration paypalGlobalData = this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYMENTGATEWAY_IS_PAYPAL_CHECK);
				final JSONObject object = new JSONObject(paypalGlobalData.getValue());
				final String paypalClientId = object.getString("clientId");
				final String paypalsecretCode = object.getString("secretCode");
			
				final OAuthTokenCredential tokenCredential = new OAuthTokenCredential(paypalClientId, paypalsecretCode);
				final com.paypal.api.payments.Payment payment = com.paypal.api.payments.Payment
						.get(tokenCredential.getAccessToken().trim(),paypalEnquirey.getPaymentId());

				final String paymentMethod = payment.getPayer().getPaymentMethod();
				
				if (paymentMethod.equalsIgnoreCase(Paypal_method)) {

					final String emailId = payment.getPayer().getPayerInfo().getEmail();
					final String payerId = payment.getPayer().getPayerInfo().getPayerId();
					final String amount = payment.getTransactions().get(0).getAmount().getTotal();
					final String currency = payment.getTransactions().get(0).getAmount().getCurrency();
					final String description = payment.getTransactions().get(0).getDescription();
					final String paymentState = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
					final BigDecimal totalAmount = new BigDecimal(amount);

					paypalEnquirey.fromPaypalEnquireyTransaction(emailId,payerId, totalAmount, currency, description, paymentState, paymentMethod);		
					 
				}else if(paymentMethod.equalsIgnoreCase(CreditCard_method)){	
					final JSONObject obj=new JSONObject(payment.getPayer().getFundingInstruments().get(0));
					
					if(obj.has(CreditCard)){
						final String cardNumber = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getNumber();
						final String cardType = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getType();
						final int cardExpiryMonth = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getExpireMonth();
						final int cardExpiryYear = payment.getPayer().getFundingInstruments().get(0).getCreditCard().getExpireYear();
						final String amount = payment.getTransactions().get(0).getAmount().getTotal();
						final String currency = payment.getTransactions().get(0).getAmount().getCurrency();
						final String description = payment.getTransactions().get(0).getDescription();
						final String paymentState = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
						final BigDecimal totalAmount = new BigDecimal(amount);
						final String cardExpiryDate=Integer.toString(cardExpiryMonth)+"/"+Integer.toString(cardExpiryYear);

						paypalEnquirey.fromPaypalEnquireyTransaction(cardNumber, cardType, cardExpiryDate, totalAmount, currency, description,paymentState, paymentMethod);	
					
					}else if(obj.has(CreditCardToken)){
						
						final String cardNumber = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getLast4();
						final String cardType = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getType();
						final int cardExpiryMonth = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getExpireMonth();
						final int cardExpiryYear = payment.getPayer().getFundingInstruments().get(0).getCreditCardToken().getExpireYear();
						final String amount = payment.getTransactions().get(0).getAmount().getTotal();
						final String currency = payment.getTransactions().get(0).getAmount().getCurrency();
						final String description = payment.getTransactions().get(0).getDescription();
						final String paymentState = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
						final BigDecimal totalAmount = new BigDecimal(amount);
						final String cardExpiryDate=Integer.toString(cardExpiryMonth)+"/"+Integer.toString(cardExpiryYear);
						paypalEnquirey.fromPaypalEnquireyTransaction(cardNumber, cardType, cardExpiryDate, totalAmount, currency, description,paymentState, paymentMethod);	
					}else{
						return;
					}
					
				}
				
				this.paypalEnquireyRepository.save(paypalEnquirey);
				
				
			} catch (PayPalRESTException e) {	
				
				throw new PayPalRESTException(e.getMessage());		
				
			} catch (IOException e) {
				
				throw new IOException(e.getMessage());		
				
			} catch (JSONException e) {
				
				throw new JSONException(e.getMessage());	
			}
		
		
	}

}



