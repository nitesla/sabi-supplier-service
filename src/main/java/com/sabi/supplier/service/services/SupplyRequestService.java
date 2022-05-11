package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.ProductRepository;
import com.sabi.supplier.service.repositories.SupplyRequestRepository;
import com.sabi.supplier.service.repositories.SupplyRequestResponseRepository;
import com.sabi.suppliers.core.dto.request.SupplyRequestRequest;
import com.sabi.suppliers.core.dto.request.SupplyRequestResponseRequest;
import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.SupplyRequest;
import com.sabi.suppliers.core.models.SupplyRequestResponseEntity;
import com.sabi.suppliers.core.models.response.SupplyRequestResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@SuppressWarnings("All")
@Service
@Slf4j
public class SupplyRequestService {

    private final SupplyRequestRepository supplyRequestRepository;

    private final Validations validations;
    private final ModelMapper mapper;

    @Autowired
    private SupplyRequestResponseRepository supplyRequestResponseRepository;

    @Autowired
    private SupplyRequestResponseService supplyRequestResponseService;

    @Autowired
    private ProductRepository productRepository;

    public SupplyRequestService(SupplyRequestRepository supplyRequestRepository, Validations validations, ModelMapper mapper) {
        this.supplyRequestRepository = supplyRequestRepository;
        this.validations = validations;
        this.mapper = mapper;
    }

    public SupplyRequestResponse createSupplyRequest(SupplyRequestRequest request) {
        validations.validateSupplyRequest(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequest supplyRequest = mapper.map(request, SupplyRequest.class);
        LocalDateTime fiveMinutesLater = LocalDateTime.now().plusMinutes(15);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        LocalDateTime formatPresentDateTime = LocalDateTime.parse(fiveMinutesLater.format(formatter));
        boolean supplyRequestExists = supplyRequestRepository.existsByReferenceNo(
                request.getReferenceNo());
        if (supplyRequestExists) {
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " SupplyRequest already exist");
        }
        Product savedProduct = productRepository.findProductById(request.getProductId());
        if (savedProduct == null){
           throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested warehouse good Id does not exist!");
        }
        supplyRequest.setCreatedBy(userCurrent.getId());
        supplyRequest.setIsActive(false);
        supplyRequest.setDeliveryStatus("Awaiting_Shipment");
        supplyRequest.setProductWeight(savedProduct.getWeight());
        supplyRequest.setExpireTime(fiveMinutesLater);
        log.info("Setting Expiry time :::::::::::::::::::::::::::: " + fiveMinutesLater);
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
        supplyRequest.setUpdatedBy(userCurrent.getId());
        SupplyRequestResponseEntity supplyRequestResponse = new SupplyRequestResponseEntity();
        SupplyRequestResponseRequest supplyRequestResponseRequest = new SupplyRequestResponseRequest();

        if(supplyRequest.getStatus() != request.getStatus())
        {
            if (request.getStatus().equalsIgnoreCase("Pending") || supplyRequest.getStatus().equalsIgnoreCase("Pending")) {
                supplyRequestResponse = supplyRequestResponseRepository.findBySupplyRequestId(supplyRequest.getId());
                if (supplyRequestResponse == null) {
                    supplyRequestResponseRequest.setSupplyRequestId(supplyRequest.getId());
                    supplyRequestResponseRequest.setUserId(userCurrent.getId());
                    supplyRequestResponseRequest.setResponseDate(supplyRequest.getUpdatedDate().now());
                    supplyRequestResponseRequest.setStatus(request.getStatus());
                    supplyRequestResponseRequest.setRejectReason(request.getRejectReason());
                    supplyRequestResponseService.createSupplyRequestResponse(supplyRequestResponseRequest);
                }
            }
            if(request.getStatus().equalsIgnoreCase("Rejected")){
                supplyRequestResponse = supplyRequestResponseRepository.findBySupplyRequestId(supplyRequest.getId());
                if (supplyRequestResponse != null) {
                    supplyRequestResponseRequest.setSupplyRequestId(supplyRequest.getId());
                    supplyRequestResponseRequest.setUserId(userCurrent.getId());
                    supplyRequestResponseRequest.setResponseDate(supplyRequest.getUpdatedDate().now());
                    supplyRequestResponseRequest.setStatus(request.getStatus());
                    supplyRequestResponseRequest.setRejectReason(request.getRejectReason());
                    supplyRequestResponseRequest.setId(supplyRequestResponse.getId());
                    supplyRequestResponseService.updateSupplyRequestResponse(supplyRequestResponseRequest);
                }else if (supplyRequestResponse == null){
                    supplyRequestResponseRequest.setSupplyRequestId(supplyRequest.getId());
                    supplyRequestResponseRequest.setUserId(userCurrent.getId());
                    supplyRequestResponseRequest.setResponseDate(supplyRequest.getUpdatedDate().now());
                    supplyRequestResponseRequest.setStatus(request.getStatus());
                    supplyRequestResponseRequest.setRejectReason(request.getRejectReason());
                    supplyRequestResponseService.createSupplyRequestResponse(supplyRequestResponseRequest);
                }
            }
            if(request.getStatus().equalsIgnoreCase("Accepted")) {
                supplyRequestResponse = supplyRequestResponseRepository.findBySupplyRequestId(supplyRequest.getId());
                if (supplyRequestResponse != null) {
                    supplyRequestResponseRequest.setSupplyRequestId(supplyRequest.getId());
                    supplyRequestResponseRequest.setUserId(userCurrent.getId());
                    supplyRequestResponseRequest.setResponseDate(supplyRequest.getUpdatedDate().now());
                    supplyRequestResponseRequest.setStatus(request.getStatus());
                    supplyRequestResponseRequest.setRejectReason(request.getRejectReason());
                    supplyRequestResponseRequest.setId(supplyRequestResponse.getId());
                    supplyRequestResponseService.updateSupplyRequestResponse(supplyRequestResponseRequest);
                }
            }
        }

        if (request.getStatus().equalsIgnoreCase("Rejected")){
            request.setStatus("Pending");
            request.setWarehouseId(0l);
            mapper.map(request, supplyRequest);
        }else {
            mapper.map(request, supplyRequest);
        }
            supplyRequestRepository.save(supplyRequest);
        log.debug("State record updated - {}" + new Gson().toJson(supplyRequest));
        return mapper.map(supplyRequest, SupplyRequestResponse.class);
    }




    public SupplyRequestResponse findSupplyRequest(long id){
        SupplyRequest supplyRequest = supplyRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supply Request Id does not exist!"));
        Product savedProduct = productRepository.findProductById(supplyRequest.getProductId());
        supplyRequest.setProductWeight(savedProduct.getWeight());
        supplyRequest.setProductImage(savedProduct.getImage());
        return mapper.map(supplyRequest, SupplyRequestResponse.class);
    }

    public Page<SupplyRequest> findAll(Long productId, String productName, Long askingQuantity, BigDecimal askingPrice, Date startTime, Date endTime,String referenceNo,String status,Long warehouseId,Long supplierId,Boolean unassigned,PageRequest pageRequest ){
        Page<SupplyRequest> supplyRequests = supplyRequestRepository.findSupplyRequests(productId,productName,askingQuantity,askingPrice,startTime,endTime,referenceNo,status,warehouseId,supplierId,unassigned,pageRequest);
        if(supplyRequests == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        supplyRequests.forEach(supplyRequest -> {
            Product savedProduct = productRepository.findProductById(supplyRequest.getProductId());
            supplyRequest.setProductWeight(savedProduct.getWeight());
            supplyRequest.setProductImage(savedProduct.getImage());
        });
        return supplyRequests;

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
        return supplyRequestRepository.findByIsActiveOrderByIdDesc(isActive);
    }

    public void pushToIncomingRequest() {
        LocalDateTime presentTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime fiveMinutesLater = LocalDateTime.now().plusMinutes(15L);
        List<SupplyRequest> supplyRequests = supplyRequestRepository.findSupplyRequest("Pending",
                presentTime, fiveMinutesLater);
        log.info("Result Of of expire time ::::::::::::::::::::::::::::::::::::::: " + fiveMinutesLater);
        log.info("ExpiryTime ::::::::::::::::::::::::::::::::::::::: " + presentTime);
        log.info("Result Of Fetch ::::::::::::::::::::::::::::::::::::::: " + supplyRequests);
        supplyRequests.forEach(supplyRequest -> {
            supplyRequest.setWarehouseId(0l);
            supplyRequest.setSupplierId(0l);
            supplyRequestRepository.save(supplyRequest);
        });
        if (supplyRequests == null) {
            throw new ConflictException(CustomResponseCode.
                    CONFLICT_EXCEPTION, "Not outstanding Supply request");
        }
    }

    public List<SupplyRequest> timeCheck (LocalDateTime presentTime, LocalDateTime expiredTime) {
            List<SupplyRequest> supplyRequests = this.supplyRequestRepository.findSupplyRequest("Pending",
                    presentTime, expiredTime);
        supplyRequests.forEach(supplyRequest -> {
            supplyRequest.setWarehouseId(1l);
            supplyRequest.setPhone("090505050505050");
            supplyRequestRepository.save(supplyRequest);
        });

            return supplyRequests;

    }
}
