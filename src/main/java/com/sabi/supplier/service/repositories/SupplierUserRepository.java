package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierUserRepository extends JpaRepository<SupplierUser,Long> {

    SupplierUser findSupplierUSerById(Long id);

    List<SupplierUser> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM SupplierUser c WHERE ((:userId IS NULL) OR (:userId IS NOT NULL AND c.userId = :userId))" +
            " AND ((:wareHouseId IS NULL) OR (:wareHouseId IS NOT NULL AND c.wareHouseId = :wareHouseId))" +
            " AND ((:roleId IS NULL) OR (:roleId IS NOT NULL AND c.roleId = :roleId))"
    )
    Page<SupplierUser> findSupplierUser(@Param("userId") Long userId,
                                            @Param("wareHouseId") Long wareHouseId,
                                            @Param("roleId") Long roleId,
                                            Pageable pageable);
}
