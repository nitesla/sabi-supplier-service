package com.sabi.supplier.service.repositories;


import com.sabi.suppliers.core.models.SupplierCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for Supplier Category crud operations
 */

@Repository
public interface SupplierCategoryRepository extends JpaRepository<SupplierCategory, Long>, JpaSpecificationExecutor<SupplierCategory> {

    SupplierCategory findByName(String name);

    List<SupplierCategory> findByIsActive(Boolean isActive);



}
