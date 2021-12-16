package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Shipment findShipmentByWarehouseId (Long warehouseId);
    Shipment findShipmentById(Long id);

    List<Shipment> findByIsActive(Boolean isActive);

    @Query("SELECT s FROM Shipment s WHERE ((:warehouseId IS NULL) OR (:warehouseId IS NOT NULL AND s.warehouseId = :warehouseId))" +
            " AND ((:logisticsPartnerId IS NULL) OR (:logisticsPartnerId IS NOT NULL AND s.logisticPartnerId = :logisticsPartnerId))" +
            "AND ((:logisticPartnerName IS NULL) OR (:logisticPartnerName IS NOT NULL AND s.logisticPartnerName = :logisticPartnerName))" +
            "AND ((:phoneNumber IS NULL) OR (:phoneNumber IS NOT NULL AND s.phoneNumber = :phoneNumber))" +
            "AND ((:vehicle IS NULL) OR (:vehicle IS NOT NULL AND s.vehicle = :vehicle))" +
            "AND ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status))" )
    Page<Shipment> findShipments(@Param("warehouseId") Long warehouseId,
                                      @Param("logisticsPartnerId") Long logisticsPartnerId,
                                      @Param("logisticPartnerName") String logisticPartnerName,
                                      @Param("phoneNumber") String phoneNumber,
                                      @Param("vehicle") String vehicle,
                                      @Param("status") String status,
                                      Pageable pageable);
}
