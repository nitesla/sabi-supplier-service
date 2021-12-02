package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.State;
import com.sabi.suppliers.core.models.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findStockByAction(String action);


    List<Stock> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM Stock c WHERE ((:supplyGoodId IS NULL) OR (:supplyGoodId IS NOT NULL AND c.supplyGoodId = :supplyGoodId))" +
            " AND ((:action IS NULL) OR (:action IS NOT NULL AND c.action = :action))" +
            " AND ((:userId IS NULL) OR (:userId IS NOT NULL AND c.userId = :userId))"
    )
    Page<Stock> findStocks(@Param("supplyGoodId") Long supplyGoodId,
                                  @Param("action") String action,
                                  @Param("userId") Long userId,
//                                        @Param("price") double price,
                                  Pageable pageable);
}
