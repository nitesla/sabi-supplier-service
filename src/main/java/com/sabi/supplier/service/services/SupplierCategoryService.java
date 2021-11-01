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
import com.sabi.supplier.service.repositories.SupplierCategoryRepository;
import com.sabisupplierscore.dto.request.SupplierCategoryDto;
import com.sabisupplierscore.dto.response.SupplierCategoryResponseDto;
import com.sabisupplierscore.models.SupplierCategory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierCategoryService {

    @Autowired
    private SupplierCategoryRepository supplierCategoryRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierCategoryService(SupplierCategoryRepository supplierCategoryRepository, ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.supplierCategoryRepository = supplierCategoryRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }


    /** <summary>
     * State creation
     * </summary>
     * <remarks>this method is responsible for creation of new supplier category</remarks>
     */

    public SupplierCategoryResponseDto createSupplierCategory(SupplierCategoryDto request) {
        validations.validateSupplierCategory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierCategory supplierCategory = mapper.map(request,SupplierCategory.class);
        SupplierCategory productExist = supplierCategoryRepository.findByName(request.getName());
        if(productExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " supplier category already exist");
        }
        supplierCategory.setCreatedBy(userCurrent.getId());
        supplierCategory.setIsActive(true);
        supplierCategory = supplierCategoryRepository.save(supplierCategory);
        log.debug("Create new supplier category - {}"+ new Gson().toJson(supplierCategory));
        return mapper.map(supplierCategory, SupplierCategoryResponseDto.class);
    }


    /** <summary>
     * State update
     * </summary>
     * <remarks>this method is responsible for updating already existing supplier category</remarks>
     */

    public SupplierCategoryResponseDto updateSupplierCategory(SupplierCategoryDto request) {
        validations.validateSupplierCategory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierCategory supplierCategory = supplierCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier category Id does not exist!"));
        mapper.map(request, supplierCategory);
        supplierCategory.setUpdatedBy(userCurrent.getId());
        supplierCategoryRepository.save(supplierCategory);
        log.debug("supplier category record updated - {}"+ new Gson().toJson(supplierCategory));
        return mapper.map(supplierCategory, SupplierCategoryResponseDto.class);
    }


    /** <summary>
     * Find State
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public SupplierCategoryResponseDto findSupplierCategory(Long id){
        SupplierCategory supplierCategory = supplierCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier category Id does not exist!"));
        return mapper.map(supplierCategory,SupplierCategoryResponseDto.class);
    }


    /** <summary>
     * Find all State
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<SupplierCategory> findAllSupplierCategory(String name, PageRequest pageRequest ){
        Page<SupplierCategory> products = supplierCategoryRepository.findSupplierCategories(name,pageRequest);
        if(products == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return products;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a supplier category</remarks>
     */
    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierCategory supplierCategory = supplierCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier category Id does not exist!"));
        supplierCategory.setIsActive(request.isActive());
        supplierCategory.setUpdatedBy(userCurrent.getId());
        supplierCategoryRepository.save(supplierCategory);

    }


    public List<SupplierCategory> getAll(Boolean isActive){
        List<SupplierCategory> products = supplierCategoryRepository.findByIsActive(isActive);
        return products;

    }
}
