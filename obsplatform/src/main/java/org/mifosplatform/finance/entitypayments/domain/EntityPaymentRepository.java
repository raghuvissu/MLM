package org.mifosplatform.finance.entitypayments.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EntityPaymentRepository extends JpaRepository<EntityPayment, Long>,
JpaSpecificationExecutor<EntityPayment> {

}
