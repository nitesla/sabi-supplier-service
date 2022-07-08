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
import com.sabi.supplier.service.repositories.StateRepository;
import com.sabi.supplier.service.repositories.SupplierLocationRepository;
import com.sabi.suppliers.core.dto.request.SupplierLocationRequestDto;
import com.sabi.suppliers.core.dto.response.SupplierLocationResponseDto;
import com.sabi.suppliers.core.models.State;
import com.sabi.suppliers.core.models.SupplierLocation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 * This class is responsible for all business logic for Supplier Location
 */


@Slf4j
@Service
public class SupplierLocationService {



    @Autowired
    private StateRepository stateRepository;

    private SupplierLocationRepository supplierLocationRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierLocationService(SupplierLocationRepository supplierLocationRepository, ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.supplierLocationRepository = supplierLocationRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;

    }

    /** <summary>
      * Supplier Location creation
      * </summary>
      * <remarks>this method is responsible for creation of new Supplier Locations</remarks>
      */

    public SupplierLocationResponseDto createSupplierLocation(SupplierLocationRequestDto request) {
        validations.validateSupplierLocation(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierLocation supplierLocation = mapper.map(request,SupplierLocation.class);
        SupplierLocation supplierLocationExist = supplierLocationRepository.findBySupplierIdAndStateId(request.getSupplierID(), request.getStateID());
        if(supplierLocationExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier Location already exist");
        }
        supplierLocation.setCreatedBy(userCurrent.getId());
        supplierLocation.setIsActive(true);
        supplierLocation = supplierLocationRepository.save(supplierLocation);
        log.debug("Create new Supplier Location - {}"+ new Gson().toJson(supplierLocation));
        return mapper.map(supplierLocation, SupplierLocationResponseDto.class);
    }


    /** <summary>
     * Supplier Location update
     * </summary>
     * <remarks>this method is responsible for updating already existing Supplier Locations</remarks>
     */

    public SupplierLocationResponseDto updateSupplierLocation(SupplierLocationRequestDto request) {
        validations.validateSupplierLocation(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierLocation supplierLocation = supplierLocationRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Location Id does not exist!"));
        mapper.map(request, supplierLocation);
        supplierLocation.setUpdatedBy(userCurrent.getId());
        supplierLocationRepository.save(supplierLocation);
        log.debug("Supplier Location record updated - {}"+ new Gson().toJson(supplierLocation));
        return mapper.map(supplierLocation, SupplierLocationResponseDto.class);
    }


    /** <summary>
     * Find Supplier Location
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public SupplierLocationResponseDto findSupplierLocation(Long id){
        SupplierLocation supplierLocation = supplierLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier location Id does not exist!"));
        State savedState = stateRepository.findStateById(supplierLocation.getStateId());
        supplierLocation.setStateName(savedState.getName());
        return mapper.map(supplierLocation,SupplierLocationResponseDto.class);
    }

    public Page<SupplierLocation> findAll(Long supplierId, Long stateId, String stateName,PageRequest pageRequest) {
        Page<SupplierLocation> supplierLocations = supplierLocationRepository.findSupplierLocation(supplierId, stateId,stateName, pageRequest);
        if (supplierLocations == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        supplierLocations.forEach(supplierLocation->{
            State savedState = stateRepository.findStateById(supplierLocation.getStateId());
            supplierLocation.setStateName(savedState.getName());
        });

        return supplierLocations;
    }




    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a supplier location</remarks>
     */
    public void enableDisable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierLocation supplierLocation = supplierLocationRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier location Id does not exist!"));
        supplierLocation.setIsActive(request.getIsActive());
        supplierLocation.setUpdatedBy(userCurrent.getId());
        supplierLocationRepository.save(supplierLocation);

    }


    public List<SupplierLocation> getAll(Boolean isActive){
        List<SupplierLocation> supplierLocations = supplierLocationRepository.findByIsActiveOrderByIdDesc(isActive);
        return supplierLocations;

    }


}
