package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRoleRespository extends JpaRepository<SupplierRole,Long> {

    SupplierRole findSupplierUSerById(Long id);

    List<SupplierRole> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM SupplierRole c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name = :name))" +
            " AND ((:partnerId IS NULL) OR (:partnerId IS NOT NULL AND c.partnerId = :partnerId))" +
            " AND ((:roleId IS NULL) OR (:roleId IS NOT NULL AND c.roleId = :roleId))"
    )
    Page<SupplierRole> findSupplierUser(@Param("name") String name,
                                        @Param("partnerId") Long partnerId,
                                        @Param("roleId") Long roleId,
                                        Pageable pageable);

}
