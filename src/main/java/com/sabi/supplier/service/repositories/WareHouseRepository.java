package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WareHouse;
import com.sabi.suppliers.core.models.WareHouseGood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, Long>, JpaSpecificationExecutor<WareHouse> {

    @Query("SELECT s FROM WareHouse s WHERE ((:isActive IS NULL) OR (:isActive IS NOT NULL AND s.isActive = :isActive)) " +
            "and ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND s.supplierId = :supplierId)) order by s.id desc ")
    List<WareHouse> findByIsActive(@Param("isActive")Boolean isActive,
                                   @Param("supplierId")Long supplierId);

    @Query("SELECT c FROM WareHouse c WHERE ((:supplierId IS NULL) OR (:supplierId IS NOT NULL AND c.supplierId = :supplierId))" +
            "AND((:productId IS NULL) OR (:productId IS NOT NULL AND c.productId = :productId))" +
            "AND((:stateId IS NULL) OR (:stateId IS NOT NULL AND c.stateId = :stateId))" +
            "AND((:contactPerson IS NULL) OR (:contactPerson IS NOT NULL AND c.contactPerson like %:contactPerson%))" +
            "AND((:contactPhone IS NULL) OR (:contactPhone IS NOT NULL AND c.contactPhone like %:contactPhone%))" +
            "AND((:contactEmail IS NULL) OR (:contactEmail IS NOT NULL AND c.contactEmail like %:contactEmail%))" +
            "AND((:longitude IS NULL) OR (:longitude IS NOT NULL AND c.longitude like %:longitude%))" +
            "AND((:latitude IS NULL) OR (:latitude IS NOT NULL AND c.latitude like %:latitude%))" +
            "AND((:userId IS NULL) OR (:userId IS NOT NULL AND c.userId = :userId))" +
            "AND((:lgaId IS NULL) OR (:lgaId IS NOT NULL AND c.lgaId = :lgaId))" +
            "AND((:productCount IS NULL) OR (:productCount IS NOT NULL AND c.productCount = :productCount))" +
            "AND((:name IS NULL) OR (:name IS NOT NULL AND c.name like %:name%))" +
            "AND((:isActive IS NULL) OR (:isActive IS NOT NULL AND c.isActive = :isActive))" +
            " AND ((:address IS NULL) OR (:address IS NOT NULL AND c.address like %:address%)) order by c.id desc "
    )
    Page<WareHouse> findWareHoues(@Param("productId") Long productId,
                                          @Param("supplierId") Long supplierId,
                                          @Param("stateId") Long stateId,
                                        @Param("address") String address,
                                        @Param("contactPerson") String contactPerson,
                                         @Param("contactPhone") String contactPhone,
                                         @Param("contactEmail") String contactEmail,
                                      @Param("longitude") String longitude,
                                      @Param("latitude") String latitude,
                                      @Param("userId") Long userId,
                                      @Param("lgaId") Long lgaId,
                                      @Param("productCount") Long productCount,
                                      @Param("name") String name,
                                      @Param("isActive") Boolean isActive,
                                          Pageable pageable);

    WareHouse findWareHouseById(Long id);

    Boolean existsByAddress(String address);
}
