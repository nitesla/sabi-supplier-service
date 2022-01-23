package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long>, JpaSpecificationExecutor<SupplyRequest> {
    List<SupplyRequest> findByIsActiveOrderByIdDesc(Boolean isActive);

    Boolean existsByReferenceNo(String referenceNo);

    SupplyRequest findSupplyRequestById(Long id);

    @Query("SELECT s FROM SupplyRequest s left join WareHouse pt on s.warehouseId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            " AND ((:productName IS NULL) OR (:productName IS NOT NULL AND s.productName like %:productName%))" +
            "AND ((:askingQuantity IS NULL) OR (:askingQuantity IS NOT NULL AND s.askingQuantity = :askingQuantity))" +
            "AND ((:askingPrice IS NULL) OR (:askingPrice IS NOT NULL AND s.askingPrice = :askingPrice))" +
            "AND ((:startTime IS NULL) OR (:startTime IS NOT NULL AND s.startTime = :startTime))" +
            "AND ((:endTime IS NULL) OR (:endTime IS NOT NULL AND s.endTime = :endTime))" +
            "AND ((:referenceNo IS NULL) OR (:referenceNo IS NOT NULL AND s.referenceNo like %:referenceNo%))" +
            "AND ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status))" +
            "AND ((:warehouseId IS NULL and :unassigned is null )OR (s.warehouseId IS NULL and :unassigned = true ) OR (:warehouseId IS NOT NULL AND s.warehouseId = :warehouseId))" +
            "AND ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            "AND ((:productId IS NULL) OR (:productId IS NOT NULL AND s.productId = :productId))" +
            "AND ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status)) order by s.id DESC "
    )
    Page<SupplyRequest>findSupplyRequests(@Param("productId") Long productId,
                                           @Param("productName") String productName,
                                           @Param("askingQuantity") Long askingQuantity,
                                           @Param("askingPrice") BigDecimal askingPrice,
                                           @Param("startTime") Date startTime,
                                           @Param("endTime") Date endTime,
                                           @Param("referenceNo") String referenceNo,
                                           @Param("status") String status,
                                           @Param("warehouseId") Long warehouseId,
                                           @Param("supplierId") Long supplierId,
                                           @Param("unassigned") Boolean unassigned,
                                           Pageable pageable);

}
