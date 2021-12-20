package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierGood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierGoodRepository extends JpaRepository<SupplierGood, Long> {

    SupplierGood findSupplierGoodById(Long supplierGoodId);

    @Query("SELECT s FROM SupplierGood s WHERE ((:isActive IS NULL) OR (:isActive IS NOT NULL AND s.isActive = :isActive))" +
            " AND ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND s.supplierId = :supplierId))")
    List<SupplierGood> findByIsActive(@Param("isActive")Boolean isActive,
                                      @Param("supplierId")Long supplierId);

    @Query("SELECT c FROM SupplierGood c WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND c.supplierId = :supplierId))" +
            " AND ((:variantId IS NULL) OR (:variantId IS NOT NULL AND c.variantId = :variantId))")
    Page<SupplierGood> findSupplierGoods(@Param("supplierId") Long supplierId,
                                        @Param("variantId") Long variantId,Pageable pageable);
}
