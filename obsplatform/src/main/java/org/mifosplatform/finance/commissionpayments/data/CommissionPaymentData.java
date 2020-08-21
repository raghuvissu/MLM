package org.mifosplatform.finance.commissionpayments.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

public class CommissionPaymentData {
	
    private Collection<McodeData> data;
	private LocalDate paymentDate;
	private String clientName;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private String payMode;
	private Boolean isDeleted;
	private String receiptNo;
	private Long id;
	private Integer rate;
	private List<CommissionPaymentData> depositDatas;
	
	public CommissionPaymentData(final Collection<McodeData> data,final List<CommissionPaymentData> depositDatas){
		this.data= data;
		this.depositDatas = depositDatas;
	}
	
	
	public CommissionPaymentData(final String clientName, final String payMode,final LocalDate paymentDate, final BigDecimal debitAmount, final BigDecimal creditAmount,
			final Boolean isDeleted, final String receiptNumber, final Integer rate) {
		  this.clientName = clientName;
		  this.payMode = payMode;
		  this.paymentDate = paymentDate;
		  this.debitAmount = debitAmount;
		  this.isDeleted = isDeleted;
		  this.creditAmount = creditAmount;
		  this.receiptNo = receiptNumber;
		  this.rate = rate;
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


	public Integer getRate() {
		return rate;
	}


	public void setRate(Integer rate) {
		this.rate = rate;
	}


	public List<CommissionPaymentData> getDepositDatas() {
		return depositDatas;
	}


	public void setDepositDatas(List<CommissionPaymentData> depositDatas) {
		this.depositDatas = depositDatas;
	}


	public void setData(Collection<McodeData> data) {
		this.data = data;
	}


	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}


	public void setClientName(String clientName) {
		this.clientName = clientName;
	}


	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}


	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}


	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public CommissionPaymentData() {
		
	}


	public Collection<McodeData> getData() {
		return data;
	}


	public LocalDate getPaymentDate() {
		return paymentDate;
	}


	public String getClientName() {
		return clientName;
	}


	public String getPayMode() {
		return payMode;
	}


	public Boolean getIsDeleted() {
		return isDeleted;
	}



	public String getReceiptNo() {
		return receiptNo;
	}


	public Long getId() {
		return id;
	}


	
	
}
