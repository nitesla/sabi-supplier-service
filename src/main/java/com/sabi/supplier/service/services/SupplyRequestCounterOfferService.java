package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.SupplyRequestCounterOfferRepository;
import com.sabi.supplier.service.repositories.SupplyRequestRepository;
import com.sabi.suppliers.core.dto.request.SupplyRequestCounterOfferRequestDto;
import com.sabi.suppliers.core.dto.response.SupplyRequestCounterOfferResponseDto;
import com.sabi.suppliers.core.models.SupplyRequest;
import com.sabi.suppliers.core.models.SupplyRequestCounterOffer;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class SupplyRequestCounterOfferService {

    @Autowired
    private SupplyRequestCounterOfferRepository repository;
    @Autowired
    private SupplyRequestRepository supplyRequestRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplyRequestCounterOfferService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    public SupplyRequestCounterOfferResponseDto createSupplyRequestCounterOffer(SupplyRequestCounterOfferRequestDto request) {
        validations.validateSupplyRequestCounterOffer(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequestCounterOffer supplyRequestCounterOffer = mapper.map(request,SupplyRequestCounterOffer.class);
        supplyRequestCounterOffer.setCreatedBy(userCurrent.getId());
        supplyRequestCounterOffer.setUserId(userCurrent.getId());
        supplyRequestCounterOffer.setIsActive(true);
        SupplyRequest savedSupplyRequest = supplyRequestRepository.findSupplyRequestById(supplyRequestCounterOffer.getSupplyRequestId());
        if (savedSupplyRequest == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested Supply request Id does not exist!");
        }
        supplyRequestCounterOffer.setProductName(savedSupplyRequest.getProductName());
        supplyRequestCounterOffer.setAskingPrice(savedSupplyRequest.getAskingPrice());
        supplyRequestCounterOffer.setSupplierName(savedSupplyRequest.getSupplierName());
        supplyRequestCounterOffer = repository.save(supplyRequestCounterOffer);
        log.debug("Create new product suggestion - {}"+ new Gson().toJson(supplyRequestCounterOffer));
        return mapper.map(supplyRequestCounterOffer, SupplyRequestCounterOfferResponseDto.class);
    }



    public SupplyRequestCounterOfferResponseDto updateSupplyRequestCounterOffer(SupplyRequestCounterOfferRequestDto request) {
        validations.validateSupplyRequestCounterOfferUpdate(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequestCounterOffer supplyRequestCounterOffer = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product suggestion Id does not exist!"));
        mapper.map(request, supplyRequestCounterOffer);
        supplyRequestCounterOffer.setUpdatedBy(userCurrent.getId());
        repository.save(supplyRequestCounterOffer);
        log.debug("product record updated - {}"+ new Gson().toJson(supplyRequestCounterOffer));
        return mapper.map(supplyRequestCounterOffer, SupplyRequestCounterOfferResponseDto.class);
    }


    public SupplyRequestCounterOfferResponseDto findSupplyRequestCounterOfferById(Long id){
        SupplyRequestCounterOffer supplyRequestCounterOffer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supply request counter offer Id does not exist!"));
        SupplyRequest savedSupplyRequest = supplyRequestRepository.findSupplyRequestById(supplyRequestCounterOffer.getSupplyRequestId());
        if (savedSupplyRequest == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested Supply request Id does not exist!");
        }
        supplyRequestCounterOffer.setProductName(savedSupplyRequest.getProductName());
        supplyRequestCounterOffer.setAskingPrice(savedSupplyRequest.getAskingPrice());
        supplyRequestCounterOffer.setSupplierName(savedSupplyRequest.getSupplierName());
        return mapper.map(supplyRequestCounterOffer,SupplyRequestCounterOfferResponseDto.class);
    }


    public Page<SupplyRequestCounterOffer> findAllSupplyRequestCounterOffer(BigDecimal price, Long supplierRequestId, Integer quantity, Long userId, PageRequest pageRequest ){
        Page<SupplyRequestCounterOffer> supplyRequestCounterOffers = repository.findSupplyRequestCounterOffer(price,supplierRequestId, quantity,userId,pageRequest);
        if(supplyRequestCounterOffers == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        supplyRequestCounterOffers.forEach(counterOffer ->{
            SupplyRequest savedSupplyRequest = supplyRequestRepository.findSupplyRequestById(counterOffer.getSupplyRequestId());
            if (savedSupplyRequest == null){
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supply request Id does not exist!");
            }
            counterOffer.setProductName(savedSupplyRequest.getProductName());
            counterOffer.setAskingPrice(savedSupplyRequest.getAskingPrice());
            counterOffer.setSupplierName(savedSupplyRequest.getSupplierName());
        });
        return supplyRequestCounterOffers;

    }


    public void enableDisEnable(EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplyRequestCounterOffer supplyRequestCounterOffers = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product suggestion Id does not exist!"));
        supplyRequestCounterOffers.setIsActive(request.isActive());
        supplyRequestCounterOffers.setUpdatedBy(userCurrent.getId());
        repository.save(supplyRequestCounterOffers);

    }

    public List<SupplyRequestCounterOffer> getAll(Boolean isActive){
        List<SupplyRequestCounterOffer> counterOffers = repository.findByIsActiveOrderByIdDesc(isActive);
        return counterOffers;
    }
}
