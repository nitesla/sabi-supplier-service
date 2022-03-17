package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Shipment findShipmentByWarehouseId (Long warehouseId);
    Shipment findShipmentById(Long id);


//    @Query("SELECT s FROM Shipment s inner join WareHouse pt on s.warehouseId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
//            "AND ((:startDate IS NULL) OR (:startDate IS NOT NULL AND  pt.createdDate <= :startDate)) " +
//            "AND ((:endDate IS NULL) OR (:endDate IS NOT NULL AND  pt.createdDate <= :endDate)) " )
//    List<Shipment>findShipmentBySupplierIds(@Param("supplierId")Long supplierId,
//                                           @Param("startDate") LocalDateTime startDate,
//                                           @Param("endDate") LocalDateTime endDate);



//        @Query("SELECT s FROM Shipment s inner join WareHouse pt on s.warehouseId = pt.id WHERE (pt.supplierId = :supplierId) AND (  pt.createdDate BETWEEN  ?2 and ?3)" )
//@Query("select sum(t.id) from SupplyRequest t where ( t.status = ?1) AND (  t.createdDate BETWEEN  ?2 and ?3)")
@Query("select s from Shipment s inner join WareHouse pt on s.warehouseId = pt.id where ( pt.supplierId = ?1) AND (  pt.createdDate BETWEEN  ?2 and ?3)")
    List<Shipment>findShipmentBySupplierId(@Param("supplierId")Long supplierId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Shipment s inner join WareHouse pt on s.warehouseId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" )
    List<Shipment>findShipmentBySupplierId(@Param("supplierId")Long supplierId);

    @Query("select t from Shipment t where ( t.paymentStatus = ?1) AND (  t.createdDate BETWEEN  ?2 and ?3)")
    List<Shipment>findShipmentByPaymentStatus(@Param("paymentStatus")String paymentStatus,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    List<Shipment> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT s FROM Shipment s WHERE ((:warehouseId IS NULL) OR (:warehouseId IS NOT NULL AND s.warehouseId = :warehouseId))" +
            " AND ((:logisticsPartnerId IS NULL) OR (:logisticsPartnerId IS NOT NULL AND s.logisticPartnerId = :logisticsPartnerId))" +
            "AND ((:logisticPartnerName IS NULL) OR (:logisticPartnerName IS NOT NULL AND s.logisticPartnerName like %:logisticPartnerName%))" +
            "AND ((:phoneNumber IS NULL) OR (:phoneNumber IS NOT NULL AND s.phoneNumber like %:phoneNumber%))" +
            "AND ((:vehicle IS NULL) OR (:vehicle IS NOT NULL AND s.vehicle like %:vehicle%))" +
            "AND ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status)) order by s.id desc " )
    Page<Shipment> findShipments(@Param("warehouseId") Long warehouseId,
                                      @Param("logisticsPartnerId") Long logisticsPartnerId,
                                      @Param("logisticPartnerName") String logisticPartnerName,
                                      @Param("phoneNumber") String phoneNumber,
                                      @Param("vehicle") String vehicle,
                                      @Param("status") String status,
                                      Pageable pageable);



    List<Shipment> findByFeedStatus (String feedStatus);
}
