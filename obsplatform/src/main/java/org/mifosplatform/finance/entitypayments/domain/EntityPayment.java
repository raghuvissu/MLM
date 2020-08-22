package org.mifosplatform.finance.entitypayments.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;
@SuppressWarnings("serial")
@Entity
@Table(name = "m_wallet_amount_entity")
public class EntityPayment extends AbstractPersistable<Long> {

	@Column(name = "client_id", nullable = false)
	private Long clientId;
	
	@Column(name = "office_id", nullable = false)
	private Long officeId;
	
	@Column(name = "client_name")
	private String clientName;

	@Column(name = "total_office_dr", scale = 6, precision = 19, nullable = false)
	private BigDecimal totalOfficeDR;
	
	@Column(name = "total_office_cr", scale = 6, precision = 19, nullable = false)
	private BigDecimal totalOfficeCR;
	
	@Column(name = "office_total_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal totalOfficeAmount;
	
	@Column(name = "office_payment_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal officePaymentAmount;	

	public EntityPayment() {
	}

	public EntityPayment(final Long clientId, final Long officeId,final String clientName, final BigDecimal totalOfficeDR,
			final BigDecimal totalOfficeCR, final BigDecimal totalOfficeAmount, final BigDecimal officePaymentAmount) {


		this.clientId = clientId;
		this.officeId = officeId;
		this.clientName = clientName;
		this.totalOfficeDR = totalOfficeDR;
		this.totalOfficeCR = totalOfficeCR;
		this.totalOfficeAmount = totalOfficeAmount;
		this.officePaymentAmount = officePaymentAmount;
	}
	
	public static EntityPayment fromJson(final JsonCommand command) {
		final BigDecimal totalOfficeDR = command.bigDecimalValueOfParameterNamed("totalOfficeDR");
		final String clientName = command.stringValueOfParameterNamed("clientName");
		final Long clientId = command.longValueOfParameterNamed("clientId");
		final Long officeId = command.longValueOfParameterNamed("officeId");
		final BigDecimal totalOfficeCR = command.bigDecimalValueOfParameterNamed("totalOfficeCR");
		final BigDecimal totalOfficeAmount = command.bigDecimalValueOfParameterNamed("totalOfficeAmount");
		final BigDecimal officePaymentAmount = command.bigDecimalValueOfParameterNamed("officePaymentAmount");
		return new EntityPayment(clientId, officeId, clientName, totalOfficeDR, totalOfficeCR, totalOfficeAmount,officePaymentAmount);


	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public BigDecimal getTotalOfficeDR() {
		return totalOfficeDR;
	}

	public void setTotalOfficeDR(BigDecimal totalOfficeDR) {
		this.totalOfficeDR = totalOfficeDR;
	}

	public BigDecimal getTotalOfficeCR() {
		return totalOfficeCR;
	}

	public void setTotalOfficeCR(BigDecimal totalOfficeCR) {
		this.totalOfficeCR = totalOfficeCR;
	}

	public BigDecimal getTotalOfficeAmount() {
		return totalOfficeAmount;
	}

	public void setTotalOfficeAmount(BigDecimal totalOfficeAmount) {
		this.totalOfficeAmount = totalOfficeAmount;
	}

	public BigDecimal getOfficePaymentAmount() {
		return officePaymentAmount;
	}

	public void setOfficePaymentAmount(BigDecimal officePaymentAmount) {
		this.officePaymentAmount = officePaymentAmount;
	}

	

}

