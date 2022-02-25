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
import com.sabi.supplier.service.repositories.InventoryRepository;
import com.sabi.supplier.service.repositories.ProductVariantRepository;
import com.sabi.supplier.service.repositories.SupplierGoodRepository;
import com.sabi.supplier.service.repositories.WareHouseRepository;
import com.sabi.suppliers.core.dto.request.InventoryDto;
import com.sabi.suppliers.core.models.Inventory;
import com.sabi.suppliers.core.models.ProductVariant;
import com.sabi.suppliers.core.models.SupplierGood;
import com.sabi.suppliers.core.models.WareHouse;
import com.sabi.suppliers.core.models.response.InventoryResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InventoryService {

    @Autowired
    private InventoryRepository repository;
    @Autowired
    private WareHouseRepository wareHouseRepository;
    @Autowired
    private SupplierGoodRepository supplierGoodRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Validations validations;

    /** <summary>
     * Inventory creation
     * </summary>
     * <remarks>this method is responsible for creation of new Inventory</remarks>
     */

    public InventoryResponseDto createInventory(InventoryDto request) {
        validations.validateInventory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Inventory inventory = mapper.map(request,Inventory.class);
        Inventory inventoryExist = repository.findByName(request.getName());
        if(inventoryExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Inventory already exist");
        }
        WareHouse wareHouse = wareHouseRepository.findWareHouseById(request.getWarehouseId());
        SupplierGood supplierGood = supplierGoodRepository.findSupplierGoodById(request.getSupplierGoodId());
        ProductVariant productVariant = productVariantRepository.getOne(supplierGood.getVariantId());
        inventory.setCreatedBy(userCurrent.getId());
        inventory.setWareHouseAddress( wareHouse.getAddress());
        inventory.setProductVariant(productVariant.getName());
        inventory.setIsActive(true);
        inventory = repository.save(inventory);
        log.debug("Create new Country - {}"+ new Gson().toJson(inventory));
        return mapper.map(inventory, InventoryResponseDto.class);
    }



    /** <summary>
     * Inventory update
     * </summary>
     * <remarks>this method is responsible for updating already existing Inventory</remarks>
     */

    public InventoryResponseDto updateInventory(InventoryDto request) {
        validations.validateInventory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Inventory inventory = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested inventory Id does not exist!"));
        mapper.map(request, inventory);
        inventory.setUpdatedBy(userCurrent.getId());
        repository.save(inventory);
        log.debug("Inventory record updated - {}"+ new Gson().toJson(inventory));
        return mapper.map(inventory, InventoryResponseDto.class);
    }




    /** <summary>
     * Find Inventory
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public InventoryResponseDto findInventoryById(Long id){
        Inventory inventory  = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Inventory Id does not exist!"));
        return mapper.map(inventory,InventoryResponseDto.class);
    }



    /** <summary>
     * Find all Inventory
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<Inventory> findAll(Long supplierRequestId, Long warehouseId, PageRequest pageRequest ){
        Page<Inventory> country = repository.findInventories(supplierRequestId,warehouseId,pageRequest);
        if(country == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return country;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a Inventory</remarks>
     */
    public void enableDisEnableInventory(EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Inventory country = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Inventory Id does not exist!"));
        country.setIsActive(request.isActive());
        country.setUpdatedBy(userCurrent.getId());
        repository.save(country);

    }


    public List<Inventory> getAll(boolean isActive){
        List<Inventory> countries = repository.findByIsActiveOrderByIdDesc(isActive);
        return countries;
    }

    public List<Inventory> getInventoryiesByWarehouseId(Long warehouseId){
        List<Inventory> countries = repository.findInventoriesByWarehouseId(warehouseId);
        return countries;
    }
}
