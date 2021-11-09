package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    SupplierProduct findSupplierProductById(Long supplierProductId);

    List<SupplierProduct> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM SupplierProduct c WHERE ((:supplierID IS NULL) OR (:supplierID IS NOT NULL AND c.supplierID = :supplierID))" +
            " AND ((:productID IS NULL) OR (:productID IS NOT NULL AND c.productIDariantId = :productID))")
    Page<SupplierProduct> findSupplierProducts(@Param("supplierID") Long SupplierID,
                                            @Param("productId") Long variantId,
                                            Pageable pageable);
}
