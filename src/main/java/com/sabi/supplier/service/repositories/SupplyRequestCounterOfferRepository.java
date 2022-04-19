package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplyRequestCounterOfferRepository extends JpaRepository<SupplyRequestCounterOffer, Long> {

    List<SupplyRequestCounterOffer> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT c FROM SupplyRequestCounterOffer c WHERE ((:price IS NULL) OR (:price IS NOT NULL AND c.price = :price))" +
            " AND ((:supplyRequestId IS NULL) OR (:supplyRequestId IS NOT NULL AND c.supplyRequestId = :supplyRequestId))" +
            " AND ((:quantity IS NULL) OR (:quantity IS NOT NULL AND c.quantity = :quantity))" +
            " AND ((:userId IS NULL) OR (:userId IS NOT NULL AND c.userId =: userId))  order by c.id desc ")
    Page<SupplyRequestCounterOffer> findSupplyRequestCounterOffer(@Param("price") BigDecimal price,
                                         @Param("supplyRequestId") Long supplyRequestId,
                                                     @Param("quantity") Integer quantity,
                                         @Param("userId") Long userId, Pageable pageable);
}
