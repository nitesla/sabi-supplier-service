package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
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

    public WareHouseResponse createWareHouse(WareHouseRequest request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse warehouse = mapper.map(request, WareHouse.class);
        boolean wareHouseExists = wareHouseRepository.existsByUserId(request.getUserId());
        if(wareHouseExists ){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Warehouse already exist");
        }
        warehouse.setCreatedBy(userCurrent.getId());
        warehouse.setIsActive(true);
        warehouse = wareHouseRepository.save(warehouse);
        log.debug("Create new State - {}"+ new Gson().toJson(warehouse));
        return mapper.map(warehouse, WareHouseResponse.class);
    }

    public WareHouseResponse updateWareHouse(WareHouseRequest request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = wareHouseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Warehouse Id does not exist!"));
        mapper.map(request, wareHouse);
        wareHouse.setUpdatedBy(userCurrent.getId());
        wareHouseRepository.save(wareHouse);
        log.debug("State record updated - {}"+ new Gson().toJson(wareHouse));
        return mapper.map(wareHouse, WareHouseResponse.class);
    }

    public Page<WareHouse> findWareHouse(long userId, PageRequest pageRequest){
        Page<WareHouse> warehouse = wareHouseRepository.findWarehouse(userId, pageRequest);
        if(warehouse == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return warehouse;
    }

    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = wareHouseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested State Id does not exist!"));
        wareHouse.setIsActive(request.isActive());
        wareHouse.setUpdatedBy(userCurrent.getId());
        wareHouseRepository.save(wareHouse);
    }

    public List<WareHouse> getAll(Boolean isActive){
        List<WareHouse> wareHouses = wareHouseRepository.findByIsActive(isActive);
        return wareHouses;
    }
}
