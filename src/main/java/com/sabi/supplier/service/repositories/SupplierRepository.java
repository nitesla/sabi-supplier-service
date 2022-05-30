package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for Supplier crud operations
 */

@SuppressWarnings("ALL")
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Supplier findByName(String name);

    Supplier findSupplierById(Long id);

    Supplier findByUserId(Long userId);

    List<Supplier> findByIsActiveOrderByIdDesc(Boolean isActive);


    @Query("SELECT s FROM Supplier s WHERE ((:name IS NULL) OR (:name IS NOT NULL AND s.name like %:name%)) order by s.id DESC ")
    Page<Supplier> findALLSupplier(@Param("name") String name, Pageable pageable);


}
