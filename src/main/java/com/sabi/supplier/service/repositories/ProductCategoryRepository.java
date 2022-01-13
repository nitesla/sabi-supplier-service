package com.sabi.supplier.service.repositories;


import com.sabi.suppliers.core.models.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    ProductCategory findByName(String name);
    List<ProductCategory> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT c FROM ProductCategory c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name = :name)) order by c.id desc " )
//            " AND ((:code IS NULL) OR (:code IS NOT NULL AND c.code = :code))")
    Page<ProductCategory> findProductCategories(@Param("name") String name,
                                       Pageable pageable);
}
