package org.mifosplatform.finance.entitypayments.data;

import java.math.BigDecimal;

public class EntityPaymentData {
	
    private Long id;
    private Long clientId;
    private Long officeId;
	private String clientName;
	private BigDecimal totalOfficeDR;
	private BigDecimal totalOfficeCR;
	private BigDecimal totalOfficeAmount;
	private BigDecimal officePaymentAmount;
	
	public EntityPaymentData(final Long id, final Long clientId, final Long officeId,final String clientName, final BigDecimal totalOfficeDR,
			final BigDecimal totalOfficeCR, final BigDecimal totalOfficeAmount, final BigDecimal officePaymentAmount) {

		this.id = id;
		this.clientId = clientId;
		this.officeId = officeId;
		this.clientName = clientName;
		this.totalOfficeDR = totalOfficeDR;
		this.totalOfficeCR = totalOfficeCR;
		this.totalOfficeAmount = totalOfficeAmount;
		this.officePaymentAmount = officePaymentAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
