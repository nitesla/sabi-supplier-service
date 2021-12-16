package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long>, JpaSpecificationExecutor<WareHouse> {

    @Query("SELECT s FROM WareHouse s WHERE ((:isActive IS NULL) OR (:isActive IS NOT NULL AND s.isActive = :isActive)) " +
            "and ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND s.supplierId = :supplierId))")
    List<WareHouse> findByIsActive(@Param("isActive")Boolean isActive,
                                   @Param("supplierId")Long supplierId);

    WareHouse findWareHouseById(Long id);

    Boolean existsByAddress(String address);
}
