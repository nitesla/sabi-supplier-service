package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Product;
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
    Product findProductById(Long id);
    List<Product> findByIsActiveOrderByIdDesc(Boolean isActive);

    Integer countAllById(Long productId);

    @Query("SELECT c FROM Product c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name like %:name%)) " +
            " AND ((:productCategoryId IS NULL) OR (:productCategoryId IS NOT NULL AND c.productCategoryId = :productCategoryId)) order by c.id desc ")
    Page<Product> findProducts(@Param("name") String name,
                               @Param("productCategoryId")  Long productCategoryId,
                               Pageable pageable);
}
