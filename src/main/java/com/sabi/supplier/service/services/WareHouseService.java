package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.DefaultWarehouseRequest;
import com.sabi.suppliers.core.dto.request.WareHouseRequest;
import com.sabi.suppliers.core.models.*;
import com.sabi.suppliers.core.models.response.WareHouseResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WareHouseService {

    @Autowired
    WareHouseGoodRepository wareHouseGoodRepository;
    @Autowired
    ProductRepository productRepository;
    private final WareHouseRepository wareHouseRepository;
    private final Validations validations;
    private final ModelMapper mapper;
    private final StateRepository stateRepository;
    private final LGARepository lgaRepository;
    private final WareHouseUserRepository wareHouseUserRepository;

    public WareHouseService(StateRepository stateRepository,WareHouseRepository wareHouseRepository,LGARepository lgaRepository,
                            WareHouseUserRepository wareHouseUserRepository,Validations validations, ModelMapper mapper) {
        this.stateRepository = stateRepository;
        this.lgaRepository = lgaRepository;
        this.wareHouseRepository = wareHouseRepository;
        this.validations = validations;
        this.mapper = mapper;
        this.wareHouseUserRepository = wareHouseUserRepository;
    }

    public WareHouseResponse createWareHouse(WareHouseRequest request) {
        validations.validateWareHouse(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = mapper.map(request, WareHouse.class);
        boolean wareHouseExists = wareHouseRepository.existsByAddress(request.getAddress());
        if (wareHouseExists) {
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " wareHouse already exist");
        }
        if (request.getProductId() != null) {
            int savedProduct = productRepository.countAllById(request.getProductId());
            wareHouse.setProductCount(savedProduct);
        }
        Integer savedWarehouse = wareHouseRepository.countAllByUserId(request.getUserId());
        State savedState = stateRepository.findStateById(request.getStateId());
        LGA savedLga = lgaRepository.findLGAById(request.getLgaId());
        wareHouse.setWareHouseUserCount(savedWarehouse);
        wareHouse.setStateName(savedState.getName());
        wareHouse.setLgaName(savedLga.getName());
        wareHouse.setCreatedBy(userCurrent.getId());
        wareHouse.setIsActive(false);
        wareHouse.setIsDefault(false);
        wareHouse = wareHouseRepository.save(wareHouse);
        log.debug("Create new WareHouse - {}" + new Gson().toJson(wareHouse));
        return mapper.map(wareHouse, WareHouseResponse.class);
    }

    public WareHouseResponse updateWareHouse(WareHouseRequest request) {
        validations.validateWareHouse(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouse wareHouse = wareHouseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested wareHouse Id does not exist!"));
        mapper.map(request, wareHouse);
        if (request.getProductId() != null) {
            int savedProduct = productRepository.countAllById(request.getProductId());
            wareHouse.setProductCount(savedProduct);
        }
        State savedState = stateRepository.findStateById(request.getStateId());
        LGA savedLga = lgaRepository.findLGAById(request.getLgaId());
        wareHouse.setStateName(savedState.getName());
        wareHouse.setLgaName(savedLga.getName());
        wareHouse.setUpdatedBy(userCurrent.getId());
        wareHouseRepository.save(wareHouse);
        log.debug("wareHouse record updated - {}" + new Gson().toJson(wareHouse));
        return mapper.map(wareHouse, WareHouseResponse.class);
    }

    public WareHouse setWareHouseAsDefault(DefaultWarehouseRequest request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        List<WareHouse> wareHouseList = wareHouseRepository.findAll();
        wareHouseList.forEach(wareHouse -> {
            wareHouse.setIsDefault(false);
            wareHouseRepository.save(wareHouse);
        });
//        WareHouse wareHouses = wareHouseRepository.findAllBySupplierIdAndIsDefault(request.getSupplierId(),true);
//        if (wareHouses.equals("null") || wareHouses.equals("")){
//            List<WareHouse> wareHouse = wareHouseRepository.findAll();
//            wareHouse.forEach(warehouseList -> {
//                warehouseList.setIsDefault(false);
//            });
//        }
//        wareHouses.setIsDefault(false);
        WareHouse wareHouse = wareHouseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested wareHouse Id does not exist!"));

        if (wareHouse.getIsActive().equals(true)) {
            wareHouse.setIsDefault(true);
        } else throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION,"Warehouse is inactive");
        wareHouseRepository.save(wareHouse);
//        return mapper.map(wareHouse, WareHouseResponse.class);
        return wareHouse;
    }

    public Page<WareHouse> findWareHouses(Long productId, Long supplierId, Long stateId, String address,
                                         String contactPerson, String contactPhone, String contactEmail, String longitude, String latitude,
                                         Long userId, Long lgaId, Long productCount, String name,
                                         Boolean isActive, PageRequest pageRequest){
        Page<WareHouse> wareHouses = wareHouseRepository.findWareHoues(productId,supplierId,stateId,address,contactPerson,contactPhone,contactEmail,longitude,latitude,userId,lgaId,productCount,name,isActive,pageRequest);
        if(wareHouses == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        wareHouses.forEach(wareHouse ->{
            State stateExist = stateRepository.getOne(wareHouse.getStateId());
            wareHouse.setStateName(stateExist.getName());
            LGA lga = lgaRepository.getOne(wareHouse.getLgaId());
            wareHouse.setLgaName(lga.getName());
            wareHouse.setWareHouseUserCount(getWareHouseUsers(wareHouse.getId()));
            wareHouse.setProductCount(getProductCount(wareHouse.getId()));
        });
        return wareHouses;

    }

    public WareHouseResponse findWareHouse(long id) {
        WareHouse wareHouse = wareHouseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested WareHouse Id does not exist!"));
        WareHouseResponse wareHouseResponse = mapper.map(wareHouse, WareHouseResponse.class);
        wareHouseResponse.setWareHouseUserCount(getWareHouseUsers(id));
        State state = stateRepository.getOne(wareHouse.getStateId());
        wareHouseResponse.setStateName(state.getName());
        LGA lga = lgaRepository.getOne(wareHouse.getLgaId());
        wareHouseResponse.setLgaName(lga.getName());
        wareHouseResponse.setProductCount(getProductCount(wareHouseResponse.getId()));
        return wareHouseResponse;
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

    public List<WareHouse> getAll(Boolean isActive, Long supplierId) {
        List<WareHouse> wareHouses = wareHouseRepository.findByIsActive(isActive, supplierId);
//        for (WareHouse request : wareHouses) {
            wareHouses.forEach(wareHouse -> {
                State stateExist = stateRepository.getOne(wareHouse.getStateId());
                wareHouse.setStateName(stateExist.getName());
                LGA lga = lgaRepository.getOne(wareHouse.getLgaId());
                wareHouse.setLgaName(lga.getName());
                wareHouse.setWareHouseUserCount(getWareHouseUsers(wareHouse.getId()));
                wareHouse.setProductCount(getProductCount(wareHouse.getId()));

            });
       // }

        return wareHouses;
    }


    public Integer getWareHouseUsers(Long wareHouseId){
        Integer userCount = wareHouseUserRepository.countAllByWareHouseId(wareHouseId);
        log.info("warehouse user count :::::::::::::::::::::::::::::::::::::: " + userCount);
        return userCount;

    }

    public Integer getProductCount(Long warehouseId){
        Integer productCount = wareHouseGoodRepository.countAllByWarehouseId(warehouseId);
        log.info("productCount ::::::::::::::::::::::::::::::::::::::::::::::::::: " + productCount);
        return productCount;

    }
}
