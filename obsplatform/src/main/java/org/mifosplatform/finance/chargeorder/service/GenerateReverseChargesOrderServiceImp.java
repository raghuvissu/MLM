package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.chargeorder.domain.ChargeTax;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author hugo
 *s
 */
@Service
public class GenerateReverseChargesOrderServiceImp implements GenerateReverseChargesOrderService {

	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateDisconnectionCharges generateDisconnectionCharges;
	private final BillItemRepository billItemRepository;

	@Autowired
	public GenerateReverseChargesOrderServiceImp(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final GenerateDisconnectionCharges generateDisconnectionCharges,final BillItemRepository billItemRepository) {

		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateDisconnectionCharges = generateDisconnectionCharges;
		this.billItemRepository = billItemRepository;
	}

	@Override
	public List<ChargeData> generateReverseBillingOrder(final List<BillingOrderData> billingOrderProducts,final LocalDate disconnectDate) {

		ChargeData billingOrderCommand = null;
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();
		
		if (billingOrderProducts.size() != 0) {

			for (BillingOrderData billingOrderData : billingOrderProducts) {

				DiscountMasterData discountMasterData = null;

		      List<DiscountMasterData> discountMasterDatas = chargingOrderReadPlatformService.retrieveDiscountOrders(billingOrderData.getClientOrderId(),
		    		                                               billingOrderData.getOderPriceId());
				if (discountMasterDatas.size() != 0) {
					discountMasterData = discountMasterDatas.get(0);
				}

				if (generateDisconnectionCharges.isChargeTypeRC(billingOrderData)) {

					// monthly
					if (billingOrderData.getDurationType().equalsIgnoreCase("month(s)")) {

						billingOrderCommand = generateDisconnectionCharges.getReverseMonthyBill(billingOrderData,discountMasterData, disconnectDate);
						billingOrderCommands.add(billingOrderCommand);
					}
					// weekly	
					else if (billingOrderData.getDurationType().equalsIgnoreCase("week(s)")) {

						billingOrderCommand = generateDisconnectionCharges.getReverseWeeklyBill(billingOrderData,discountMasterData,disconnectDate);
						billingOrderCommands.add(billingOrderCommand);
				 }
				}
			}

		}

		return billingOrderCommands;
	}

	@Override
	public BillItem generateNegativeCharge(final List<ChargeData> billingOrderCommands) {
		
		BigDecimal invoiceAmount = BigDecimal.ZERO;
		BigDecimal totalChargeAmount = BigDecimal.ZERO;
		BigDecimal netTaxAmount = BigDecimal.ZERO;
		
		BillItem invoice = new BillItem(billingOrderCommands.get(0).getClientId(), DateUtils.getLocalDateOfTenant().toDate(), 
				               invoiceAmount, invoiceAmount,netTaxAmount, "active");
		
		for (ChargeData billingOrderCommand : billingOrderCommands) {
			
			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			String discountCode="None";
			BigDecimal discountAmount = BigDecimal.ZERO;
		    BigDecimal netChargeAmount = billingOrderCommand.getPrice();
		    
			if(billingOrderCommand.getDiscountMasterData()!= null){
				 discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				 discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode();
				 if(billingOrderCommand.getChargeType().equalsIgnoreCase("NRC")){
				  netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);
				 }
			}
			
			List<ChargeTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();

			Charge charge = new Charge(billingOrderCommand.getClientId(), billingOrderCommand.getClientOrderId(), billingOrderCommand.getOrderPriceId(),

					billingOrderCommand.getChargeCode(),billingOrderCommand.getChargeType(),discountCode, billingOrderCommand.getPrice().negate(), discountAmount.negate(),
					netChargeAmount.negate(), billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate());

			if(!invoiceTaxCommands.isEmpty()){
			
			     for(ChargeTaxCommand invoiceTaxCommand : invoiceTaxCommands){
				
			    	if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) != 0) {
			    		
				     netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
				     ChargeTax invoiceTax = new ChargeTax(invoice, charge, invoiceTaxCommand.getTaxCode(),invoiceTaxCommand.getTaxValue(), 
						                  invoiceTaxCommand.getTaxPercentage(), invoiceTaxCommand.getTaxAmount().negate());
				      charge.addChargeTaxes(invoiceTax);
			    	}
			     }


			     if (billingOrderCommand.getTaxInclusive() != null) {
						
						if (isTaxInclusive(billingOrderCommand.getTaxInclusive())&&invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
							netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
							charge.setNetChargeAmount(netChargeAmount.negate());
							charge.setChargeAmount(netChargeAmount.negate());
						}
					}

			}
			netTaxAmount = netTaxAmount.add(netChargeTaxAmount);
			totalChargeAmount = totalChargeAmount.add(netChargeAmount);
			invoice.addCharges(charge);		
			
		 }

		invoiceAmount = totalChargeAmount.add(netTaxAmount);
		invoice.setNetChargeAmount(totalChargeAmount.negate());
		invoice.setTaxAmount(netTaxAmount.negate());
		invoice.setInvoiceAmount(invoiceAmount.negate());
		return this.billItemRepository.save(invoice);
	}
	
	public Boolean isTaxInclusive(Integer taxInclusive){
		
		Boolean isTaxInclusive = false;
		if(taxInclusive == 1){ isTaxInclusive = true;}
		return isTaxInclusive;
	}
}
