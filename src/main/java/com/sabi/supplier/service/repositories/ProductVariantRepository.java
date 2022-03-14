package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    ProductVariant findByName(String name);
    ProductVariant findProductVariantById(Long id);
    List<ProductVariant> findProductVariantByProductId(Long productId);

    List<ProductVariant> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT s FROM ProductVariant s WHERE ((:name IS NULL) OR (:name IS NOT NULL AND s.name like %:name%))" +
            " AND ((:productId IS NULL) OR (:productId IS NOT NULL AND s.productId = :productId))" +
            "AND ((:picture IS NULL) OR (:picture IS NOT NULL AND s.picture like %:picture%))" +
            "AND ((:rowPerPack IS NULL) OR (:rowPerPack IS NOT NULL AND s.rowPerPack = :rowPerPack))" +
            "AND ((:pieceaPerRow IS NULL) OR (:pieceaPerRow IS NOT NULL AND s.pieceaPerRow = :pieceaPerRow)) order by s.id desc "
    )
    Page<ProductVariant> findProductVariant(@Param("name") String name,
                                            @Param("productId") Long productId,
                                            @Param("picture") String picture,
                                            @Param("rowPerPack") Integer rowPerPack,
                                            @Param("pieceaPerRow") Integer pieceaPerRow,
                                            Pageable pageable);
}
