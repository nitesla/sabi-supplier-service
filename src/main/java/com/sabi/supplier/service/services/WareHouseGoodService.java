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
import com.sabi.supplier.service.repositories.ProductVariantRepository;
import com.sabi.supplier.service.repositories.WareHouseGoodRepository;
import com.sabi.suppliers.core.dto.request.WareHouseGoodDto;
import com.sabi.suppliers.core.dto.response.WareHouseGoodResponseDto;
import com.sabi.suppliers.core.models.ProductVariant;
import com.sabi.suppliers.core.models.WareHouseGood;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WareHouseGoodService {

    @Autowired
    private WareHouseGoodRepository repository;
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

    public WareHouseGoodResponseDto createWarehouseGood(WareHouseGoodDto request) {
        validations.validateWarehouseGood(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouseGood warehouseGood = mapper.map(request,WareHouseGood.class);
        WareHouseGood wareHouseGoodsExist = repository.findBySupplierGoodId(request.getSupplierGoodId());
        if (wareHouseGoodsExist != null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    " Warehouse goods already exist");
        }
        warehouseGood.setCreatedBy(userCurrent.getId());
        warehouseGood.setIsActive(true);
        warehouseGood = repository.save(warehouseGood);
        log.debug("Create new warehouse goods - {}"+ new Gson().toJson(warehouseGood));
        return mapper.map(warehouseGood, WareHouseGoodResponseDto.class);
    }



    /** <summary>
     * warehouse goods update
     * </summary>
     * <remarks>this method is responsible for updating already existing warehouse goods</remarks>
     */

    public WareHouseGoodResponseDto updateWarehouseGood(WareHouseGoodDto request) {
        validations.validateWarehouseGood(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouseGood wareHouseGood = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse good Id does not exist!"));
        mapper.map(request, wareHouseGood);
        wareHouseGood.setUpdatedBy(userCurrent.getId());
        repository.save(wareHouseGood);
        log.debug("warehouse goods record updated - {}"+ new Gson().toJson(wareHouseGood));
        return mapper.map(wareHouseGood, WareHouseGoodResponseDto.class);
    }




    /** <summary>
     * Find warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public WareHouseGoodResponseDto findWarehouseGood(Long id){
        WareHouseGood country  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse goods Id does not exist!"));
        return mapper.map(country,WareHouseGoodResponseDto.class);
    }



    /** <summary>
     * Find all warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<WareHouseGood> findAll(Long warehouseId, Long supplierGoodId,Long supplierId, PageRequest pageRequest ){
        Page<WareHouseGood> country = repository.findWarehouseGood(warehouseId,supplierGoodId,supplierId,pageRequest);
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
        WareHouseGood warehouseGood = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested  warehouse goods Id does not exist!"));
        warehouseGood.setIsActive(request.isActive());
        warehouseGood.setUpdatedBy(userCurrent.getId());
        repository.save(warehouseGood);

    }


    public List<WareHouseGood> getAll(boolean isActive){
        List<WareHouseGood> countries = repository.findByIsActiveOrderByIdDesc(isActive);
        return countries;

    }
}
