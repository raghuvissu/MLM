package org.mifosplatform.finance.commissionpayments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
@SuppressWarnings("serial")
@Entity
@Table(name = "m_commission_payment",uniqueConstraints = {@UniqueConstraint(name = "receipt_no", columnNames = { "receipt_no" })})
public class CommissionPayment extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "client_id", nullable = false)
	private Long clientId;
	
	@Column(name = "client_name")
	private String clientName;

	@Column(name = "debit_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal debitAmount;
	
	@Column(name = "credit_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal creditAmount;

	@Column(name = "is_deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "payment_date")
	private Date paymentDate;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "paymode_id")
	private int paymodeId;
	
	@Column(name = "rate")
	private int rate;
	
	@Column(name = "receipt_no")
	private String receiptNo;
	
	@Column(name = "office_id")
	private Long officeId;
	

	public CommissionPayment() {
	}

	public CommissionPayment(final Long clientId, final Long paymentId, final BigDecimal debitAmount,final BigDecimal creditAmount,
			final LocalDate paymentDate,final String remark, final Long paymodeCode, final String receiptNo, Integer rate, String clientName, Long officeId) {


		this.clientId = clientId;
		this.paymentDate = paymentDate.toDate();
		this.remarks = remark;
		this.paymodeId = paymodeCode.intValue();
		this.receiptNo=receiptNo.isEmpty()?null:receiptNo;
		this.debitAmount = debitAmount;
		this.creditAmount = creditAmount;
		this.rate = rate;
		this.clientName = clientName;
		this.officeId = officeId;

	}
	
	
	public static CommissionPayment fromJson(final JsonCommand command, final Long clientid) {
		final LocalDate paymentDate = command
				.localDateValueOfParameterNamed("paymentDate");
		final Long paymentCode = command.longValueOfParameterNamed("paymentCode");
				
		final BigDecimal debitAmount = command.bigDecimalValueOfParameterNamed("debitAmount");
		final BigDecimal creditAmount = command.bigDecimalValueOfParameterNamed("creditAmount");
		final Integer rate = command.integerValueOfParameterNamed("rate");
		final String clientName = command.stringValueOfParameterNamed("clientName");
		final String remarks = command.stringValueOfParameterNamed("remarks");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		final Long officeId=command.longValueOfParameterNamed("officeId");
		return new CommissionPayment(clientid, null, debitAmount, creditAmount, paymentDate,remarks, paymentCode, receiptNo, rate, clientName, officeId);


	}

	public Long getClientId() {
		return clientId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setPaymodeId(int paymodeId) {
		this.paymodeId = paymodeId;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public int getPaymodeCode() {
		return paymodeId;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public int getPaymodeId() {
		return paymodeId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
	


}

