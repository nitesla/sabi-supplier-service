package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.RoleDto;
import com.sabi.framework.dto.responseDto.RoleResponseDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.helpers.CoreValidations;
import com.sabi.framework.models.Role;
import com.sabi.framework.models.User;
import com.sabi.framework.repositories.RoleRepository;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.SupplierRoleRespository;
import com.sabi.supplier.service.repositories.SupplierUserRepository;
import com.sabi.suppliers.core.models.SupplierRole;
import com.sabi.suppliers.core.models.SupplierUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierRoleService {


    private SupplierRoleRespository supplierRoleRespository;
    private RoleRepository roleRepository;
    private SupplierUserRepository supplierUserRepository;
    private final CoreValidations coreValidations;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierRoleService(SupplierRoleRespository supplierRoleRespository,RoleRepository roleRepository,
                               SupplierUserRepository supplierUserRepository,CoreValidations coreValidations,
                               ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.supplierRoleRespository = supplierRoleRespository;
        this.roleRepository = roleRepository;
        this.supplierUserRepository = supplierUserRepository;
        this.coreValidations = coreValidations;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }





    public RoleResponseDto createSupplierRole(RoleDto request) {
        coreValidations.validateRole(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Role role = mapper.map(request,Role.class);
        SupplierUser supplierUser = supplierUserRepository.findByUserId(userCurrent.getId());
        Role roleExist = roleRepository.findByNameAndClientId(request.getName(),supplierUser.getSupplierId());
        if(roleExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Role already exist");
        }

        role.setClientId(supplierUser.getSupplierId());
        role.setCreatedBy(userCurrent.getId());
        role.setIsActive(true);
        role = roleRepository.save(role);
        log.debug("Create new role - {}"+ new Gson().toJson(role));

        SupplierRole supplierRole = SupplierRole.builder()
                .supplierId(role.getClientId())
                .roleId(role.getId())
                .build();
        supplierRoleRespository.save(supplierRole);

        return mapper.map(role, RoleResponseDto.class);
    }




    public Page<Role> findByClientId(String name, Boolean isActive, PageRequest pageRequest ){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        SupplierUser supplier = supplierUserRepository.findByUserId(userCurrent.getId());
        Page<Role> roles = roleRepository.findRolesByClientId(name,supplier.getSupplierId(),isActive,pageRequest);
        if(roles == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return roles;

    }



    public List<Role> getAll(Boolean isActive){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        SupplierUser supplier = supplierUserRepository.findByUserId(userCurrent.getId());
        List<Role> roles = roleRepository.findByIsActiveAndClientId(isActive,supplier.getSupplierId());
        return roles;

    }


}
