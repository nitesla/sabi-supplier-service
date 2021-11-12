package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Country;
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

    List<SupplierGood> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM SupplierGood c WHERE ((:supplierProductId IS NULL) OR (:supplierProductId IS NOT NULL AND c.supplierProductId = :supplierProductId))" +
            " AND ((:variantId IS NULL) OR (:variantId IS NOT NULL AND c.variantId = :variantId))"
//            "AND ((:price IS NULL) OR (:price IS NOT NULL AND c.price = :price))"
    )
    Page<SupplierGood> findSupplierGoods(@Param("supplierProductId") Long supplierProductId,
                                        @Param("variantId") Long variantId,
//                                        @Param("price") double price,
                                Pageable pageable);
}
