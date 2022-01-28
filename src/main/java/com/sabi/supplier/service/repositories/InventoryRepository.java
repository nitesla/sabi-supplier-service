package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Inventory findByName(String name);

    List<Inventory> findByIsActiveOrderByIdDesc(Boolean isActive);

    List<Inventory> findInventoriesByWarehouseId(Long warehouseId);

    @Query("SELECT c FROM Inventory c WHERE ((:supplierGoodId IS NULL) OR (:supplierGoodId IS NOT NULL AND c.supplierGoodId = :supplierGoodId))" +
            " AND ((:warehouseId IS NULL) OR (:warehouseId IS NOT NULL AND c.warehouseId = :warehouseId)) order by c.id desc " )
    Page<Inventory> findInventories(@Param("supplierGoodId") Long supplierRequestId,
                            @Param("warehouseId")Long shipmentId,
                            Pageable pageable);
}
