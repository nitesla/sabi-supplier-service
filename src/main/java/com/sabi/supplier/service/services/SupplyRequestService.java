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
import com.sabi.supplier.service.repositories.SupplyRequestRepository;
import com.sabi.suppliers.core.dto.request.SupplyRequestRequest;
import com.sabi.suppliers.core.dto.response.SupplyRequestResponse;
import com.sabi.suppliers.core.models.SupplyRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SupplyRequestService {

    private final SupplyRequestRepository supplyRequestRepository;

    private final Validations validations;
    private final ModelMapper mapper;

    public SupplyRequestService(SupplyRequestRepository supplyRequestRepository, Validations validations, ModelMapper mapper) {
        this.supplyRequestRepository = supplyRequestRepository;
        this.validations = validations;
        this.mapper = mapper;
    }

    public SupplyRequestResponse createSupplyRequest(SupplyRequestRequest request) {
        validations.validateSupplyRequest(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequest supplyRequest = mapper.map(request, SupplyRequest.class);
        boolean supplyRequestExists = supplyRequestRepository.existsByReferenceNo(request.getReferenceNo());
        if (supplyRequestExists) {
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " SupplyRequest already exist");
        }
        supplyRequest.setCreatedBy(userCurrent.getId());
        supplyRequest.setIsActive(false);
        supplyRequest = supplyRequestRepository.save(supplyRequest);
        log.debug("Create new State - {}" + new Gson().toJson(supplyRequest));
        return mapper.map(supplyRequest, SupplyRequestResponse.class);
    }

    public SupplyRequestResponse updateSupplyRequest(SupplyRequestRequest request) {
        validations.validateSupplyRequest(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequest supplyRequest = supplyRequestRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested SupplyRequest Id does not exist!"));
        mapper.map(request, supplyRequest);
        supplyRequest.setUpdatedBy(userCurrent.getId());
        supplyRequestRepository.save(supplyRequest);
        log.debug("State record updated - {}" + new Gson().toJson(supplyRequest));
        return mapper.map(supplyRequest, SupplyRequestResponse.class);
    }

//    public Page<SupplyRequest> findSupplyRequests(Long productId, String productName, Long askingQuantity, BigDecimal askingPrice,
//                                                 Date startTime, Date endTime, String referenceNo,
//                                                 String status, PageRequest pageRequest) {
//        GenericSpecification<SupplyRequest> genericSpecification = new GenericSpecification<>();
//
//        if (productId != null) {
//            genericSpecification.add(new SearchCriteria("productId", productId, SearchOperation.EQUAL));
//        }
//
//        if (productName != null && !productName.isEmpty()) {
//            genericSpecification.add(new SearchCriteria("productName", productName, SearchOperation.MATCH));
//        }
//        if (askingQuantity != null) {
//            genericSpecification.add(new SearchCriteria("askingQuantity", askingQuantity, SearchOperation.EQUAL));
//        }
//        if (askingPrice != null) {
//            genericSpecification.add(new SearchCriteria("askingPrice", askingPrice, SearchOperation.EQUAL));
//        }
//        if (startTime != null) {
//            genericSpecification.add(new SearchCriteria("startTime", startTime, SearchOperation.EQUAL));
//        }
//        if (endTime != null) {
//            genericSpecification.add(new SearchCriteria("endTime", endTime, SearchOperation.EQUAL));
//        }
//        if (referenceNo != null && !referenceNo.isEmpty()) {
//            genericSpecification.add(new SearchCriteria("referenceNo", referenceNo, SearchOperation.MATCH));
//        }
//        if (status != null && !status.isEmpty()) {
//            genericSpecification.add(new SearchCriteria("status", status, SearchOperation.EQUAL));
//        }
//
//        Page<SupplyRequest> supplyRequests = supplyRequestRepository.findAll(genericSpecification, pageRequest);
//
//        return supplyRequests;
//    }
//
    public SupplyRequestResponse findSupplyRequest(long id){
        SupplyRequest supplyRequest = supplyRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supply Request Id does not exist!"));
        return mapper.map(supplyRequest, SupplyRequestResponse.class);
    }

    public Page<SupplyRequest> findAll(Long productId, String productName, Long askingQuantity, BigDecimal askingPrice, Date startTime, Date endTime,String referenceNo,String status,Long warehouseId,Long supplierId,PageRequest pageRequest ){
        Page<SupplyRequest> stocks = supplyRequestRepository.findSupplyRequests(productId,productName,askingQuantity,askingPrice,startTime,endTime,referenceNo,status,warehouseId,supplierId,pageRequest);
        if(stocks == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return stocks;

    }

    public void enableDisEnableState(EnableDisEnableDto request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequest supplyRequest = supplyRequestRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested State Id does not exist!"));
        supplyRequest.setIsActive(request.isActive());
        supplyRequest.setUpdatedBy(userCurrent.getId());
        supplyRequestRepository.save(supplyRequest);
    }

    public List<SupplyRequest> getAll(Boolean isActive) {
        return supplyRequestRepository.findByIsActive(isActive);
    }
}
