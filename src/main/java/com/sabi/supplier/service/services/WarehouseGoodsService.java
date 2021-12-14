package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.WarehouseGoodsRepository;
import com.sabi.suppliers.core.dto.request.WarehouseGoodsDto;
import com.sabi.suppliers.core.dto.response.WarehouseGoodsResponseDto;
import com.sabi.suppliers.core.models.WarehouseGoods;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WarehouseGoodsService {

    @Autowired
    private WarehouseGoodsRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Validations validations;

    /** <summary>
     * warehouse goods creation
     * </summary>
     * <remarks>this method is responsible for creation of new warehouse goods</remarks>
     */

    public WarehouseGoodsResponseDto createWarehouseGoods(WarehouseGoodsDto request) {
        validations.validateWarehouseGoods(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WarehouseGoods warehouseGoods = mapper.map(request,WarehouseGoods.class);
//        WarehouseGoods wareHouseGoodsExist = request.findByName(request.getName());
//        if(wareHouseGoodsExist !=null){
//            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Country already exist");
//        }
        warehouseGoods.setCreatedBy(userCurrent.getId());
        warehouseGoods.setIsActive(true);
        warehouseGoods = repository.save(warehouseGoods);
        log.debug("Create new Country - {}"+ new Gson().toJson(warehouseGoods));
        return mapper.map(warehouseGoods, WarehouseGoodsResponseDto.class);
    }



    /** <summary>
     * warehouse goods update
     * </summary>
     * <remarks>this method is responsible for updating already existing warehouse goods</remarks>
     */

    public WarehouseGoodsResponseDto updateWarehouseGoods(WarehouseGoodsDto request) {
        validations.validateWarehouseGoods(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WarehouseGoods country = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse goods Id does not exist!"));
        mapper.map(request, country);
        country.setUpdatedBy(userCurrent.getId());
        repository.save(country);
        log.debug("Country record updated - {}"+ new Gson().toJson(country));
        return mapper.map(country, WarehouseGoodsResponseDto.class);
    }




    /** <summary>
     * Find warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public WarehouseGoodsResponseDto findWarehouseGoods(Long id){
        WarehouseGoods country  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse goods Id does not exist!"));
        return mapper.map(country,WarehouseGoodsResponseDto.class);
    }



    /** <summary>
     * Find all warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<WarehouseGoods> findAll(Long warehouseId, Long supplyGoodId,Long supplierId, PageRequest pageRequest ){
        Page<WarehouseGoods> country = repository.findWarehouseGoods(warehouseId,supplyGoodId,supplierId,pageRequest);
        if(country == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return country;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a country</remarks>
     */
    public void enableDisEnableWarehouseGoods (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WarehouseGoods warehouseGoods = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested  warehouse goods Id does not exist!"));
        warehouseGoods.setIsActive(request.isActive());
        warehouseGoods.setUpdatedBy(userCurrent.getId());
        repository.save(warehouseGoods);

    }


    public List<WarehouseGoods> getAll(boolean isActive){
        List<WarehouseGoods> countries = repository.findByIsActive(isActive);
        return countries;

    }
}
