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
import com.sabi.supplier.service.repositories.ShipmentRepository;
import com.sabi.supplier.service.repositories.StateRepository;
import com.sabi.supplier.service.repositories.WareHouseRepository;
import com.sabi.suppliers.core.dto.request.ShipmentDto;
import com.sabi.suppliers.core.dto.request.ShipmentShipmentItemDto;
import com.sabi.suppliers.core.dto.response.ShipmentItemResponseDto;
import com.sabi.suppliers.core.dto.response.ShipmentResponseDto;
import com.sabi.suppliers.core.dto.response.ShipmentShipmentResponseDto;
import com.sabi.suppliers.core.models.Shipment;
import com.sabi.suppliers.core.models.ShipmentItem;
import com.sabi.suppliers.core.models.WareHouse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ShipmentItemService shipmentItemService;
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
        validations.validateShipment(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Shipment shipment = mapper.map(request,Shipment.class);
        Shipment shipmentExists = shipmentRepository.findShipmentById(request.getWarehouseId());
        if(shipmentExists != null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "shipment already exist");
        }
        shipment.setCreatedBy(userCurrent.getId());
        shipment.setIsActive(true);
        shipment = shipmentRepository.save(shipment);
        log.debug("Create new shipment - {}"+ new Gson().toJson(shipment));
        ShipmentResponseDto productResponseDto =  mapper.map(shipment, ShipmentResponseDto.class);
        return productResponseDto;

    }

    public ShipmentShipmentResponseDto createShipmentItems(ShipmentShipmentItemDto request) {
        List<ShipmentItemResponseDto> responseDtos = new ArrayList<>();
        validations.validateShipmentAndShipmentItem(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Shipment order = mapper.map(request,Shipment.class);
        ShipmentItem orderItem = mapper.map(request, ShipmentItem.class);

        Shipment shipmentExists = shipmentRepository.findShipmentById(request.getWarehouseId());
        if(shipmentExists != null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "shipment already exist");
        }
        order.setCreatedBy(userCurrent.getId());
        order.setIsActive(true);
        order = shipmentRepository.save(order);
        log.debug("Create new shipment - {}"+ new Gson().toJson(order));
        ShipmentShipmentResponseDto orderResponseDto = mapper.map(order, ShipmentShipmentResponseDto.class);
        log.info("request sent ::::::::::::::::::::::::::::::::: " + request.getShipmentItemDtoList());
        responseDtos = shipmentItemService.createShipmentItems(request.getShipmentItemDtoList());
        List<ShipmentItemResponseDto> finalResponseDtos = responseDtos;
        responseDtos.forEach(orderItemResponseDto -> {
            orderResponseDto.setShipmentItemResponseDtoList(finalResponseDtos);
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
        Shipment state = shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment Id does not exist!"));
        return mapper.map(state,ShipmentResponseDto.class);
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
        List<Shipment> states = shipmentRepository.findByIsActive(isActive);
        return states;

    }

}
