package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Stock;
import com.sabi.suppliers.core.models.SupplierBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierBankRepository extends JpaRepository<SupplierBank, Long>, JpaSpecificationExecutor<SupplierBank> {
    SupplierBank findBySupplierId(Long supplierId);

    List<SupplierBank> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT b FROM SupplierBank b WHERE ((:accountNumber IS NULL) OR (:accountNumber IS NOT NULL AND b.accountNumber = :accountNumber))" +
            " AND ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND b.supplierId = :supplierId)) order by b.id desc "
    )
    Page<SupplierBank> findSupplierBanks(@Param("accountNumber") String accountNumber,
                           @Param("supplierId") Long supplierId,
                            Pageable pageable);
}
