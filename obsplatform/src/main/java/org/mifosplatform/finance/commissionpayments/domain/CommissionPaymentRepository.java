package org.mifosplatform.finance.commissionpayments.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommissionPaymentRepository extends JpaRepository<CommissionPayment, Long>,
JpaSpecificationExecutor<CommissionPayment> {

}
