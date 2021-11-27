package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long>, JpaSpecificationExecutor<SupplyRequest> {
    List<SupplyRequest> findByIsActive(Boolean isActive);

    Boolean existsByReferenceNo(String referenceNo);
}
