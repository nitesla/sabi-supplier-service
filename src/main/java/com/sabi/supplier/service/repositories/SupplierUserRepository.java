package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierUserRepository extends JpaRepository<SupplierUser, Long> {

    SupplierUser findByUserId(Long userId);
}
