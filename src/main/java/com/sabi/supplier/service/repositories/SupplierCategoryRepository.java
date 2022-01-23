package com.sabi.supplier.service.repositories;


import com.sabi.suppliers.core.models.SupplierBank;
import com.sabi.suppliers.core.models.SupplierCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for Supplier Category crud operations
 */

@Repository
public interface SupplierCategoryRepository extends JpaRepository<SupplierCategory, Long>, JpaSpecificationExecutor<SupplierCategory> {

    SupplierCategory findByName(String name);

    List<SupplierCategory> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT c FROM SupplierCategory c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name like %:name%))" +
            " AND ((:creditPeriod IS NULL) OR (:creditPeriod IS NOT NULL AND c.creditPeriod = :creditPeriod))" +
            " AND ((:isActive IS NULL) OR (:isActive IS NOT NULL AND c.isActive = :isActive)) order by c.id desc "
    )
    Page<SupplierCategory> findSupplierCategory(@Param("name") String name,
                                         @Param("creditPeriod") Integer creditPeriod,
                                            @Param("isActive") Boolean isActive,
                                         Pageable pageable);


}
