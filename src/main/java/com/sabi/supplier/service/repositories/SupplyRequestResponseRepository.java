package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplyRequestResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRequestResponseRepository extends JpaRepository<SupplyRequestResponseEntity, Long> {
    List<SupplyRequestResponseEntity> findByIsActive(Boolean isActive);

    Boolean existsBySupplyRequestId(Long supplyRequestId);

    SupplyRequestResponseEntity findBySupplyRequestId(Long supplyRequestId);

    @Query("SELECT s FROM SupplyRequestResponseEntity s WHERE ((:supplyRequestId IS NULL) OR (:supplyRequestId IS NOT NULL AND s.supplyRequestId = :supplyRequestId))")
    Page<SupplyRequestResponseEntity> findSupplierRequestResponse(@Param("supplyRequestId") Long supplyRequestId, Pageable pageable);
}
