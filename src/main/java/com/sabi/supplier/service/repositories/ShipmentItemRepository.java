package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.ShipmentItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {

//    ShipmentItem findByName(String name);
    ShipmentItem findShipmentItemById(Long id);
    ShipmentItem findShipmentItemBySupplierRequestId(Long supplyRequestId);
    List<ShipmentItem> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT c FROM ShipmentItem c WHERE ((:supplierRequestId IS NULL) OR (:supplierRequestId IS NOT NULL AND c.supplierRequestId = :supplierRequestId))" +
            " AND ((:shipmentId IS NULL) OR (:shipmentId IS NOT NULL AND c.shipmentId = :shipmentId)) order by c.id desc " )
    Page<ShipmentItem> findAll(@Param("supplierRequestId") Long supplierRequestId,
                                    @Param("shipmentId")Long shipmentId,
                                    Pageable pageable);
}
