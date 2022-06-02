package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Shipment;
import com.sabi.suppliers.core.models.SupplyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long>, JpaSpecificationExecutor<SupplyRequest> {
    List<SupplyRequest> findByIsActiveOrderByIdDesc(Boolean isActive);

    List<SupplyRequest> findSupplyRequestByStatus(String status);


    @Query("select s from Shipment s inner join WareHouse pt on s.warehouseId = pt.id where ( pt.supplierId = ?1) AND (  pt.createdDate BETWEEN  ?2 and ?3)")
    List<Shipment>findShipmentBySupplierId(@Param("supplierId")Long supplierId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);


//    @Query("select s from SupplyRequest s where ( s.status = ?1) AND  (s.expireTime BETWEEN  ?2 and ?3)")
//    List<SupplyRequest> findSupplyRequest(@Param("status") String status,
//                                          @Param("startDate") LocalDateTime startDate,
//                                           @Param("endDate") LocalDateTime endDate);
    //check//
//    @Query("select s from SupplyRequest s where ( s.status = ?1) AND  (s.expireTime NOT BETWEEN  ?2 and ?3)")
//    @Query("SELECT s FROM Stock s WHERE ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status))" +
//            " AND ((:expiredTime IS NULL) OR (:expiredTime IS NOT NULL AND s.expiredTime = :expiredTime%))")


    @Query("SELECT s FROM SupplyRequest s WHERE ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status))" +
            "AND ((:expireTime IS NULL) OR (:expireTime IS NOT NULL AND  s.expiryTime = :expireTime)) ")
    List<SupplyRequest> findSupplyRequestnew(@Param("status") String status,
                                          @Param("expireTime") String expireTime);

//    @Query("SELECT d FROM SupplyRequest d WHERE ( d.status = ?1) AND (  d.expireTime BETWEEN  ?2 and ?3)")
//    List<SupplyRequest> findSupplyRequest(@Param("status") String status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Boolean existsByReferenceNo(String referenceNo);

    SupplyRequest findSupplyRequestById(Long id);

    @Query("SELECT d FROM SupplyRequest d WHERE ( d.supplierId = ?1) AND (  d.createdDate BETWEEN  ?2 and ?3)")
    List<SupplyRequest> findSupplyRequestBySupplierId(@Param("supplierId") Long supplierId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


@Query("SELECT d FROM SupplyRequest d WHERE ( d.productId = ?1) AND (  d.createdDate BETWEEN  ?2 and ?3)")
    List<SupplyRequest> findSupplyRequestByProductId(@Param("productId") Long productId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<SupplyRequest> findSupplyRequestByProductId(Long productId);

    List<SupplyRequest>findByProductIdOrderByPriceDesc(Long productId);

        @Query("select sum(t.id) from SupplyRequest t where ( t.status = ?1) AND (  t.createdDate BETWEEN  ?2 and ?3)")
    Integer countAllByStatus(String status,LocalDateTime startDate,LocalDateTime endDate);

//    @Query("select sum(t.supplierId) from WareHouse t where ( t.supplierId = ?1) AND (  t.createdDate BETWEEN  ?2 and ?3)")
//    int countAllBySupplierId(Long id, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM SupplyRequest s left join WareHouse pt on s.warehouseId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            " AND ((:productName IS NULL) OR (:productName IS NOT NULL AND s.productName like %:productName%))" +
            "AND ((:askingQuantity IS NULL) OR (:askingQuantity IS NOT NULL AND s.askingQuantity = :askingQuantity))" +
            "AND ((:askingPrice IS NULL) OR (:askingPrice IS NOT NULL AND s.askingPrice = :askingPrice))" +
            "AND ((:startTime IS NULL) OR (:startTime IS NOT NULL AND s.startTime = :startTime))" +
            "AND ((:endTime IS NULL) OR (:endTime IS NOT NULL AND s.endTime = :endTime))" +
            "AND ((:referenceNo IS NULL) OR (:referenceNo IS NOT NULL AND s.referenceNo like %:referenceNo%))" +
            "AND ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status))" +
            "AND ((:warehouseId IS NULL) OR (:warehouseId IS NOT NULL AND s.warehouseId = :warehouseId))" +
            "AND ((:unassigned IS NULL) OR (:unassigned IS NOT NULL AND s.unassigned = :unassigned))" +
//            "AND ((:warehouseId IS NULL and :unassigned is null )OR (s.warehouseId IS NULL and :unassigned = true ) OR (:warehouseId IS NOT NULL AND s.warehouseId = :warehouseId))" +
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
