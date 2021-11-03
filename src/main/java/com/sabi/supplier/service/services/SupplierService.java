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
import com.sabi.supplier.service.repositories.SupplierRepository;
import com.sabi.suppliers.core.dto.request.SupplierRequestDto;
import com.sabi.suppliers.core.dto.response.SupplierResponseDto;
import com.sabi.suppliers.core.models.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * This class is responsible for all business logic for Supplier
 */


@Slf4j
@Service
public class SupplierService {



    private SupplierRepository supplierRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierService(SupplierRepository supplierRepository, ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.supplierRepository = supplierRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;

    }

    /** <summary>
      * Supplier creation
      * </summary>
      * <remarks>this method is responsible for creation of new Suppliers</remarks>
      */

    public SupplierResponseDto createSupplier(SupplierRequestDto request) {
        validations.validateSupplier(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Supplier supplier = mapper.map(request,Supplier.class);
        Supplier supplierExist = supplierRepository.findByName(request.getName());
        if(supplierExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier already exist");
        }
        supplier.setCreatedBy(userCurrent.getId());
        supplier.setIsActive(true);
        supplier = supplierRepository.save(supplier);
        log.debug("Create new Supplier - {}"+ new Gson().toJson(supplier));
        return mapper.map(supplier, SupplierResponseDto.class);
    }


    /** <summary>
     * Supplier update
     * </summary>
     * <remarks>this method is responsible for updating already existing Supplier</remarks>
     */

    public SupplierResponseDto updateSupplier(SupplierRequestDto request) {
        validations.validateSupplier(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Supplier supplier = supplierRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!"));
        mapper.map(request, supplier);
        supplier.setUpdatedBy(userCurrent.getId());
        supplierRepository.save(supplier);
        log.debug("Supplier record updated - {}"+ new Gson().toJson(supplier));
        return mapper.map(supplier, SupplierResponseDto.class);
    }


    /** <summary>
     * Find Supplier
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public SupplierResponseDto findSupplier(Long id){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!"));
        return mapper.map(supplier,SupplierResponseDto.class);
    }


    /** <summary>
     * Find all Supplier
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<Supplier> findAll(String name, Long stateID, String address, String phone, String email,
                                  String website, Long supplierCategoryID, String contactPerson, String contactPhone,
                                  String contactEmail, Double discountProvided,  PageRequest pageRequest ){

        GenericSpecification<Supplier> genericSpecification = new GenericSpecification<Supplier>();

        if (name != null && !name.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("name", name, SearchOperation.MATCH));
        }

        if (stateID != null)
        {
            genericSpecification.add(new SearchCriteria("stateID", stateID, SearchOperation.EQUAL));
        }

        if (address != null && !address.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("address", address, SearchOperation.MATCH));
        }

        if (phone != null && !phone.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("phone", phone, SearchOperation.MATCH));
        }

        if (email != null && !email.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("email", email, SearchOperation.MATCH));
        }

        if (website != null && !website.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("website", website, SearchOperation.MATCH));
        }

        if (supplierCategoryID != null)
        {
            genericSpecification.add(new SearchCriteria("supplierCategoryID", supplierCategoryID, SearchOperation.EQUAL));
        }

        if (contactPerson != null && !contactPerson.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("contactPerson", contactPerson, SearchOperation.MATCH));
        }

        if (contactPhone != null && !contactPhone.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("contactPhone", contactPhone, SearchOperation.MATCH));
        }

        if (contactEmail != null && !contactEmail.isEmpty())
        {
            genericSpecification.add(new SearchCriteria("contactEmail", contactEmail, SearchOperation.MATCH));
        }

        if (discountProvided != null)
        {
            genericSpecification.add(new SearchCriteria("discountProvided", discountProvided, SearchOperation.EQUAL));
        }

        Page<Supplier> supplier = supplierRepository.findAll(genericSpecification,pageRequest);
            if(supplier == null){
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
            }
        return supplier;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a Supplier</remarks>
     */
    public void enableDisable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Supplier supplier = supplierRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!"));
        supplier.setIsActive(request.isActive());
        supplier.setUpdatedBy(userCurrent.getId());
        supplierRepository.save(supplier);

    }


    public List<Supplier> getAll(Boolean isActive){
        List<Supplier> suppliers = supplierRepository.findByIsActive(isActive);
        return suppliers;

    }


}
