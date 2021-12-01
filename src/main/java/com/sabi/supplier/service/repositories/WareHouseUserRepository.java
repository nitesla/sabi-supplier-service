package com.sabi.supplier.service.repositories;

import com.sabi.suppliers.core.models.WareHouseUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseUserRepository extends JpaRepository<WareHouseUser, Long>, JpaSpecificationExecutor<WareHouseUser> {

    Boolean existsByUserId(Long userId);


    List<WareHouseUser> findByIsActive(Boolean isActive);
}
