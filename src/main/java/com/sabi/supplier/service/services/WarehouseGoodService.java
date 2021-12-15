package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.WarehouseGoodRepository;
import com.sabi.suppliers.core.dto.request.WarehouseGoodDto;
import com.sabi.suppliers.core.dto.response.WarehouseGoodResponseDto;
import com.sabi.suppliers.core.models.WarehouseGood;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WarehouseGoodService {

    @Autowired
    private WarehouseGoodRepository repository;
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

    public WarehouseGoodResponseDto createWarehouseGood(WarehouseGoodDto request) {
        validations.validateWarehouseGood(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WarehouseGood warehouseGood = mapper.map(request,WarehouseGood.class);
//        WarehouseGood wareHouseGoodsExist = request.findByName(request.getName());
//        if(wareHouseGoodsExist !=null){
//            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Country already exist");
//        }
        warehouseGood.setCreatedBy(userCurrent.getId());
        warehouseGood.setIsActive(true);
        warehouseGood = repository.save(warehouseGood);
        log.debug("Create new Country - {}"+ new Gson().toJson(warehouseGood));
        return mapper.map(warehouseGood, WarehouseGoodResponseDto.class);
    }



    /** <summary>
     * warehouse goods update
     * </summary>
     * <remarks>this method is responsible for updating already existing warehouse goods</remarks>
     */

    public WarehouseGoodResponseDto updateWarehouseGood(WarehouseGoodDto request) {
        validations.validateWarehouseGood(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WarehouseGood country = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse goods Id does not exist!"));
        mapper.map(request, country);
        country.setUpdatedBy(userCurrent.getId());
        repository.save(country);
        log.debug("Country record updated - {}"+ new Gson().toJson(country));
        return mapper.map(country, WarehouseGoodResponseDto.class);
    }




    /** <summary>
     * Find warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public WarehouseGoodResponseDto findWarehouseGood(Long id){
        WarehouseGood country  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse goods Id does not exist!"));
        return mapper.map(country,WarehouseGoodResponseDto.class);
    }



    /** <summary>
     * Find all warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<WarehouseGood> findAll(Long warehouseId, Long supplierGoodId,Long supplierId, PageRequest pageRequest ){
        Page<WarehouseGood> country = repository.findWarehouseGood(warehouseId,supplierGoodId,supplierId,pageRequest);
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
        WarehouseGood warehouseGood = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested  warehouse goods Id does not exist!"));
        warehouseGood.setIsActive(request.isActive());
        warehouseGood.setUpdatedBy(userCurrent.getId());
        repository.save(warehouseGood);

    }


    public List<WarehouseGood> getAll(boolean isActive){
        List<WarehouseGood> countries = repository.findByIsActive(isActive);
        return countries;

    }
}
