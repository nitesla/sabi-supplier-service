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
import com.sabi.supplier.service.repositories.ManufacturerRepository;
import com.sabi.suppliers.core.dto.request.ManufacturerDto;
import com.sabi.suppliers.core.dto.response.ManufacturerResponseDto;
import com.sabi.suppliers.core.models.Manufacturer;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ManufacturerService {

    private ManufacturerRepository manufacturerRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public ManufacturerService(ManufacturerRepository manufacturerRepository, ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.manufacturerRepository = manufacturerRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    /** <summary>
     * State creation
     * </summary>
     * <remarks>this method is responsible for creation of new product</remarks>
     */

    public ManufacturerResponseDto createManufacturer(ManufacturerDto request) {
        validations.validateManufacturer(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Manufacturer manufacturer = mapper.map(request,Manufacturer.class);
        Manufacturer manufacturerExist = manufacturerRepository.findByName(request.getName());
        if(manufacturerExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " product already exist");
        }
        manufacturer.setCreatedBy(userCurrent.getId());
        manufacturer.setIsActive(true);
        manufacturer = manufacturerRepository.save(manufacturer);
        log.debug("Create new manufacturer - {}"+ new Gson().toJson(manufacturer));
        return mapper.map(manufacturer, ManufacturerResponseDto.class);
    }


    /** <summary>
     * State update
     * </summary>
     * <remarks>this method is responsible for updating already existing product</remarks>
     */

    public ManufacturerResponseDto updateManufacturer(ManufacturerDto request) {
        validations.validateManufacturer(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Manufacturer product = manufacturerRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested manufacturer Id does not exist!"));
        mapper.map(request, product);
        product.setUpdatedBy(userCurrent.getId());
        manufacturerRepository.save(product);
        log.debug("product record updated - {}"+ new Gson().toJson(product));
        return mapper.map(product, ManufacturerResponseDto.class);
    }


    /** <summary>
     * Find State
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public ManufacturerResponseDto findManufacturer(Long id){
        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested manufacturer Id does not exist!"));
        return mapper.map(manufacturer,ManufacturerResponseDto.class);
    }


    /** <summary>
     * Find all State
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<Manufacturer> findAllManufacturer(String name, PageRequest pageRequest ){
        Page<Manufacturer> manufacturers = manufacturerRepository.findManufacturers(name,pageRequest);
        if(manufacturers == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return manufacturers;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a product</remarks>
     */
    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Manufacturer manufacturer = manufacturerRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested manufacturer Id does not exist!"));
        manufacturer.setIsActive(request.isActive());
        manufacturer.setUpdatedBy(userCurrent.getId());
        manufacturerRepository.save(manufacturer);

    }


    public List<Manufacturer> getAll(Boolean isActive){
        List<Manufacturer> manufacturers = manufacturerRepository.findByIsActive(isActive);
        return manufacturers;

    }
}
