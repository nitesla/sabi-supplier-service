package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for Supplier Location crud operations
 */

@Repository
public interface SupplierLocationRepository extends JpaRepository<SupplierLocation, Long> {

    SupplierLocation findBySupplierIdAndStateId(Long supplierId, Long stateId);

    List<SupplierLocation> findByIsActiveOrderByIdDesc(Boolean isActive);



}
