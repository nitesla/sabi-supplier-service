package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    Preference findBySupplierId(Long supplierId);
}
