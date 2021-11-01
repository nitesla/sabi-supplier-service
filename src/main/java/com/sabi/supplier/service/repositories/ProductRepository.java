package com.sabi.supplier.service.repositories;

import com.sabisupplierscore.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByName(String name);
    List<Product> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM Product c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name = :name))" )
//            " AND ((:code IS NULL) OR (:code IS NOT NULL AND c.code = :code))")
    Page<Product> findProducts(@Param("name") String name,
                               Pageable pageable);
}
