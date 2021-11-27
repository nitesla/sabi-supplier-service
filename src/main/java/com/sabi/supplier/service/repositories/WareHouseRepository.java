package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WareHouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {

    Boolean existsByUserId(Long userId);

    @Query("SELECT s FROM WareHouse s WHERE ((:userId IS NULL) OR (:userId IS NOT NULL AND s.userId = :userId))")
    Page<WareHouse> findWarehouse(@Param("userId") Long userId, Pageable pageable);

    List<WareHouse> findByIsActive(Boolean isActive);
}
