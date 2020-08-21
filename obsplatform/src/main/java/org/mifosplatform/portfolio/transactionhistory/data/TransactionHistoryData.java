package org.mifosplatform.portfolio.transactionhistory.data;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class TransactionHistoryData {
	
		private Long id;
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}


		private Long clientId;
		private String transactionType;
		private DateTime transactionDate;
		private String history;
		private String user;
		private String resourceId;
		/*private LocalDate fromDate;
		private LocalDate toDate;*/

		public TransactionHistoryData() {
			
		}
		
		public TransactionHistoryData(final Long clientId, final String transactionType, final DateTime transactionDate, final String history){
			this.clientId = clientId;
			this.transactionType = transactionType;
			this.transactionDate = transactionDate;
			this.history = history;
		}
		
		public TransactionHistoryData(final Long id, final Long clientId, final String transactionType, final DateTime transactionDate, final String resourceId, final String history, String user/*,final LocalDate fromDate,final LocalDate toDate*/){
			this.id = id;
			this.clientId = clientId;
			this.transactionType = transactionType;
			this.transactionDate = transactionDate;
			this.resourceId=resourceId;
			this.history = history;
			this.user=user;
			/*this.fromDate = fromDate;
			this.toDate = toDate;*/
			
		}
		
		
		/**
		 * @return the clientId
		 */
		public Long getClientId() {
			return clientId;
		}


		/**
		 * @param clientId the clientId to set
		 */
		public void setClientId(Long clientId) {
			this.clientId = clientId;
		}


		/**
		 * @return the transactionType
		 */
		public String getTransactionType() {
			return transactionType;
		}


		/**
		 * @param transactionType the transactionType to set
		 */
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}


		/**
		 * @return the transactionDate
		 */
		public DateTime getTransactionDate() {
			return transactionDate;
		}


		/**
		 * @param transactionDate the transactionDate to set
		 */
		public void setTransactionDate(DateTime transactionDate) {
			this.transactionDate = transactionDate;
		}


		/**
		 * @return the history
		 */
		public String getHistory() {
			return history;
		}


		/**
		 * @param history the history to set
		 */
		public void setHistory(String history) {
			this.history = history;
		}

		/**
		 * @return the user
		 */
		public String getUser() {
			return user;
		}
/*
		public LocalDate getFromDate() {
			return fromDate;
		}

		public void setFromDate(LocalDate fromDate) {
			this.fromDate = fromDate;
		}

		public LocalDate getToDate() {
			return toDate;
		}

		public void setToDate(LocalDate toDate) {
			this.toDate = toDate;
		}
		*/

}
