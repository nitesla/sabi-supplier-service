package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.SupplierBank;
import com.sabi.suppliers.core.models.SupplierLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT l FROM SupplierLocation l inner join State  s on l.stateId = s.id WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND l.supplierId = :supplierId))" +
//            " AND ((:stateId IS NULL) OR (:stateId IS NOT NULL AND l.stateId = :stateId))" +
            " AND ((:stateId IS NULL) OR (:stateId IS NOT NULL AND l.stateId = :stateId))" +
            " AND ((:stateName IS NULL) OR (:stateName IS NOT NULL AND s.name like %:stateName%)) order by l.id desc "
    )
    Page<SupplierLocation> findSupplierLocation(@Param("supplierId") Long supplierId,
                                            @Param("stateId") Long stateId,
                                                @Param("stateName") String stateName,
                                            Pageable pageable);


}
