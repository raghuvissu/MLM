package org.mifosplatform.finance.chargeorder.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ProcessDate;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.chargeorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.finance.chargeorder.serialization.ChargingOrderCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ChargingCustomerOrders {
	
	private final static Logger logger = LoggerFactory.getLogger(ChargingCustomerOrders.class);

	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateChargesForOrderService generateChargesForOrderService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final ChargingOrderCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ConfigurationRepository globalConfigurationRepository;
	

	@Autowired
	public ChargingCustomerOrders(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,final GenerateChargesForOrderService generateChargesForOrderService,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,final ChargingOrderCommandFromApiJsonDeserializer apiJsonDeserializer,
		    final ConfigurationRepository globalConfigurationRepository,final BillItemRepository billItemRepository) {

		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateChargesForOrderService = generateChargesForOrderService;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.globalConfigurationRepository = globalConfigurationRepository;
	}
	
	public CommandProcessingResult createNewCharges(JsonCommand command) {
		
		try {
			// validation not written
			this.apiJsonDeserializer.validateForCreate(command.json());
			LocalDate processDate = ProcessDate.fromJson(command);
			BillItem invoice = this.invoicingSingleClient(command.entityId(),processDate);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(invoice.getId()).build();
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}
	
	public BillItem invoicingSingleClient(Long clientId, LocalDate processDate) {

	
		LocalDate initialProcessDate = processDate;
		Date nextBillableDate = null;
		// Get list of qualified orders of customer
		List<BillingOrderData> billingOrderDatas = chargingOrderReadPlatformService.retrieveOrderIds(clientId, processDate);
		
		if (billingOrderDatas.size() != 0) {
			
			boolean prorataWithNextBillFlag = this.checkInvoiceConfigurations(ConfigurationConstants.CONFIG_PRORATA_WITH_NEXT_BILLING_CYCLE);
			Map<String, List<Charge>> groupOfCharges = new HashMap<String, List<Charge>>();
			
			for (BillingOrderData billingOrderData : billingOrderDatas) {
				
				nextBillableDate = billingOrderData.getNextBillableDate();
				if (prorataWithNextBillFlag && ("Y".equalsIgnoreCase(billingOrderData.getBillingAlign())) && billingOrderData.getInvoiceTillDate() == null ) {
					LocalDate alignEndDate = new LocalDate(nextBillableDate).dayOfMonth().withMaximumValue();
					if (!processDate.toDate().after(alignEndDate.toDate())) 
						processDate = alignEndDate.plusDays(2);
				} else {
					processDate = initialProcessDate;
				}
				while (processDate.toDate().after(nextBillableDate) || processDate.toDate().compareTo(nextBillableDate) == 0) {

					groupOfCharges = chargeLinesForServices(billingOrderData, clientId, processDate, groupOfCharges);
					if (!groupOfCharges.isEmpty() && groupOfCharges.containsKey(billingOrderData.getOrderId().toString())) {
						List<Charge> charges = groupOfCharges.get(billingOrderData.getOrderId().toString());
						nextBillableDate = new LocalDate(charges.get(charges.size() - 1).getEntDate()).plusDays(1).toDate();
					} else if (!groupOfCharges.isEmpty() && groupOfCharges.containsKey(billingOrderData.getChargeCode())) {
						List<Charge> charges = groupOfCharges.get(billingOrderData.getChargeCode());
						nextBillableDate = new LocalDate(charges.get(charges.size() - 1).getEntDate()).plusDays(1).toDate();
					}
				}
			}
			return this.generateChargesForOrderService.createBillItemRecords(groupOfCharges, clientId);
			
		} else {
			throw new BillingOrderNoRecordsFoundException();
		}
	}
	
	public Map<String, List<Charge>> chargeLinesForServices(BillingOrderData billingOrderData, Long clientId,LocalDate processDate, Map<String, List<Charge>> groupOfCharges) {

		// Get qualified order complete details
		List<BillingOrderData> chargeServices = this.chargingOrderReadPlatformService.retrieveBillingOrderData(clientId, processDate,billingOrderData.getOrderId());

		List<ChargeData> chargeDatas = this.generateChargesForOrderService.generatebillingOrder(chargeServices);
		
		this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
		
		return this.generateChargesForOrderService.createNewChargesForServices(chargeDatas, groupOfCharges);
	}

	public BillItem singleOrderInvoice(Long orderId, Long clientId,LocalDate processDate) {

		// Get qualified order complete details
		List<BillingOrderData> chargeServices = this.chargingOrderReadPlatformService.retrieveBillingOrderData(clientId, processDate,orderId);

		List<ChargeData> chargeDatas = this.generateChargesForOrderService.generatebillingOrder(chargeServices);
			
		// BillItem
		BillItem  billItem = this.generateChargesForOrderService.generateCharge(chargeDatas);

		// Update order-price
		this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
		logger.info("Top-Up:---------------------"+ chargeDatas.get(0).getNextBillableDate());

		// Update Client Balance
		this.chargingOrderWritePlatformService.updateClientBalance(billItem.getInvoiceAmount(), clientId, false);
		
		return billItem;
	
	}
	
	public boolean checkInvoiceConfigurations(final String configName) {

		Configuration configuration = this.globalConfigurationRepository.findOneByName(configName);
		if (configuration != null && configuration.isEnabled()) {
			return true;
		} else {
			return false;
		}

	}
	
	/*	public GenerateChargeData chargesForServices(BillingOrderData billingOrderData, Long clientId,LocalDatße processDate,BillItem invoice,boolean singleInvoiceFlag) {

	// Get qualified order complete details
	List<BillingOrderData> products = this.chargingOrderReadPlatformService.retrieveBillingOrderData(clientId, processDate,billingOrderData.getOrderId());

	List<ChargeData> chargeDatas = this.generateChargesForOrderService.generatebillingOrder(products);

	if(singleInvoiceFlag){

		invoice = this.generateChargesForOrderService.generateMultiOrderCharges(chargeDatas,invoice);

		// Update order-price
		this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
		System.out.println("---------------------"+ chargeDatas.get(0).getNextBillableDate());

		return new GenerateChargeData(clientId, chargeDatas.get(0).getNextBillableDate(), invoice.getInvoiceAmount(),invoice);

	}else{

		// BillItem
		BillItem singleInvoice = this.generateChargesForOrderService.generateCharge(chargeDatas);

		// Update order-price
		this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
		System.out.println("---------------------"+ chargeDatas.get(0).getNextBillableDate());
		
		// Update Client Balance
		this.chargingOrderWritePlatformService.updateClientBalance(singleInvoice.getInvoiceAmount(), clientId, false);

		return new GenerateChargeData(clientId, chargeDatas.get(0).getNextBillableDate(),singleInvoice.getInvoiceAmount(), singleInvoice);
	}
	return new GenerateChargeData(clientId, chargeDatas.get(0).getNextBillableDate(), invoice.getInvoiceAmount(),invoice);
}*/
	
	
/*	if (singleInvoiceFlag) {

		this.billItemRepository.save(invoiceData.getInvoice());

		// Update Client Balance
		this.chargingOrderWritePlatformService.updateClientBalance(invoiceData.getInvoice().getInvoiceAmount(), clientId,false);
	}*/


}
