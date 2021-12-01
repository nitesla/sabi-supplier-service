package com.sabi.supplier.service.services;

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
import com.sabi.supplier.service.repositories.WareHouseUserRepository;
import com.sabi.suppliers.core.dto.request.WareHouseUserRequest;
import com.sabi.suppliers.core.dto.response.WareHouseUserResponse;
import com.sabi.suppliers.core.models.WareHouse;
import com.sabi.suppliers.core.models.WareHouseUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WareHouseUserService {

    private final WareHouseUserRepository wareHouseUserRepository;

    private final Validations validations;
    private final ModelMapper mapper;

    public WareHouseUserService(WareHouseUserRepository wareHouseUserRepository, Validations validations, ModelMapper mapper) {
        this.wareHouseUserRepository = wareHouseUserRepository;
        this.validations = validations;
        this.mapper = mapper;
    }

    public WareHouseUserResponse createWareHouseUser(WareHouseUserRequest request){
        validations.validateWareHouseUser( request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouseUser wareHouseUser = mapper.map(request, WareHouseUser.class);
        boolean wareHouseUserExists = wareHouseUserRepository.existsByUserId(request.getUserId());
        if(wareHouseUserExists ){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " WareHouseUser already exist");
        }
        wareHouseUser.setCreatedBy(userCurrent.getId());
        wareHouseUser.setIsActive(false);
        wareHouseUser = wareHouseUserRepository.save(wareHouseUser);
        log.debug("Create new State - {}"+ new Gson().toJson(wareHouseUser));
        return mapper.map(wareHouseUser, WareHouseUserResponse.class);
    }

    public WareHouseUserResponse updateWareHouseUser(WareHouseUserRequest request){
        validations.validateWareHouseUser( request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouseUser wareHouseUser = wareHouseUserRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested WareHouseUser Id does not exist!"));
        mapper.map(request, wareHouseUser);
        wareHouseUser.setUpdatedBy(userCurrent.getId());
        wareHouseUserRepository.save(wareHouseUser);
        log.debug("State record updated - {}"+ new Gson().toJson(wareHouseUser));
        return mapper.map(wareHouseUser, WareHouseUserResponse.class);
    }

    public Page<WareHouseUser> findWareHouseUsers(Long userId, PageRequest pageRequest){
        GenericSpecification<WareHouseUser> genericSpecification = new GenericSpecification<>();
        if(userId != null) genericSpecification.add(new SearchCriteria("userId", userId, SearchOperation.EQUAL));
        Page<WareHouseUser> wareHouseUser = wareHouseUserRepository.findAll(genericSpecification, pageRequest);
        return wareHouseUser;
    }

    public WareHouseUserResponse findWareHouseUser(long id){
        WareHouseUser wareHouseUser = wareHouseUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested wareHouseUser Id does not exist!"));
        return mapper.map(wareHouseUser, WareHouseUserResponse.class);
    }

    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        WareHouseUser wareHouseUser = wareHouseUserRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested State Id does not exist!"));
        wareHouseUser.setIsActive(request.isActive());
        wareHouseUser.setUpdatedBy(userCurrent.getId());
        wareHouseUserRepository.save(wareHouseUser);
    }

    public List<WareHouseUser> getAll(Boolean isActive){
        List<WareHouseUser> wareHouseUsers = wareHouseUserRepository.findByIsActive(isActive);
        return wareHouseUsers;
    }
}
