package com.sabisupplier.service.repositories;

import com.sabisupplierscore.models.SupplyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long>, JpaSpecificationExecutor<SupplyRequest> {
    List<SupplyRequest> findByIsActive(Boolean isActive);

    Page<SupplyRequest> findSupplyRequest(Long productId, String productName, Long askingQuantity, BigDecimal askingPrice, Date startTime, Date endTime, String referenceNo, String status, PageRequest pageRequest);

    Boolean existsByReferenceNo(String referenceNo);
}
