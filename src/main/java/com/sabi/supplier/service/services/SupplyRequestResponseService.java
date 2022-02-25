package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.SupplyRequestResponseRepository;
import com.sabi.suppliers.core.dto.request.SupplyRequestResponseRequest;
import com.sabi.suppliers.core.models.SupplyRequestResponseEntity;
import com.sabi.suppliers.core.models.response.SupplyRequestResponseResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SupplyRequestResponseService {
    private final SupplyRequestResponseRepository supplyRequestResponseRepository;

    private final Validations validations;
    private final ModelMapper mapper;

    public SupplyRequestResponseService(SupplyRequestResponseRepository supplyRequestResponseRepository, Validations validations, ModelMapper mapper) {
        this.supplyRequestResponseRepository = supplyRequestResponseRepository;
        this.validations = validations;
        this.mapper = mapper;
    }

    public SupplyRequestResponseResponse createSupplyRequestResponse(SupplyRequestResponseRequest request) {
        validations.validateSupplyRequestResponse(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequestResponseEntity supplyRequestResponse = mapper.map(request, SupplyRequestResponseEntity.class);
        boolean supplyRequestResponseExists = supplyRequestResponseRepository.existsBySupplyRequestId(request.getSupplyRequestId());
        if (supplyRequestResponseExists) {
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " supplyRequestResponse already exist");
        }
        supplyRequestResponse.setCreatedBy(userCurrent.getId());
        supplyRequestResponse.setIsActive(true);
        supplyRequestResponse = supplyRequestResponseRepository.save(supplyRequestResponse);
        log.debug("Create new State - {}" + new Gson().toJson(supplyRequestResponse));
        return mapper.map(supplyRequestResponse, SupplyRequestResponseResponse.class);
    }

    public SupplyRequestResponseResponse updateSupplyRequestResponse(SupplyRequestResponseRequest request) {
        validations.validateSupplyRequestResponse(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequestResponseEntity supplyRequestResponse = supplyRequestResponseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplyRequestResponse Id does not exist!"));
        mapper.map(request, supplyRequestResponse);
        supplyRequestResponse.setUpdatedBy(userCurrent.getId());
        supplyRequestResponseRepository.save(supplyRequestResponse);
        log.debug("State record updated - {}" + new Gson().toJson(supplyRequestResponse));
        return mapper.map(supplyRequestResponse, SupplyRequestResponseResponse.class);
    }

    public Page<SupplyRequestResponseEntity> findSupplyRequestResponses(Long supplyRequestId, PageRequest pageRequest) {
        Page<SupplyRequestResponseEntity> supplyRequestResponseEntities = supplyRequestResponseRepository.findSupplierRequestResponse(supplyRequestId, pageRequest);
        return supplyRequestResponseEntities;
    }

    public SupplyRequestResponseResponse findsupplyRequestResponse(long id){
        SupplyRequestResponseEntity supplyRequestResponse = supplyRequestResponseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supply Request Id does not exist!"));
        return mapper.map(supplyRequestResponse, SupplyRequestResponseResponse.class);
    }

    public void enableDisEnableState(EnableDisEnableDto request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequestResponseEntity supplyRequestResponse = supplyRequestResponseRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested State Id does not exist!"));
        supplyRequestResponse.setIsActive(request.isActive());
        supplyRequestResponse.setUpdatedBy(userCurrent.getId());
        supplyRequestResponseRepository.save(supplyRequestResponse);
    }

    public List<SupplyRequestResponseEntity> getAll(Boolean isActive) {
        return supplyRequestResponseRepository.findByIsActiveOrderByIdDesc(isActive);
    }


}
