package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WareHouseGood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseGoodRepository extends JpaRepository<WareHouseGood, Long> {

    List<WareHouseGood> findByIsActiveOrderByIdDesc(Boolean isActive);

//    List<WareHouseGood> findByWarehouseIdOrderByIdDesc(Long warehouseId);

    Integer countAllByWarehouseId(Long warehouseId);


    WareHouseGood findBySupplierGoodIdAndWarehouseId(Long supplierGoodsId,Long warehouseId);

    @Query("SELECT c FROM WareHouseGood c inner join WareHouse pt on c.warehouseId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            "AND((:warehouseId IS NULL) OR (:warehouseId IS NOT NULL AND c.warehouseId = :warehouseId))" +
            "AND((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            "AND((:productId IS NULL) OR (:productId IS NOT NULL AND pt.productId = :productId))" +
            " AND ((:supplierGoodId IS NULL) OR (:supplierGoodId IS NOT NULL AND c.supplierGoodId = :supplierGoodId)) order by c.id desc "
    )
    Page<WareHouseGood> findWarehouseGood(@Param("warehouseId") Long warehouseId,
                                          @Param("supplierGoodId") Long supplierGoodId,
                                          @Param("supplierId") Long supplierId,
                                          @Param("productId") Long productId,
                                          Pageable pageable);
}
