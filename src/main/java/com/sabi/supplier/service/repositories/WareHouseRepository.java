package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long>, JpaSpecificationExecutor<WareHouse> {
    List<WareHouse> findByIsActive(Boolean isActive);

    WareHouse findWareHouseById(Long id);

    Boolean existsByAddress(String address);
}
