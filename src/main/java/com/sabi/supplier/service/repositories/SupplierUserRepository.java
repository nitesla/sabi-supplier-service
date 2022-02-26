package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierUserRepository extends JpaRepository<SupplierUser, Long> {

    SupplierUser findByUserId(Long userId);

    List<SupplierUser> findBySupplierId(Long supplierId);
}
