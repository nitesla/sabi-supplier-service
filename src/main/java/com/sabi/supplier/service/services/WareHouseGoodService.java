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
import com.sabi.supplier.service.repositories.SupplierGoodRepository;
import com.sabi.supplier.service.repositories.WareHouseGoodRepository;
import com.sabi.suppliers.core.dto.request.WareHouseGoodDto;
import com.sabi.suppliers.core.dto.response.WareHouseGoodResponseDto;
import com.sabi.suppliers.core.models.ProductVariant;
import com.sabi.suppliers.core.models.SupplierGood;
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
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private SupplierGoodRepository supplierGoodRepository;
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
        WareHouseGood wareHouseGoodsExist = repository.findBySupplierGoodIdAndWarehouseId(request.getSupplierGoodId(), request.getWarehouseId());
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
        WareHouseGood wareHouseGood  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested warehouse goods Id does not exist!"));
        SupplierGood supplierGood = supplierGoodRepository.findSupplierGoodById(wareHouseGood.getSupplierGoodId());
        ProductVariant productVariant = productVariantRepository.findProductVariantById(supplierGood.getVariantId());
        wareHouseGood.setVariantId(productVariant.getId());
        wareHouseGood.setVariantName(productVariant.getName());
        return mapper.map(wareHouseGood,WareHouseGoodResponseDto.class);
    }



    /** <summary>
     * Find all warehouse goods
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<WareHouseGood> findAll(Long warehouseId, Long supplierGoodId,Long supplierId, PageRequest pageRequest ){
        Page<WareHouseGood> warehouseGood = repository.findWarehouseGood(warehouseId,supplierGoodId,supplierId,pageRequest);
        if(warehouseGood == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        warehouseGood.forEach(wareHouseGood -> {
            SupplierGood supplierGood = supplierGoodRepository.findSupplierGoodById(wareHouseGood.getSupplierGoodId());
            ProductVariant productVariant = productVariantRepository.findProductVariantById(supplierGood.getVariantId());
            wareHouseGood.setVariantId(productVariant.getId());
            wareHouseGood.setVariantName(productVariant.getName());
        });
        return warehouseGood;

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
        List<WareHouseGood> wareHouseGoods = repository.findByIsActiveOrderByIdDesc(isActive);
        wareHouseGoods.forEach(wareHouseGood -> {
            SupplierGood supplierGood = supplierGoodRepository.findSupplierGoodById(wareHouseGood.getSupplierGoodId());
            ProductVariant productVariant = productVariantRepository.findProductVariantById(supplierGood.getVariantId());
            wareHouseGood.setVariantId(productVariant.getId());
            wareHouseGood.setVariantName(productVariant.getName());
        });
        return wareHouseGoods;

    }

//    public List<WareHouseGood> getAllByWareHouseId(Long warehouseId){
//        List<WareHouseGood> wareHouseGoods = repository.findByWarehouseIdOrderByIdDesc(warehouseId);
//        wareHouseGoods.forEach(wareHouseGood -> {
//            SupplierGood supplierGood = supplierGoodRepository.findSupplierGoodById(wareHouseGood.getSupplierGoodId());
//            ProductVariant productVariant = productVariantRepository.findProductVariantById(supplierGood.getVariantId());
//            wareHouseGood.setVariantId(productVariant.getId());
//            wareHouseGood.setVariantName(productVariant.getName());
//        });
//        return wareHouseGoods;
//
//    }
}
