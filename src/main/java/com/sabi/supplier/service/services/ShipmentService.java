package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.exceptions.ProcessingException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.ShipmentDto;
import com.sabi.suppliers.core.dto.request.ShipmentShipmentItemDto;
import com.sabi.suppliers.core.dto.request.ShipmentTripRequest;
import com.sabi.suppliers.core.models.ProductCount;
import com.sabi.suppliers.core.models.Shipment;
import com.sabi.suppliers.core.models.ShipmentItem;
import com.sabi.suppliers.core.models.SupplyRequest;
import com.sabi.suppliers.core.models.response.ProductCountResponse;
import com.sabi.suppliers.core.models.response.ShipmentItemResponseDto;
import com.sabi.suppliers.core.models.response.ShipmentResponseDto;
import com.sabi.suppliers.core.models.response.ShipmentShipmentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ShipmentItemService shipmentItemService;
    @Autowired
    private ProductCountService productCountService;
    @Autowired
    private ProductCountRepository productCountRepository;
    @Autowired
    private PartnerSignUpService partnerSignUpService;
    @Autowired
    private SupplyRequestRepository supplyRequestRepository;

    @Autowired
    private ShipmentItemRepository shipmentItemRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    @Autowired
    private WareHouseRepository warehouseRepository;

    public ShipmentService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    /** <summary>
     * shipment creation
     * </summary>
     * <remarks>this method is responsible for creation of new shipment</remarks>
     */

    public ShipmentResponseDto createShipment(ShipmentDto request) {
        if (request.getStatus() == null|| request.getStatus().isEmpty()){
            request.setStatus("Awaiting_Shipment");
        }
        validations.validateShipment(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        generateShipmentReferenceNumber(request);
        Shipment shipment = mapper.map(request,Shipment.class);
//        Shipment shipmentExists = shipmentRepository.findShipmentById(request.getWarehouseId());
//        if(shipmentExists != null){
//            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "shipment already exist");
//        }
        shipment.setCreatedBy(userCurrent.getId());
        shipment.setIsActive(true);
//        shipment.setStatus("Awaiting_Shipment");
        shipment = shipmentRepository.save(shipment);
        log.debug("Create new shipment - {}"+ new Gson().toJson(shipment));
        ShipmentResponseDto productResponseDto =  mapper.map(shipment, ShipmentResponseDto.class);
        return productResponseDto;

    }

    public ShipmentShipmentResponseDto createShipmentItems(ShipmentShipmentItemDto request) {
        List<ShipmentItemResponseDto> responseDtos = new ArrayList<>();
        List<ProductCount> productCountList = new ArrayList<>();
        List<ProductCountResponse> productCountResponseDtos = new ArrayList<>();
        validations.validateShipmentAndShipmentItem(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        generateShipmentReferenceNumbers(request);
        Shipment shipment = mapper.map(request,Shipment.class);
        shipment.setCreatedBy(userCurrent.getId());
        shipment.setIsActive(true);
        shipment.setStatus("Awaiting_Shipment");
        shipment = shipmentRepository.save(shipment);
        log.debug("Create new shipment - {}"+ new Gson().toJson(shipment));
        ShipmentShipmentResponseDto orderResponseDto = mapper.map(shipment, ShipmentShipmentResponseDto.class);
        log.info("request sent ::::::::::::::::::::::::::::::::: " + request.getShipmentItemDtoList());
        Long  shipmentId = shipment.getId();
        request.getShipmentItemDtoList().forEach(orderItemRequest ->{
            SupplyRequest savedSupplyRequest = supplyRequestRepository.findSupplyRequestById(orderItemRequest.getSupplierRequestId());
            orderItemRequest.setCustomerName(savedSupplyRequest.getCustomerName());
            orderItemRequest.setDeliveryAddress(savedSupplyRequest.getDeliveryAddress());
            orderItemRequest.setEmail(savedSupplyRequest.getEmail());
            orderItemRequest.setShipmentId(shipmentId);
            orderItemRequest.setPhoneNumber(savedSupplyRequest.getPhone());
        });
            request.getShipmentItemDtoList().forEach(requestItem ->{
                ProductCount productCountToSave = new ProductCount();
                List<SupplyRequest> savedSupplyRrquestsList = supplyRequestRepository.findAllSupplyRequestById(requestItem.getSupplierRequestId());
                System.out.println("Supply Request Id : {}:::::::::::::: " + requestItem.getSupplierRequestId());
//                AtomicLong quantity = new AtomicLong();
                savedSupplyRrquestsList.forEach(supplyRequest -> {
//                  Long checker =  quantity.addAndGet(1);
//                    log.info("quantity :::::::::::::::::: {} " +quantity );
//                    log.info("quantity :::::::::::::::::: {} " +checker );
                    productCountToSave.setShipmentId(requestItem.getShipmentId());
                    productCountToSave.setProductId(supplyRequest.getProductId());
                    productCountToSave.setName(supplyRequest.getProductName());
                    productCountToSave.setShipmentId(Long.valueOf(shipmentId));
//                    productCountToSave.setQuantity(checker);
//                    System.out.println("Supply Quantity  : {}:::::::::::::: " + quantity);
                    productCountList.add(productCountToSave);
                });

                });

        responseDtos = shipmentItemService.createShipmentItems(request.getShipmentItemDtoList());
        productCountResponseDtos = productCountService.createProductCount(productCountList);
        List<ShipmentItemResponseDto> finalResponseDtos = responseDtos;
        List<ProductCountResponse> finalProductCountResponseDtos = productCountResponseDtos;
        responseDtos.forEach(orderItemResponseDto -> {
            orderResponseDto.setShipmentItemResponseDtoList(finalResponseDtos);
            orderResponseDto.setProductCountResponseList(finalProductCountResponseDtos);
        });
        return orderResponseDto;
    }



    /** <summary>
     * shipment update
     * </summary>
     * <remarks>this method is responsible for updating already existing shipment</remarks>
     */

    public ShipmentResponseDto updateShipment(ShipmentDto request) {
        validations.validateShipment(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Shipment state = shipmentRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment Id does not exist!"));
        mapper.map(request, state);
        state.setUpdatedBy(userCurrent.getId());
        shipmentRepository.save(state);
        log.debug("shipment record updated - {}"+ new Gson().toJson(state));
        return mapper.map(state, ShipmentResponseDto.class);
    }


    /** <summary>
     * Find shipment
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public ShipmentResponseDto findShipment(Long id){
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment Id does not exist!"));
        return mapper.map(shipment,ShipmentResponseDto.class);
    }

    public ShipmentShipmentResponseDto findMasterShipment(Long id){
        List<ProductCount> productCountList = new ArrayList<>();
        ShipmentShipmentResponseDto responseDto = new ShipmentShipmentResponseDto();
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment Id does not exist!"));
        responseDto.setId(shipment.getId());
        responseDto.setWarehouseId(shipment.getWarehouseId());
        responseDto.setDeliveryDate(shipment.getDeliveryDate());
        responseDto.setLogisticPartnerId(shipment.getLogisticPartnerId());
        responseDto.setLogisticPartnerName(shipment.getLogisticPartnerName());
        responseDto.setPhoneNumber(shipment.getPhoneNumber());
        responseDto.setVehicle(shipment.getVehicle());
        responseDto.setStatus(shipment.getStatus());
        responseDto.setQuantity(shipment.getQuantity());
        responseDto.setTotalAmount(shipment.getTotalAmount());
        responseDto.setExpectedDeliveryDate(shipment.getExpectedDeliveryDate());
        responseDto.setStartTime(shipment.getStartTime());
        responseDto.setEndTime(shipment.getEndTime());
        responseDto.setFeedStatus(shipment.getFeedStatus());
        responseDto.setCreatedBy(shipment.getCreatedBy());
        responseDto.setUpdatedDate(shipment.getUpdatedDate());
        responseDto.setCreatedBy(shipment.getCreatedBy());
//        ShipmentItem savedShipmentItem = new ShipmentItem();
        List<ShipmentItem> savedShipmentItem = shipmentItemRepository.findShipmentItemByShipmentId(shipment.getId());
        if (savedShipmentItem == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested shipment Id does not exist!");
        }
        log.info("Saved shipment item :::::::::::::::::::::: {} "+savedShipmentItem);
//        ShipmentItem shipmentItem = new ShipmentItem();
        List<ShipmentItemResponseDto> shipmentItemList = new ArrayList<>();
        savedShipmentItem.forEach(item -> {
            ShipmentItemResponseDto shipmentItem = new ShipmentItemResponseDto();
            shipmentItem.setId(item.getId());
            shipmentItem.setSupplierRequestId(item.getSupplierRequestId());
            shipmentItem.setQuantity(item.getQuantity());
            shipmentItem.setPrice(item.getPrice());
            shipmentItem.setStatus(item.getStatus());
            shipmentItem.setDeliveryAddress(item.getDeliveryAddress());
            shipmentItem.setEmail(item.getEmail());
            shipmentItem.setPhoneNumber(item.getPhoneNumber());
            shipmentItem.setDeliveryDate(item.getDeliveryDate());
            shipmentItem.setCreatedDate(item.getCreatedDate());
            shipmentItem.setUpdatedDate(item.getUpdatedDate());
            shipmentItem.setShipmentId(item.getShipmentId());
            shipmentItem.setCreatedBy(item.getCreatedBy());
            shipmentItemList.add(shipmentItem);
            log.info("Checking ::::::::::: {} 123 " + shipmentItemList);
        });
        log.info("Shipment item single :::::::::::::: {} "+ shipmentItemList);
        responseDto.setShipmentItemResponseDtoList(shipmentItemList);
        List<ProductCount> savedProductCount = productCountRepository.findProductCountByShipmentId(shipment.getId());
        if (savedProductCount == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested Product Count Id does not exist!");
        }
        List<ProductCountResponse> productCountResponseList = new ArrayList<>();
        savedProductCount.forEach(productCount -> {
            ProductCountResponse productCountResponse = new ProductCountResponse();
            productCountResponse.setId(productCount.getId());
            productCountResponse.setId(productCount.getId());
            productCountResponse.setShipmentId(productCount.getQuantity());
            productCountResponse.setProductId(productCount.getProductId());
            productCountResponse.setName(productCount.getName());
            productCountResponse.setQuantity(productCount.getQuantity());
            productCountResponse.setCreatedDate(productCount.getCreatedDate());
            productCountResponse.setUpdatedDate(productCount.getUpdatedDate());
            productCountResponse.setCreatedBy(productCount.getCreatedBy());
            productCountResponseList.add(productCountResponse);
            log.info("Checking ::::::::::: {} 123 " + shipmentItemList);
        });
        responseDto.setProductCountResponseList(productCountResponseList);

        log.info("Response :::::::::::::: {} " + responseDto);
        return responseDto;
    }


    /** <summary>
     * Find all shipment
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<Shipment> findAll(Long warehouseId, Long logisticPartnerId, String logisticsPartnerName, String phoneNumber, String vehicle, String status, PageRequest pageRequest ){
        Page<Shipment> state = shipmentRepository.findShipments(warehouseId,logisticPartnerId,logisticsPartnerName,phoneNumber,vehicle,status,pageRequest);
        if(state == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return state;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a state</remarks>
     */
    public void enableDisEnableShipment (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Shipment state = shipmentRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment Id does not exist!"));
        state.setIsActive(request.isActive());
        state.setUpdatedBy(userCurrent.getId());
        shipmentRepository.save(state);

    }


    public List<Shipment> getAll(Boolean isActive){
        List<Shipment> shipments = shipmentRepository.findByIsActiveOrderByIdDesc(isActive);
        return shipments;

    }

    public List<Shipment> getAllShipment(Long supplierId){
        List<Shipment> shipments = shipmentRepository.findShipmentBySupplierId(supplierId);
        return shipments;
    }

//    public List<Shipment> getAllShipmentData(Long supplierId, LocalDateTime startDate, LocalDateTime endDate){
//        List<Shipment> shipments = shipmentRepository.findShipmentBySupplierId(supplierId, startDate,endDate);
//        return shipments;
//    }


    public void shipmentTripRequests(){
        List<Shipment> shipments = shipmentRepository.findByFeedStatus("pending");

        if(shipments ==null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No pending shipment found!");
        }

        shipments.forEach(pendingShipment->{
            ShipmentTripRequest shipmentTripRequest = ShipmentTripRequest.builder()
//                    .assestId(Long.valueOf(pendingShipment.getAssestId()))
                    .deliveryDate(pendingShipment.getDeliveryDate())
                    .endTime(pendingShipment.getEndTime())
                    .id(pendingShipment.getId())
                    .logisticPartnerId(Long.valueOf(pendingShipment.getLogisticPartnerId()))
                    .phoneNumber(pendingShipment.getPhoneNumber())
                    .startTime(pendingShipment.getStartTime())
                    .status(pendingShipment.getStatus())
                    .totalAmount(pendingShipment.getTotalAmount())
                    .warehouseId(pendingShipment.getWarehouseId())
                    .build();
            log.info(":::::::  shipment request ::::::: " + shipmentTripRequest);
            partnerSignUpService.shipmentTripRequest(shipmentTripRequest);

        });
    }

    public void generateShipmentReferenceNumber(ShipmentDto shipmentDto) {
        String rawKey = shipmentDto.getPhoneNumber()+ shipmentDto.getLogisticPartnerName() + shipmentDto.getExpectedDeliveryDate() + new Date() + Math.random();
        String encodedKey = hashWithSha256(rawKey);
        shipmentDto.setShipmentReferenceNumber("SPM-" + encodedKey.substring(0, 20));
    }

    public void generateShipmentReferenceNumbers(ShipmentShipmentItemDto shipmentDto) {
        String rawKey = shipmentDto.getPhoneNumber()+ shipmentDto.getLogisticPartnerName() + shipmentDto.getExpectedDeliveryDate() + new Date() + Math.random();
        String encodedKey = hashWithSha256(rawKey);
        shipmentDto.setShipmentReferenceNumber("SPM-" + encodedKey.substring(0, 20));
    }

    public static String hashWithSha256(String rawKey) throws ProcessingException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encrypted = md.digest(rawKey.getBytes());
            return new String(Hex.encodeHex(encrypted));
        } catch (NoSuchAlgorithmException ex) {
            String errorMessage = "Unable to hash this string";
            throw new ProcessingException(errorMessage);
        }
    }
}
