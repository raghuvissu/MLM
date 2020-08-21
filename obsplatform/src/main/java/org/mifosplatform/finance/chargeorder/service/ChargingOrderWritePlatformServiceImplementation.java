package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfoRepository;
import org.mifosplatform.organisation.office.domain.OfficeCommision;
import org.mifosplatform.organisation.office.domain.OfficeCommisionRepository;
import org.mifosplatform.organisation.partner.domain.OfficeControlBalance;
import org.mifosplatform.organisation.partner.domain.PartnerBalanceRepository;
import org.mifosplatform.organisation.partneragreement.data.AgreementData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargingOrderWritePlatformServiceImplementation implements ChargingOrderWritePlatformService {


	private final OrderRepository orderRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final ClientRepository clientRepository;
	private final PartnerBalanceRepository partnerBalanceRepository;
	private final OfficeAdditionalInfoRepository infoRepository;
	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final OfficeCommisionRepository officeCommisionRepository;
	
	@Autowired
	public ChargingOrderWritePlatformServiceImplementation(final OrderRepository orderRepository,
			final ClientBalanceRepository clientBalanceRepository,
			final ClientRepository clientRepository,
			final PartnerBalanceRepository partnerBalanceRepository,
			final OfficeAdditionalInfoRepository infoRepository,
			final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final OfficeCommisionRepository officeCommisionRepository){

		this.orderRepository = orderRepository;
		this.clientBalanceRepository = clientBalanceRepository;
		this.clientRepository = clientRepository;
		this.partnerBalanceRepository = partnerBalanceRepository;
		this.infoRepository = infoRepository;
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.officeCommisionRepository = officeCommisionRepository;
	}
	
	
	@Override
	public CommandProcessingResult updateBillingOrder(List<ChargeData> commands) {
		Order clientOrder = null;
		
		for (ChargeData billingOrderCommand : commands) {
			
			clientOrder = this.orderRepository.findOne(billingOrderCommand.getClientOrderId());
				if (clientOrder != null ) {
					
						clientOrder.setNextBillableDay(billingOrderCommand.getNextBillableDate());
						List<OrderPrice> orderPrices = clientOrder.getPrice();
						
						for (OrderPrice orderPriceData : orderPrices) {
							
						    if(billingOrderCommand.getOrderPriceId().equals(orderPriceData.getId())){
						    	
							orderPriceData.setInvoiceTillDate(billingOrderCommand.getInvoiceTillDate());
							orderPriceData.setNextBillableDay(billingOrderCommand.getNextBillableDate());
						
						}
					}
				}
				this.orderRepository.saveAndFlush(clientOrder);
		}
	
		return new CommandProcessingResult(Long.valueOf(clientOrder.getId()));
	}


	@Override
	public void updateClientVoucherBalance(BigDecimal amount,Long clientId,boolean isWalletEnable) {

		
		BigDecimal balance=null;
		ClientBalance clientBalance = this.clientBalanceRepository.findByClientId(clientId);
		
		if(clientBalance == null){
			clientBalance =new ClientBalance(clientId,amount,isWalletEnable?'Y':'N');
		}else{
			if(isWalletEnable){
				balance=clientBalance.getWalletAmount().add(amount);
				clientBalance.setWalletAmount(balance);
				
			}else{
				balance=clientBalance.getBalanceAmount().add(amount);
				clientBalance.setBalanceAmount(balance);
			}

		}

		this.clientBalanceRepository.saveAndFlush(clientBalance);
		
	}
	
	
	@Override
	public void updateClientBalance(BigDecimal amount, Long clientId,boolean isWalletEnable) {
		
		BigDecimal balance=null;
		
		ClientBalance clientBalance = this.clientBalanceRepository.findByClientId(clientId);
		
		if(clientBalance == null){
			clientBalance =new ClientBalance(clientId, amount, isWalletEnable?'Y':'N');
		}else{
			if(isWalletEnable){
				balance=clientBalance.getWalletAmount().add(amount);
				clientBalance.setWalletAmount(balance);
				
			}else{
				balance=clientBalance.getBalanceAmount().add(amount);
				clientBalance.setBalanceAmount(balance);
			}
			
		}

		this.clientBalanceRepository.saveAndFlush(clientBalance);
		
		final Client client = this.clientRepository.findOne(clientId);
		final OfficeAdditionalInfo officeAdditionalInfo = this.infoRepository.findoneByoffice(client.getOffice());
		if (officeAdditionalInfo != null) {
			if (officeAdditionalInfo.getIsCollective()) {
				this.updatePartnerBalance(client.getOffice(), amount);

			}
		}

	}

	private void updatePartnerBalance(final Office office,final BigDecimal amount) {

		final String accountType = "INVOICE";
		OfficeControlBalance partnerControlBalance = this.partnerBalanceRepository.findOneWithPartnerAccount(office.getId(), accountType);
		if (partnerControlBalance != null) {
			partnerControlBalance.update(amount, office.getId());

		} else {
			partnerControlBalance = OfficeControlBalance.create(amount, accountType,office.getId());

		}

		this.partnerBalanceRepository.save(partnerControlBalance);
	}

	@Override
	public void UpdateOfficeCommision(BillItem invoice, Long agreementId) {

		List<Charge> charges = invoice.getCharges();

		for (Charge charge : charges) {
      
			AgreementData data = this.chargingOrderReadPlatformService.retrieveOfficeChargesCommission(charge.getId());
			if (data != null) {
				OfficeCommision commisionData = OfficeCommision.fromJson(data);
				this.officeCommisionRepository.save(commisionData);
			}else{}
           
		}
	}


}
