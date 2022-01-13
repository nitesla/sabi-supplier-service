package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierBankRepository extends JpaRepository<SupplierBank, Long>, JpaSpecificationExecutor<SupplierBank> {
    SupplierBank findBySupplierId(Long supplierId);

    List<SupplierBank> findByIsActiveOrderByIdDesc(Boolean isActive);
}
