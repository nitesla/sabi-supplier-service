package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.sabi.supplier.service.repositories.SupplierCategoryRepository;
import com.sabi.suppliers.core.dto.request.SupplierCategoryRequestDto;
import com.sabi.suppliers.core.dto.response.SupplierCategoryResponseDto;
import com.sabi.suppliers.core.models.SupplierCategory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * This class is responsible for all business logic for Supplier Category
 */


@Slf4j
@Service
public class SupplierCategoryService {



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
      * Supplier Category creation
      * </summary>
      * <remarks>this method is responsible for creation of new Supplier categories</remarks>
      */

    public SupplierCategoryResponseDto createSupplierCategory(SupplierCategoryRequestDto request) {
        validations.validateSupplierCategory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierCategory supplierCategory = mapper.map(request,SupplierCategory.class);
        SupplierCategory supplierCategoryExist = supplierCategoryRepository.findByName(request.getName());
        if(supplierCategoryExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier Category already exist");
        }
        supplierCategory.setCreatedBy(userCurrent.getId());
        supplierCategory.setIsActive(true);
        supplierCategory = supplierCategoryRepository.save(supplierCategory);
        log.debug("Create new Supplier Category - {}"+ new Gson().toJson(supplierCategory));
        return mapper.map(supplierCategory, SupplierCategoryResponseDto.class);
    }


    /** <summary>
     * Supplier Category update
     * </summary>
     * <remarks>this method is responsible for updating already existing Supplier Categories</remarks>
     */

    public SupplierCategoryResponseDto updateSupplierCategory(SupplierCategoryRequestDto request) {
        validations.validateSupplierCategory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierCategory supplierCategory = supplierCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Category Id does not exist!"));
        mapper.map(request, supplierCategory);
        supplierCategory.setUpdatedBy(userCurrent.getId());
        supplierCategoryRepository.save(supplierCategory);
        log.debug("Supplier Category record updated - {}"+ new Gson().toJson(supplierCategory));
        return mapper.map(supplierCategory, SupplierCategoryResponseDto.class);
    }


    /** <summary>
     * Find Supplier Category
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public SupplierCategoryResponseDto findSupplierCategory(Long id){
        SupplierCategory supplierCategory = supplierCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier Category Id does not exist!"));
        return mapper.map(supplierCategory,SupplierCategoryResponseDto.class);
    }


    /** <summary>
     * Find all Supplier Category
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<SupplierCategory> findAll(String name, Integer creditPeriod, Boolean isActive, PageRequest pageRequest ){

        GenericSpecification<SupplierCategory> genericSpecification = new GenericSpecification<SupplierCategory>();

        if (name != null && !name.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("name", name, SearchOperation.MATCH));
        }

        if (creditPeriod != null)
        {
            genericSpecification.add(new SearchCriteria("creditPeriod", creditPeriod, SearchOperation.EQUAL));
        }

        if (isActive != null)
        {
            genericSpecification.add(new SearchCriteria("isActive", isActive, SearchOperation.EQUAL));
        }



        Page<SupplierCategory> supplierCategory = supplierCategoryRepository.findAll(genericSpecification,pageRequest);
            if(supplierCategory == null){
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
            }
        return supplierCategory;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a supplier Category</remarks>
     */
    public void enableDisable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierCategory supplierCategory = supplierCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier Category Id does not exist!"));
        supplierCategory.setIsActive(request.isActive());
        supplierCategory.setUpdatedBy(userCurrent.getId());
        supplierCategoryRepository.save(supplierCategory);

    }


    public List<SupplierCategory> getAll(Boolean isActive){
        List<SupplierCategory> supplierCategories = supplierCategoryRepository.findByIsActiveOrderByIdDesc(isActive);
        return supplierCategories;

    }


}
