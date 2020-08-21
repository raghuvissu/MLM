package org.mifosplatform.portfolio.jvtransaction.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.crm.ticketmaster.domain.TicketMaster;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_ticket_master")
public class JVTransaction extends AbstractAuditableCustom<AppUser, Long>{
		

		@Column(name = "office_id", length = 65536)
		private Long officeId;
		
		@Column(name = "client_id", length = 65536)
		private Long clientId;

		@Column(name = "jv_date")
		private LocalDate jvDate;

		@Column(name = "start_date")
		private LocalDate startDate;
		
		@Column(name = "end_date")
		private LocalDate endDate;

		@Column(name = "jv_description")
		private String jvDescription;
		
		@Column(name = "ref_id")
		private Long refId;
		
		@Column(name = "transAmount")
		private Float transAmount;

		@Column(name = "trans_Type")
		private String transType;

	
		public JVTransaction(){
			
		}
		
		public JVTransaction(LocalDate jvDate,LocalDate startDate,LocalDate endDate,Float transAmount){
			this.jvDate=jvDate;
			this.startDate=startDate;
			this.endDate=endDate;
			this.transAmount=transAmount;
			
		}
		
		public static JVTransaction fromJson(final JsonCommand command) throws ParseException {
			return null;
		}


}
