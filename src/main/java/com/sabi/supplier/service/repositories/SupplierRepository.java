package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for Supplier crud operations
 */

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

    Supplier findByName(String name);

    Supplier findSupplierById(Long id);

    List<Supplier> findByIsActive(Boolean isActive);



}
