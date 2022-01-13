package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * This interface is responsible for State crud operations
 */

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    State findByName(String name);

    State findStateById(Long id);

    @Query("SELECT s FROM State s WHERE ((:countryId IS NULL) OR (:countryId IS NOT NULL AND s.countryId = :countryId))")
    List<State> findByCountryId(Long countryId);

    List<State> findByIsActiveOrderByIdDesc(Boolean isActive);

    @Query("SELECT s FROM State s WHERE ((:name IS NULL) OR (:name IS NOT NULL AND s.name = :name)) order by s.id desc ")
    Page<State> findStates(@Param("name") String name, Pageable pageable);

}
