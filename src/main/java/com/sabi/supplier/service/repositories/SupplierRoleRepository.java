package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRoleRepository extends JpaRepository<SupplierRole,Long> {

    SupplierRole findByRoleId (Long roleId);
    SupplierRole findBySupplierId(Long partnerId);

}
