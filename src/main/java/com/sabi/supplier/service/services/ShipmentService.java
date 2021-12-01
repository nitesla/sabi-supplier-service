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
import com.sabi.suppliers.core.dto.request.ShipmentDto;
import com.sabi.suppliers.core.dto.response.ShipmentResponseDto;
import com.sabi.suppliers.core.models.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    @Autowired
//    private WarehouseRepository warehouseRepository;

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
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Shipment shipment = mapper.map(request,Shipment.class);
        Shipment shipmentExists = shipmentRepository.findShipmentById(request.getWarehouseId());
        log.info("Shipment request :::::::::::::::::::: " +shipmentExists);

        if(shipmentExists != null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "shipment item already exist");
        }
        log.info("Shipment request :::::::::::::::::::: " +request);
//        Warehouse savedWarehouse = warehouseRepository.findShipmentById(request.getWarehouseId());
//        if (savedWarehouse == null) {
//            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                    "Requested warehouse Id does not exist!");
//        }
        shipment.setCreatedBy(userCurrent.getId());
        shipment.setIsActive(true);
        shipment = shipmentRepository.save(shipment);
        log.debug("Create new shipment - {}"+ new Gson().toJson(shipment));
        ShipmentResponseDto productResponseDto =  mapper.map(shipment, ShipmentResponseDto.class);
        return productResponseDto;

    }



    /** <summary>
     * shipment update
     * </summary>
     * <remarks>this method is responsible for updating already existing shipment</remarks>
     */

    public ShipmentResponseDto updateShipment(ShipmentDto request) {
//        validations.validateShipment(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Shipment state = shipmentRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested shipment Id does not exist!"));
//        Warehouse savedWarehouse = warehouseRepository.findShipmentById(request.getWarehouseId());
//        if (savedWarehouse == null) {
//            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                    "Requested warehouse Id does not exist!");
//        }
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
