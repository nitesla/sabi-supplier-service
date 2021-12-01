package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRoleRespository extends JpaRepository<SupplierRole,Long> {



}
