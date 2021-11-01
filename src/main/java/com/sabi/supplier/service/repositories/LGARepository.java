package com.sabi.supplier.service.repositories;






import com.sabisupplierscore.models.LGA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for LGA crud operations
 */

@Repository
public interface LGARepository extends JpaRepository<LGA, Long> {

       LGA findByName(String name);

       List<LGA> findByIsActive(Boolean isActive);

       @Query("SELECT l FROM LGA l WHERE ((:name IS NULL) OR (:name IS NOT NULL AND l.name = :name))")
       Page<LGA> findLgas(@Param("name") String name, Pageable pageable);
}
