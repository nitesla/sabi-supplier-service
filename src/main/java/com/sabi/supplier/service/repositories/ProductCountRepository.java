package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.ProductCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCountRepository extends JpaRepository<ProductCount, Long> {

    ProductCount findProductCountByNameAndShipmentId(String name,Long shipmentId);

    List<ProductCount> findProductCountByShipmentId(Long shipmentId);

    Integer countAllByNameAndShipmentId(String name,Long shipmentId);
}
