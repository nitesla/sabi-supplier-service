package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.ProductSuggestion;
import com.sabi.suppliers.core.models.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSuggestionRepository extends JpaRepository<ProductSuggestion, Long> {

    ProductSuggestion findByName(String name);

    @Query("SELECT s FROM ProductSuggestion s WHERE ((:name IS NULL) OR (:name IS NOT NULL AND s.name like %:name%))" +
            " AND ((:manufacturer IS NULL) OR (:manufacturer IS NOT NULL AND s.manufacturer like %:manufacturer))" +
//            "AND ((:picture IS NULL) OR (:picture IS NOT NULL AND s.picture like %:picture%))" +
//            "AND ((:rowPerPack IS NULL) OR (:rowPerPack IS NOT NULL AND s.rowPerPack = :rowPerPack))" +
            "AND ((:status IS NULL) OR (:status IS NOT NULL AND s.status = :status)) order by s.id desc ")
    Page<ProductSuggestion> findProductSuggestions(@Param("name") String name,
                                            @Param("manufacturer") String manufacturer,
                                            @Param("status") String status,
                                            Pageable pageable);
}
