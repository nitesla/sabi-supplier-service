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

    List<SupplierProduct> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT c FROM SupplierProduct c WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND c.supplierId = :supplierId))" +
            " AND ((:productId IS NULL) OR (:productId IS NOT NULL AND c.productId = :productId)) order by c.id desc ")
    Page<SupplierProduct> findSupplierProducts(@Param("supplierId") Long SupplierId,
                                            @Param("productId") Long variantId,
                                            Pageable pageable);

//    @Query("SELECT c FROM Country c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name = :name))" +
//            " AND ((:code IS NULL) OR (:code IS NOT NULL AND c.code = :code))")
//    Page<Country> findCountries(@Param("name") String name,
//                                @Param("code") String code,
//                                Pageable pageable);
}
