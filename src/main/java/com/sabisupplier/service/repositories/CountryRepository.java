package com.sabisupplier.service.repositories;




import com.sabisupplierscore.models.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByName(String name);
    List<Country> findByIsActive(Boolean isActive);

    @Query("SELECT c FROM Country c WHERE ((:name IS NULL) OR (:name IS NOT NULL AND c.name = :name))" +
            " AND ((:code IS NULL) OR (:code IS NOT NULL AND c.code = :code))")
    Page<Country> findCountries(@Param("name") String name,
                                @Param("code") String code,
                                Pageable pageable);

}