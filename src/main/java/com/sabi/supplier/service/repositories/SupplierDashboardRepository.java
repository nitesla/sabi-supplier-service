package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierDashbaord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierDashboardRepository extends JpaRepository<SupplierDashbaord, Long> {
}
