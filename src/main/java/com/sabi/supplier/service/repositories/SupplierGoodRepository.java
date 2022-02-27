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

    SupplierGood findByVariantIdAndSupplierId(Long variantId,Long supplierId);

    int countAllBySupplierId(Long supplierId);

    @Query("SELECT s FROM SupplierGood s WHERE ((:isActive IS NULL) OR (:isActive IS NOT NULL AND s.isActive = :isActive))" +
            " AND ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND s.supplierId = :supplierId)) order by s.id desc ")
    List<SupplierGood> findByIsActive(@Param("isActive")Boolean isActive,
                                      @Param("supplierId")Long supplierId);

    @Query("SELECT c FROM SupplierGood c inner join ProductVariant pt on c.variantId = pt.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND c.supplierId = :supplierId))" +
            " AND ((:variantId IS NULL) OR (:variantId IS NOT NULL AND c.variantId = :variantId))" +
            " AND ((:variantName IS NULL) OR (:variantName IS NOT NULL AND pt.name = :variantName))  order by c.id desc ")
    Page<SupplierGood> findSupplierGoods(@Param("supplierId") Long supplierId,
                                         @Param("variantId") Long variantId,
                                        @Param("variantName") String variantName,Pageable pageable);
}
