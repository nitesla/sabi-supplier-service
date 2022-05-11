package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Manufacturer;
import com.sabi.suppliers.core.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    Manufacturer findByName(String name);
    List<Manufacturer> findByIsActiveOrderByIdDesc(Boolean isActive);

    Manufacturer findManufacturerById(Long id);

    @Query("SELECT c FROM Manufacturer c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name like %:name%)) order by c.id desc ")
//            " AND ((:code IS NULL) OR (:code IS NOT NULL AND c.code = :code))")
    Page<Manufacturer> findManufacturers(@Param("name") String name,
                                    Pageable pageable);
}
