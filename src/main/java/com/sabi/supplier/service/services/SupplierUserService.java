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
import com.sabi.supplier.service.repositories.SupplierUserRepository;
import com.sabi.suppliers.core.dto.request.SupplierUserDto;
import com.sabi.suppliers.core.dto.response.SupplierUserResponseDto;
import com.sabi.suppliers.core.models.SupplierUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierUserService {

    @Autowired
    private SupplierUserRepository supplierUserRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierUserService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    public SupplierUserResponseDto createSupplierUser(SupplierUserDto request) {
        validations.validateSupplierUser(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierUser supplierProduct = mapper.map(request,SupplierUser.class);
        SupplierUser supplierProductExist = supplierUserRepository.findSupplierUSerById(request.getId());
        if(supplierProductExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier product already exist");
        }
        supplierProduct.setCreatedBy(userCurrent.getId());
        supplierProduct.setIsActive(true);
        supplierProduct = supplierUserRepository.save(supplierProduct);
        log.debug("Create new Supplier - {}"+ new Gson().toJson(supplierProduct));
        return mapper.map(supplierProduct, SupplierUserResponseDto.class);
    }

    public SupplierUserResponseDto updateSupplierUser(SupplierUserDto request) {
        validations.validateSupplierUser(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierUser supplierProduct = supplierUserRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier product Id does not exist!"));
        mapper.map(request, supplierProduct);
        supplierProduct.setUpdatedBy(userCurrent.getId());
        supplierUserRepository.save(supplierProduct);
        log.debug("Supplier record updated - {}"+ new Gson().toJson(supplierProduct));
        return mapper.map(supplierProduct, SupplierUserResponseDto.class);
    }

    public SupplierUserResponseDto findSupplierUser(Long id){
        SupplierUser supplier = supplierUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier product Id does not exist!"));
        return mapper.map(supplier,SupplierUserResponseDto.class);
    }

    public Page<SupplierUser> findAll(Long userId, Long wareHouseId,Long roleId, PageRequest pageRequest ){
        Page<SupplierUser> state = supplierUserRepository.findSupplierUser(userId,wareHouseId,roleId,pageRequest);
        if(state == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return state;
    }

    public void enableDisEnable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierUser product = supplierUserRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier product Id does not exist!"));
        product.setIsActive(request.isActive());
        product.setUpdatedBy(userCurrent.getId());
        supplierUserRepository.save(product);

    }


    public List<SupplierUser> getAll(Boolean isActive){
        List<SupplierUser> states = supplierUserRepository.findByIsActive(isActive);
        return states;

    }
}
