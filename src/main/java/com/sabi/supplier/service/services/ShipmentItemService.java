package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.ShipmentItemRepository;
import com.sabi.supplier.service.repositories.ShipmentRepository;
import com.sabi.supplier.service.repositories.SupplyRequestRepository;
import com.sabi.suppliers.core.dto.request.ShipmentItemDto;
import com.sabi.suppliers.core.dto.response.ShipmentItemInfoResponse;
import com.sabi.suppliers.core.dto.response.ShipmentItemResponseDto;
import com.sabi.suppliers.core.models.Shipment;
import com.sabi.suppliers.core.models.ShipmentItem;
import com.sabi.suppliers.core.models.SupplyRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShipmentItemService {

    private final ModelMapper mapper;
    @Autowired
    private Validations validations;
    @Autowired
    private ShipmentItemRepository repository;
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private SupplyRequestRepository supplyRequestRepository;

    public ShipmentItemService(ModelMapper mapper, Validations validations, ShipmentItemRepository repository) {
        this.mapper = mapper;
        this.validations = validations;
        this.repository = repository;
    }

    public ShipmentItemResponseDto createShipmentItem(ShipmentItemDto request) {
        validations.validateShipmentItem(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ShipmentItem shipmentItem = mapper.map(request,ShipmentItem.class);
        ShipmentItem productExists = repository.findShipmentItemById(request.getShipmentId());
        if(productExists != null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "shipment item already exist");
        }
        Shipment savedShipmentItem = shipmentRepository.findShipmentById(request.getShipmentId());
        if (savedShipmentItem == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested shipment Id does not exist!");
        }
        shipmentItem.setCreatedBy(userCurrent.getId());
        shipmentItem.setIsActive(true);
//        shipmentItem.setStatus("Awaiting_shipment");
        shipmentItem = repository.save(shipmentItem);
        log.debug("Create new preference - {}"+ new Gson().toJson(shipmentItem));
        ShipmentItemResponseDto productResponseDto =  mapper.map(shipmentItem, ShipmentItemResponseDto.class);
        return productResponseDto;

    }

    public  List<ShipmentItemResponseDto> createShipmentItems(List<ShipmentItemDto> requests) {
        List<ShipmentItemResponseDto> responseDtos = new ArrayList<>();
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        requests.forEach(request->{
            validations.validateShipmentItem(request);
            ShipmentItem shipmentItem = mapper.map(request,ShipmentItem.class);
            ShipmentItem exist = repository.findShipmentItemById(request.getShipmentId());
            if(exist !=null){
                throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " shipment item already exist");
            }
            shipmentItem.setCreatedBy(userCurrent.getId());
            shipmentItem.setStatus("Awaiting shipment");
            SupplyRequest supplyRequest = supplyRequestRepository.findSupplyRequestById(request.getSupplierRequestId());
            if (supplyRequest == null) {
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "supply request Id does not exist!");
            }
            supplyRequest.setStatus("Awaiting shipment");
            supplyRequestRepository.save(supplyRequest);
            shipmentItem.setIsActive(true);
            shipmentItem = repository.save(shipmentItem);
            log.debug("Create new asset picture - {}"+ new Gson().toJson(shipmentItem));
            responseDtos.add(mapper.map(shipmentItem, ShipmentItemResponseDto.class));
        });
        return responseDtos;
    }

    public ShipmentItemResponseDto updateShipmentItem(ShipmentItemDto request) {
        validations.validateShipmentItem(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ShipmentItem shipmentItem = repository.findShipmentItemById(request.getId());
        if (shipmentItem == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested shipment item Id does not exist!");
        }
        Shipment savedShipmentItem = shipmentRepository.findShipmentById(request.getShipmentId());
        if (savedShipmentItem == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested shipment Id does not exist!");
        }
        mapper.map(request, shipmentItem);
        shipmentItem.setUpdatedBy(userCurrent.getId());
        shipmentItem.setIsActive(true);
        repository.save(shipmentItem);
        log.debug("shipment item record updated - {}"+ new Gson().toJson(shipmentItem));
        ShipmentItemResponseDto preferenceResponseDto =  mapper.map(shipmentItem, ShipmentItemResponseDto.class);
        return preferenceResponseDto;

    }

    public ShipmentItemResponseDto findShipmentItemById(Long id){
        ShipmentItem shipmentItem  = repository.findShipmentItemById(id);
        if (shipmentItem == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested shipment item id does not exist!");
        }
        ShipmentItemResponseDto productResponseDto =  mapper.map(shipmentItem, ShipmentItemResponseDto.class);
        return productResponseDto;
    }

    public ShipmentItemInfoResponse findShipmentItemBySupplierRequestId(Long supplyRequestId){

        ShipmentItem shipmentItem  = repository.findShipmentItemBySupplierRequestId(supplyRequestId);
        if (shipmentItem == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested shipment item id does not exist!");
        }
        Shipment savedShipment= shipmentRepository.findShipmentById(shipmentItem.getShipmentId());
        SupplyRequest savedSupplyRequest = supplyRequestRepository.findSupplyRequestById(shipmentItem.getSupplierRequestId());
        ShipmentItemInfoResponse shipmentItemInfoResponse =  mapper.map(shipmentItem, ShipmentItemInfoResponse.class);
        shipmentItemInfoResponse.setWarehouseId(savedShipment.getWarehouseId());
        shipmentItemInfoResponse.setShipmentDeliveryDate(savedShipment.getDeliveryDate());
        shipmentItemInfoResponse.setLogisticPartnerId(savedShipment.getLogisticPartnerId());
        shipmentItemInfoResponse.setLogisticPartnerName(savedShipment.getLogisticPartnerName());
        shipmentItemInfoResponse.setPhoneNumber(savedShipment.getPhoneNumber());
        shipmentItemInfoResponse.setVehicle(savedShipment.getVehicle());
        shipmentItemInfoResponse.setShipmentsStatus(savedShipment.getStatus());
        shipmentItemInfoResponse.setShipmentQuantity(savedShipment.getQuantity());
        shipmentItemInfoResponse.setTotalAmount(savedShipment.getTotalAmount());
        shipmentItemInfoResponse.setExpectedDeliveryDate(savedShipment.getExpectedDeliveryDate());
        shipmentItemInfoResponse.setStartTime(savedShipment.getStartTime());
        shipmentItemInfoResponse.setEndTime(savedShipment.getEndTime());
        shipmentItemInfoResponse.setShipment_createdDate(savedShipment.getCreatedDate());
        shipmentItemInfoResponse.setShipment_updatedBy(savedShipment.getUpdatedBy());
        shipmentItemInfoResponse.setShipment_createdBy(savedShipment.getCreatedBy());
        shipmentItemInfoResponse.setIsActive(savedShipment.getIsActive());
        shipmentItemInfoResponse.setDriverName(savedShipment.getDriverName());
        shipmentItemInfoResponse.setPartnerName(savedShipment.getPartnerName());
        shipmentItemInfoResponse.setPartnerId(savedShipment.getPartnerId());
        shipmentItemInfoResponse.setAssestName(savedShipment.getAssestName());
        shipmentItemInfoResponse.setAssestId(savedShipment.getAssestId());
        shipmentItemInfoResponse.setProductId(savedSupplyRequest.getProductId());
        shipmentItemInfoResponse.setProductName(savedSupplyRequest.getProductName());
        shipmentItemInfoResponse.setAskedQuantity(savedSupplyRequest.getAskedQuantity());
        shipmentItemInfoResponse.setAskingPrice(savedSupplyRequest.getAskingPrice());
        shipmentItemInfoResponse.setSupplyRequest_startTime(savedSupplyRequest.getStartTime());
        shipmentItemInfoResponse.setSupplyRequest_endTime(savedSupplyRequest.getEndTime());
        shipmentItemInfoResponse.setReferenceNo(savedSupplyRequest.getReferenceNo());
        shipmentItemInfoResponse.setSupplyRequest_status(savedSupplyRequest.getStatus());
        shipmentItemInfoResponse.setSupplyRequest_quantity(savedSupplyRequest.getQuantity());
        shipmentItemInfoResponse.setSupplyRequest_price(savedSupplyRequest.getPrice());
        shipmentItemInfoResponse.setSupplyRequest_warehouseId(savedSupplyRequest.getWarehouseId());
        shipmentItemInfoResponse.setDropOffAddress(savedSupplyRequest.getDropOffAddress());
        shipmentItemInfoResponse.setDateAccepted(savedSupplyRequest.getDateAccepted());
        shipmentItemInfoResponse.setAskedQuantity(savedSupplyRequest.getAskedQuantity());
        shipmentItemInfoResponse.setAskedPrice(savedSupplyRequest.getAskedPrice());
        shipmentItemInfoResponse.setSupplyRequest_deliveryDate(savedSupplyRequest.getDeliveryDate());
        shipmentItemInfoResponse.setDeliveryAddress(savedSupplyRequest.getDeliveryAddress());
        shipmentItemInfoResponse.setEmail(savedSupplyRequest.getEmail());
        shipmentItemInfoResponse.setPhone(savedSupplyRequest.getPhone());
        shipmentItemInfoResponse.setRejectReason(savedSupplyRequest.getRejectReason());
        return shipmentItemInfoResponse;
    }

    public Page<ShipmentItem> findAll(Long supplierRequestedId, Long shipmentId, PageRequest pageRequest ){
        Page<ShipmentItem> shipments = repository.findAll(supplierRequestedId,shipmentId,pageRequest);
        if(shipments == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return shipments;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a state</remarks>
     */
    public void enableDisEnableShipment (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ShipmentItem state = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment item Id does not exist!"));
        state.setIsActive(request.isActive());
        state.setUpdatedBy(userCurrent.getId());
        repository.save(state);

    }


    public List<ShipmentItem> getAll(Boolean isActive){
        List<ShipmentItem> shipment = repository.findByIsActive(isActive);
        return shipment;

    }
}
