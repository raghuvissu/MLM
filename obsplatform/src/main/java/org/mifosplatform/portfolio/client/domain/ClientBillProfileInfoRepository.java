package org.mifosplatform.portfolio.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientBillProfileInfoRepository extends JpaRepository<ClientBillProfileInfo, Long>, JpaSpecificationExecutor<ClientBillProfileInfo>{

}
