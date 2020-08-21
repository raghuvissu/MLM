package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReverseCharges {
	
	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateReverseChargesOrderService generateReverseChargesOrderService;
	private final GenerateChargesForOrderService generateChargesForOrderService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final BillItemRepository  billItemRepository;
	
	
	@Autowired
	public ReverseCharges(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,final GenerateChargesForOrderService generateChargesForOrderService,
			final GenerateReverseChargesOrderService generateReverseChargesOrderService,final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final BillItemRepository billItemRepository){
		
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateReverseChargesOrderService = generateReverseChargesOrderService;
		this.chargingOrderWritePlatformService=chargingOrderWritePlatformService;
		this.generateChargesForOrderService=generateChargesForOrderService;
		this.billItemRepository = billItemRepository;
	}
	
	 
	public BigDecimal reverseInvoiceServices(final Long orderId,final Long clientId,final LocalDate disconnectionDate){
		
		BillItem invoice=null;
	    BigDecimal invoiceAmount=BigDecimal.ZERO;
	   
		List<BillingOrderData> billingOrderProducts = this.chargingOrderReadPlatformService.getReverseBillingOrderData(clientId, disconnectionDate, orderId);
		
		List<ChargeData> billingOrderCommands = this.generateReverseChargesOrderService.generateReverseBillingOrder(billingOrderProducts,disconnectionDate);
		
		if(billingOrderCommands.size() !=0){
			
		if(billingOrderCommands.get(0).getChargeType().equalsIgnoreCase("RC")){
			 invoice = this.generateChargesForOrderService. generateCharge(billingOrderCommands);
			 invoiceAmount=invoice.getInvoiceAmount();
		}else{
			
		invoice = this.generateReverseChargesOrderService.generateNegativeCharge(billingOrderCommands);
        invoiceAmount=invoice.getInvoiceAmount();
       
	        List<Long> invoices = this.chargingOrderReadPlatformService.listOfInvoices(clientId, orderId);
	        if(!invoices.isEmpty() && invoiceAmount != null && invoiceAmount.intValue() != 0){
	        
	        	for(Long invoiceIds :invoices){
		        	Long invoiceId = invoiceIds;
		        	BillItem invoiceData = this.billItemRepository.findOne(invoiceId);
		        	BigDecimal dueAmount = invoiceData.getDueAmount();
		        	if(dueAmount != null && dueAmount.intValue() > 0 && invoiceAmount.intValue() < dueAmount.intValue()){
		        		BigDecimal updateAmount = dueAmount.add(invoiceAmount);
		        		invoiceData.setDueAmount(updateAmount);
		        		this.billItemRepository.saveAndFlush(invoiceData);
		        	}else if(dueAmount != null && dueAmount.intValue() > 0 && invoiceAmount.intValue() > dueAmount.intValue()){
		        		invoiceData.setDueAmount(BigDecimal.ZERO);
		        		this.billItemRepository.saveAndFlush(invoiceData);
		        	}
		        }
	        }
	        
		}
		
		this.chargingOrderWritePlatformService.updateClientBalance(invoice.getInvoiceAmount(),clientId,false);
		
		this.chargingOrderWritePlatformService.updateBillingOrder(billingOrderCommands);
		 
		return invoiceAmount;
	}else{
		return invoiceAmount;
	}
	
	}
}
	
