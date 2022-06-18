package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.ProductRepository;
import com.sabi.supplier.service.repositories.SupplierRepository;
import com.sabi.supplier.service.repositories.SupplyRequestRepository;
import com.sabi.supplier.service.repositories.SupplyRequestResponseRepository;
import com.sabi.suppliers.core.dto.request.SupplyRequestRequest;
import com.sabi.suppliers.core.dto.request.SupplyRequestResponseRequest;
import com.sabi.suppliers.core.dto.response.SupplyRequestResponse;
import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.Supplier;
import com.sabi.suppliers.core.models.SupplyRequest;
import com.sabi.suppliers.core.models.SupplyRequestResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
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

    @Autowired
    private SupplierRepository supplierRepository;

    public SupplyRequestService(SupplyRequestRepository supplyRequestRepository, Validations validations, ModelMapper mapper) {
        this.supplyRequestRepository = supplyRequestRepository;
        this.validations = validations;
        this.mapper = mapper;
    }

    public SupplyRequestResponse createSupplyRequest(SupplyRequestRequest request) {
        request.setStatus("Pending");
        validations.validateSupplyRequest(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequest supplyRequest = mapper.map(request, SupplyRequest.class);
        LocalDateTime fiveMinutesLater = LocalDateTime.now().plusMinutes(15);
        LocalDateTime presentTime = LocalDateTime.now();
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
        if (request.getSupplierId() != null) {
            Supplier savedSupplier = supplierRepository.findSupplierById(request.getSupplierId());
            if (savedSupplier == null) {
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!");
            }
            supplyRequest.setSupplierName(savedSupplier.getName());
        }
        supplyRequest.setCreatedBy(userCurrent.getId());
        supplyRequest.setIsActive(false);
        if (request.getWarehouseId() == null){
            supplyRequest.setUnassigned(true);
        } else {
            supplyRequest.setUnassigned(false);
        }
        supplyRequest.setStartTime(presentTime);
        supplyRequest.setEndTime(fiveMinutesLater);
        supplyRequest.setDeliveryStatus("Awaiting_Shipment");
        supplyRequest.setProductWeight(savedProduct.getWeight());
        supplyRequest.setProductName(savedProduct.getName());
        supplyRequest.setExpiryTime(Utility.expirationForSupplyRequest());
        log.info("Setting Expiry time :::::::::::::::::::::::::::: " + fiveMinutesLater);
        supplyRequest = supplyRequestRepository.save(supplyRequest);
        log.debug("Create new supply request - {}" + new Gson().toJson(supplyRequest));
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
        Supplier savedSupplier = null;
        if(supplyRequest.getSupplierId() != null) {
            System.out.println("checking");
             savedSupplier = supplierRepository.findSupplierById(supplyRequest.getSupplierId());
            supplyRequest.setSupplierName(savedSupplier.getName());
        }
        return mapper.map(supplyRequest, SupplyRequestResponse.class);
    }

    public Page<SupplyRequest> findAll(Long productId, String productName, Long askingQuantity, BigDecimal askingPrice,
                                       Date startTime, Date endTime,String referenceNo,String status,Long warehouseId,
                                       Long supplierId,Boolean unassigned,PageRequest pageRequest ){
        Page<SupplyRequest> supplyRequests = supplyRequestRepository.findSupplyRequests(productId,productName,askingQuantity,askingPrice,
                startTime,endTime,referenceNo,status,warehouseId,supplierId,unassigned,pageRequest);
        if(supplyRequests == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        supplyRequests.forEach(supplyRequest -> {
            Product savedProduct = productRepository.findProductById(supplyRequest.getProductId());
            supplyRequest.setProductWeight(savedProduct.getWeight());
            supplyRequest.setProductImage(savedProduct.getImage());
            Supplier savedSupplier = null;
            if(supplyRequest.getSupplierId() != null) {
                System.out.println("checking");
                if(supplyRequest.getSupplierId() == 0l){
                    supplyRequest.setSupplierName("--");
                }else if(supplyRequest.getSupplierId() != 0l) {
                    savedSupplier = supplierRepository.findSupplierById(supplyRequest.getSupplierId());
                    supplyRequest.setSupplierName(savedSupplier.getName());
                }
            }
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
//        LocalDateTime presentTime = LocalDateTime.now();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime fiveMinutesLater = LocalDateTime.now().plusMinutes(45L);
//        Calendar calObj = Calendar.getInstance();
//        String presentTime = df.format(calObj.getTime());

//        Calendar calObj = Calendar.getInstance();
//        String presentTime = df.format(calObj.getTime());

        List<SupplyRequest> supplyRequests = supplyRequestRepository.findSupplyRequestByStatus("Pending");
        supplyRequests.forEach(supplyRequest -> {
            Calendar calObj = Calendar.getInstance();
        String presentTime = df.format(calObj.getTime());
        if (supplyRequest.getExpiryTime() != null) {
            String expiredTime = supplyRequest.getExpiryTime();
            String result = String.valueOf(presentTime.compareTo(expiredTime));
            if (!result.startsWith("-")) {
                supplyRequest.setWarehouseId(0l);
                supplyRequest.setSupplierId(0l);
                supplyRequest.setUnassigned(true);
                supplyRequestRepository.save(supplyRequest);
            }
        }
//        else
//            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"No record Found to be updated");
        } );
//        String regDate =
//        List<SupplyRequest> supplyRequests = supplyRequestRepository.findSupplyRequest("Pending",
//                presentTime, fiveMinutesLater);
//        log.info("Result Of of expire time ::::::::::::::::::::::::::::::::::::::: " + fiveMinutesLater);
//        log.info("ExpiryTime ::::::::::::::::::::::::::::::::::::::: " + presentTime);
//        log.info("Result Of Fetch ::::::::::::::::::::::::::::::::::::::: " + supplyRequests);
//        supplyRequests.forEach(supplyRequest -> {
//            supplyRequest.setWarehouseId(3l);
//            supplyRequest.setSupplierId(3l);
//            supplyRequest.setUnassigned(true);
//            supplyRequestRepository.save(supplyRequest);
//        });

        if (supplyRequests == null) {
            throw new ConflictException(CustomResponseCode.
                    CONFLICT_EXCEPTION, "Not outstanding Supply request");
        }
    }

    public List<SupplyRequest> timeCheck () {
            List<SupplyRequest> supplyRequests = this.supplyRequestRepository.findSupplyRequestByStatus("Pending");
        supplyRequests.forEach(supplyRequest -> {
            supplyRequest.setWarehouseId(1l);
            supplyRequest.setPhone("090505050505050");
            supplyRequestRepository.save(supplyRequest);
        });

            return supplyRequests;

    }
}
