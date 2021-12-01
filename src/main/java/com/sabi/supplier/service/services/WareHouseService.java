package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.GenericSpecification;
import com.sabi.supplier.service.helper.SearchCriteria;
import com.sabi.supplier.service.helper.SearchOperation;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.WareHouseRepository;
import com.sabi.suppliers.core.dto.request.WareHouseRequest;
import com.sabi.suppliers.core.dto.response.WareHouseResponse;
import com.sabi.suppliers.core.models.WareHouse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class WareHouseService {
    private final WareHouseRepository wareHouseRepository;

    private final Validations validations;
    private final ModelMapper mapper;

    public WareHouseService(WareHouseRepository wareHouseRepository, Validations validations, ModelMapper mapper) {
        this.wareHouseRepository = wareHouseRepository;
        this.validations = validations;
        this.mapper = mapper;
    }

    public WareHouseResponse createWareHouse(WareHouseRequest request) {
        validations.validateWareHouse(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = mapper.map(request, WareHouse.class);
        boolean wareHouseExists = wareHouseRepository.existsByAddress(request.getAddress());
        if (wareHouseExists) {
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " wareHouse already exist");
        }
        wareHouse.setCreatedBy(userCurrent.getId());
        wareHouse.setIsActive(false);
        wareHouse = wareHouseRepository.save(wareHouse);
        log.debug("Create new State - {}" + new Gson().toJson(wareHouse));
        return mapper.map(wareHouse, WareHouseResponse.class);
    }

    public WareHouseResponse updateWareHouse(WareHouseRequest request) {
        validations.validateWareHouse(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = wareHouseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested wareHouse Id does not exist!"));
        mapper.map(request, wareHouse);
        wareHouse.setUpdatedBy(userCurrent.getId());
        wareHouseRepository.save(wareHouse);
        log.debug("State record updated - {}" + new Gson().toJson(wareHouse));
        return mapper.map(wareHouse, WareHouseResponse.class);
    }

    public Page<WareHouse> findWareHouses(Long productId, Long supplierId, Long stateId, String address,
                                          String contactPerson, String contactPhone, String contactEmail,
                                          String longitude, String latitude, Long warehouseUserId,
                                          Long userId, Long lgaId, Long productCount, String name,
                                          BigDecimal productCost, Boolean isActive, PageRequest pageRequest) {
        GenericSpecification<WareHouse> genericSpecification = new GenericSpecification<>();

        if (productId != null) {
            genericSpecification.add(new SearchCriteria("productId", productId, SearchOperation.EQUAL));
        }

        if (supplierId != null) {
            genericSpecification.add(new SearchCriteria("supplierId", supplierId, SearchOperation.EQUAL));
        }
        if (stateId != null) {
            genericSpecification.add(new SearchCriteria("stateId", stateId, SearchOperation.EQUAL));
        }
        if (address != null && !address.isEmpty()) {
            genericSpecification.add(new SearchCriteria("address", address, SearchOperation.MATCH));
        }
        if (contactPerson != null && !contactPerson.isEmpty()) {
            genericSpecification.add(new SearchCriteria("contactPerson", contactPerson, SearchOperation.MATCH));
        }
        if (contactPhone != null && !contactPhone.isEmpty()) {
            genericSpecification.add(new SearchCriteria("contactPhone", contactPhone, SearchOperation.MATCH));
        }
        if (contactEmail != null && !contactEmail.isEmpty()) {
            genericSpecification.add(new SearchCriteria("contactEmail", contactEmail, SearchOperation.EQUAL));
        }
        if (longitude != null && !longitude.isEmpty()) {
            genericSpecification.add(new SearchCriteria("longitude", longitude, SearchOperation.EQUAL));
        }
        if (latitude != null && !latitude.isEmpty()) {
            genericSpecification.add(new SearchCriteria("latitude", latitude, SearchOperation.EQUAL));
        }
        if (warehouseUserId != null) {
            genericSpecification.add(new SearchCriteria("warehouseUserId", warehouseUserId, SearchOperation.EQUAL));
        }
        if (userId != null) {
            genericSpecification.add(new SearchCriteria("userId", userId, SearchOperation.EQUAL));
        }
        if (lgaId != null) {
            genericSpecification.add(new SearchCriteria("lgaId", lgaId, SearchOperation.EQUAL));
        }
        if (productCount != null) {
            genericSpecification.add(new SearchCriteria("productCount", productCount, SearchOperation.EQUAL));
        }
        if (name != null && !name.isEmpty()) {
            genericSpecification.add(new SearchCriteria("name", name, SearchOperation.MATCH));
        }
        if (productCost != null) {
            genericSpecification.add(new SearchCriteria("productCost", productCost, SearchOperation.EQUAL));
        }
        if (isActive != null) {
            genericSpecification.add(new SearchCriteria("isActive", isActive, SearchOperation.EQUAL));
        }

        Page<WareHouse> wareHouses = wareHouseRepository.findAll(genericSpecification, pageRequest);

        return wareHouses;
    }

    public WareHouseResponse findWareHouse(long id) {
        WareHouse wareHouse = wareHouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supply Request Id does not exist!"));
        return mapper.map(wareHouse, WareHouseResponse.class);
    }

    public void enableDisEnableState(EnableDisEnableDto request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = wareHouseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested State Id does not exist!"));
        wareHouse.setIsActive(request.isActive());
        wareHouse.setUpdatedBy(userCurrent.getId());
        wareHouseRepository.save(wareHouse);
    }

    public List<WareHouse> getAll(Boolean isActive) {
        return wareHouseRepository.findByIsActive(isActive);
    }
}
