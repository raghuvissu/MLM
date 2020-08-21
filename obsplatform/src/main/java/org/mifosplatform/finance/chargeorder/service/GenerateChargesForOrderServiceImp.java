package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.chargeorder.domain.ChargeTax;
import org.mifosplatform.finance.chargeorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerateChargesForOrderServiceImp implements GenerateChargesForOrderService {
	
	private final static Logger logger = LoggerFactory.getLogger(GenerateChargesForOrderServiceImp.class);

	private final GenerateCharges generateCharges;
	private final BillItemRepository billItemRepository;
	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	
	@Autowired
	public GenerateChargesForOrderServiceImp(final GenerateCharges generateCharges,final BillItemRepository billItemRepository,final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService) {
	
		this.generateCharges = generateCharges;
		this.billItemRepository = billItemRepository;
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		
	
	}

	@Override
	public List<ChargeData> generatebillingOrder(List<BillingOrderData> products) {

		ChargeData billingOrderCommand = null;
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();

		if (products.size() != 0) {

			for (BillingOrderData billingOrderData : products) {
				// discount master
				DiscountMasterData discountMasterData = null;
				List<DiscountMasterData> discountMasterDatas = chargingOrderReadPlatformService.retrieveDiscountOrders(billingOrderData.getClientOrderId(),
						billingOrderData.getOderPriceId());

				if (discountMasterDatas.size() != 0) {
					discountMasterData = discountMasterDatas.get(0);
				}

				if (billingOrderData.getOrderStatus() == 3) {
					billingOrderCommand = generateCharges.getCancelledOrderBill(billingOrderData, discountMasterData);
					billingOrderCommands.add(billingOrderCommand);
				}

				else if ("NRC".equals(billingOrderData.getChargeType())) {

					logger.info("---- NRC ---");
					billingOrderCommand = generateCharges.getOneTimeBill(billingOrderData, discountMasterData);
					billingOrderCommands.add(billingOrderCommand);

				} else if ("RC".equals(billingOrderData.getChargeType())) {
					
					logger.info("---- RC ---");
					if ("month(s)".equalsIgnoreCase(billingOrderData.getDurationType())) {
						 if ("N".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							billingOrderCommand = generateCharges.getMonthyBill(billingOrderData, discountMasterData);
							billingOrderCommands.add(billingOrderCommand);
							

						} else if ("Y".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							if (billingOrderData.getInvoiceTillDate() == null) {

								billingOrderCommand = generateCharges.getProrataMonthlyFirstBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getInvoiceTillDate() != null) {

								billingOrderCommand = generateCharges.getNextMonthBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							}
						}

						// weekly
					} else if ("week(s)".equalsIgnoreCase(billingOrderData.getDurationType())) {

						if ("N".equalsIgnoreCase(billingOrderData.getBillingAlign())) {
							
							billingOrderCommand = generateCharges.getWeeklyBill(billingOrderData, discountMasterData);
							billingOrderCommands.add(billingOrderCommand);

						} else if ("Y".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							if (billingOrderData.getInvoiceTillDate() == null) {

								billingOrderCommand = generateCharges.getProrataWeeklyFirstBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getInvoiceTillDate() != null) {

								billingOrderCommand = generateCharges.getNextWeeklyBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							}
						}

						// daily
					} else if ("Day(s)".equalsIgnoreCase(billingOrderData.getDurationType())) {

						billingOrderCommand = generateCharges.getDailyBill(billingOrderData, discountMasterData);
						billingOrderCommands.add(billingOrderCommand);

					}
				}/*else if(generateCharges.isChargeTypeUC(billingOrderData)){
					
					System.out.println("---- UC ---");
				}*/

			}
		} else {
			throw new BillingOrderNoRecordsFoundException();
		}
		
		return billingOrderCommands;
	}

	@Transactional
	@Override
	public BillItem generateCharge(List<ChargeData> billingOrderCommands) {

		BigDecimal invoiceAmount = BigDecimal.ZERO;
		BigDecimal totalChargeAmount = BigDecimal.ZERO;
		BigDecimal netTaxAmount = BigDecimal.ZERO;

		BillItem invoice = new BillItem(billingOrderCommands.get(0).getClientId(),DateUtils.getLocalDateOfTenant().toDate(), 
				                      invoiceAmount, invoiceAmount,netTaxAmount, "active");

		for (ChargeData billingOrderCommand : billingOrderCommands) {
			
			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			BigDecimal discountAmount = BigDecimal.ZERO;
			BigDecimal netChargeAmount = billingOrderCommand.getPrice();
			String discountCode="None";
			
			
			if (billingOrderCommand.getDiscountMasterData() != null) {
				discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				 discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode(); 
			    netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);

			}

			List<ChargeTaxCommand> chargeTaxCommands = billingOrderCommand.getListOfTax();

			Charge charge = new Charge(billingOrderCommand.getClientId(),billingOrderCommand.getClientOrderId(),
					billingOrderCommand.getOrderPriceId(),billingOrderCommand.getChargeCode(),billingOrderCommand.getChargeType(),
					discountCode,billingOrderCommand.getPrice(), discountAmount,netChargeAmount, billingOrderCommand.getStartDate(),
					billingOrderCommand.getEndDate());

			if (!chargeTaxCommands.isEmpty()) {

				for (ChargeTaxCommand chargeTaxCommand : chargeTaxCommands) {

					if (BigDecimal.ZERO.compareTo(chargeTaxCommand.getTaxAmount()) < 0) {
						
						netChargeTaxAmount = netChargeTaxAmount.add(chargeTaxCommand.getTaxAmount());
						ChargeTax invoiceTax = new ChargeTax(invoice, charge,chargeTaxCommand.getTaxCode(),
								chargeTaxCommand.getTaxValue(),chargeTaxCommand.getTaxPercentage(),chargeTaxCommand.getTaxAmount());
						charge.addChargeTaxes(invoiceTax);
					}
				}

				if (billingOrderCommand.getTaxInclusive() != null){
					if (isTaxInclusive(billingOrderCommand.getTaxInclusive())&&chargeTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
						netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
						charge.setNetChargeAmount(netChargeAmount);
						charge.setChargeAmount(netChargeAmount);
					}
				  }
			}
			
			netTaxAmount = netTaxAmount.add(netChargeTaxAmount);
			totalChargeAmount = totalChargeAmount.add(netChargeAmount);
			invoice.addCharges(charge);
		}

		invoiceAmount = totalChargeAmount.add(netTaxAmount);
		invoice.setNetChargeAmount(totalChargeAmount);
		invoice.setTaxAmount(netTaxAmount);
		invoice.setInvoiceAmount(invoiceAmount);
		return this.billItemRepository.saveAndFlush(invoice);
	}

	public BigDecimal getInvoiceAmount(List<ChargeData> billingOrderCommands) {
		
		BigDecimal invoiceAmount = BigDecimal.ZERO;
		for (ChargeData billingOrderCommand : billingOrderCommands) {
			invoiceAmount = invoiceAmount.add(billingOrderCommand.getPrice());
		}
		return invoiceAmount;
	}

	public Boolean isTaxInclusive(Integer taxInclusive) {

		Boolean isTaxInclusive = false;
		if (taxInclusive == 1)
			isTaxInclusive = true;

		return isTaxInclusive;
	}

	
	@Override
	public Map<String, List<Charge>> createNewChargesForServices(List<ChargeData> billingOrderCommands, Map<String, List<Charge>> groupOfCharges) {
		
		for (ChargeData billingOrderCommand : billingOrderCommands) {

			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			BigDecimal discountAmount = BigDecimal.ZERO;
			BigDecimal netChargeAmount = billingOrderCommand.getPrice();
			String discountCode = "None";

			if (billingOrderCommand.getDiscountMasterData() != null) {
				discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				discountCode =billingOrderCommand.getDiscountMasterData().getDiscountCode();
				netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);

			}

			List<ChargeTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();

			Charge charge = new Charge(billingOrderCommand.getClientId(),billingOrderCommand.getClientOrderId(),
					billingOrderCommand.getOrderPriceId(),billingOrderCommand.getChargeCode(),
					billingOrderCommand.getChargeType(), discountCode,billingOrderCommand.getPrice(), discountAmount,
					netChargeAmount, billingOrderCommand.getStartDate(),billingOrderCommand.getEndDate());

			for (ChargeTaxCommand invoiceTaxCommand : invoiceTaxCommands) {

				if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) < 0) {

					netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
					ChargeTax invoiceTax = new ChargeTax(null, charge, invoiceTaxCommand.getTaxCode(),invoiceTaxCommand.getTaxValue(), invoiceTaxCommand.getTaxPercentage(),
							invoiceTaxCommand.getTaxAmount());
					charge.addChargeTaxes(invoiceTax);
				}
			}

			if (billingOrderCommand.getTaxInclusive() != null) {
				if (isTaxInclusive(billingOrderCommand.getTaxInclusive()) && invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
					netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
					charge.setNetChargeAmount(netChargeAmount);
					charge.setChargeAmount(netChargeAmount);
				}
			}
			//Grouping same charge code orders
			if (groupOfCharges.containsKey(charge.getChargeCode()) && billingOrderCommand.getIsAggregate()) {
				 groupOfCharges.get(charge.getChargeCode()).add(charge);
			} else {
				LinkedList<Charge> ls = new LinkedList<Charge>();
				ls.add(charge);
				groupOfCharges.put(charge.getOrderId().toString(), ls);
			}
		}
		return groupOfCharges;
	}

	@Override
	public BillItem createBillItemRecords(Map<String, List<Charge>> mappedCharges, Long clientId) {

		BillItem billItem = null;
		
		for (Entry<String, List<Charge>> key : mappedCharges.entrySet()) {
			  BigDecimal netTaxAmount = BigDecimal.ZERO;
			  BigDecimal invoiceAmount = BigDecimal.ZERO;
			  BigDecimal totalChargeAmount = BigDecimal.ZERO;
			  billItem = new BillItem(clientId, DateUtils.getLocalDateOfTenant().toDate(), invoiceAmount, invoiceAmount, netTaxAmount, "active");
			for (Charge charge : key.getValue()) {
				for (ChargeTax chargeTax : charge.getChargeTaxs()) {
					netTaxAmount = netTaxAmount.add(chargeTax.getTaxAmount());
					chargeTax.setBillItem(billItem);
				}
				totalChargeAmount = totalChargeAmount.add(charge.getNetChargeAmount());
				billItem.addCharges(charge);
			}
			invoiceAmount = totalChargeAmount.add(netTaxAmount);
			billItem.setNetChargeAmount(totalChargeAmount);
			billItem.setTaxAmount(netTaxAmount);
			billItem.setInvoiceAmount(invoiceAmount);
			billItem = this.billItemRepository.saveAndFlush(billItem);
			this.chargingOrderWritePlatformService.updateClientBalance(billItem.getInvoiceAmount(), clientId, false);
		}

		return billItem;
	}
}
