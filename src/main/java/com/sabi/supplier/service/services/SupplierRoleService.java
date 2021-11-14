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
import com.sabi.supplier.service.repositories.SupplierRoleRespository;
import com.sabi.suppliers.core.dto.request.SupplierRequestDto;
import com.sabi.suppliers.core.dto.request.SupplierRoleDto;
import com.sabi.suppliers.core.dto.response.SupplierResponseDto;
import com.sabi.suppliers.core.dto.response.SupplierRoleResponseDto;
import com.sabi.suppliers.core.models.SupplierRole;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierRoleService {

    @Autowired
    private SupplierRoleRespository supplierRoleRespository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierRoleService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    public SupplierRoleResponseDto createSupplierRole(SupplierRoleDto request) {
        validations.validateSupplierRole(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierRole supplierRole = mapper.map(request,SupplierRole.class);
        SupplierRole supplierProductExist = supplierRoleRespository.findSupplierUSerById(request.getId());
        if(supplierProductExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier role already exist");
        }
        supplierRole.setCreatedBy(userCurrent.getId());
        supplierRole.setIsActive(true);
        supplierRole = supplierRoleRespository.save(supplierRole);
        log.debug("Create new Supplier - {}"+ new Gson().toJson(supplierRole));
        return mapper.map(supplierRole, SupplierRoleResponseDto.class);
    }

    public SupplierRoleResponseDto updateSupplierRole(SupplierRoleDto request) {
        validations.validateSupplierRole(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierRole supplierRole = supplierRoleRespository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier product Id does not exist!"));
        mapper.map(request, supplierRole);
        supplierRole.setUpdatedBy(userCurrent.getId());
        supplierRoleRespository.save(supplierRole);
        log.debug("Supplier record updated - {}"+ new Gson().toJson(supplierRole));
        return mapper.map(supplierRole, SupplierRoleResponseDto.class);
    }

    public SupplierRoleResponseDto findSupplierRole(Long id){
        SupplierRole supplier = supplierRoleRespository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier product Id does not exist!"));
        return mapper.map(supplier,SupplierRoleResponseDto.class);
    }

    public Page<SupplierRole> findAll(String name, Long partnerId, Long roleId, PageRequest pageRequest ){
        Page<SupplierRole> state = supplierRoleRespository.findSupplierUser(name,partnerId,roleId,pageRequest);
        if(state == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return state;
    }

    public void enableDisEnable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierRole product = supplierRoleRespository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier product Id does not exist!"));
        product.setIsActive(request.isActive());
        product.setUpdatedBy(userCurrent.getId());
        supplierRoleRespository.save(product);

    }


    public List<SupplierRole> getAll(Boolean isActive){
        List<SupplierRole> states = supplierRoleRespository.findByIsActive(isActive);
        return states;

    }
}
