package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WarehouseGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseGoodsRepository extends JpaRepository<WarehouseGoods, Long> {

    List<WarehouseGoods> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM WarehouseGoods c inner join WareHouse pt on c.warehouseId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            "AND((:warehouseId IS NULL) OR (:warehouseId IS NOT NULL AND c.warehouseId = :warehouseId))" +
            "AND((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND pt.supplierId = :supplierId))" +
            " AND ((:supplyGoodId IS NULL) OR (:supplyGoodId IS NOT NULL AND c.supplyGoodId = :supplyGoodId))"
    )
    Page<WarehouseGoods> findWarehouseGoods(@Param("warehouseId") Long warehouseId,
                                    @Param("supplyGoodId") Long supplyGoodId,
                                        @Param("supplierId") Long supplierId,
                                    Pageable pageable);
}
