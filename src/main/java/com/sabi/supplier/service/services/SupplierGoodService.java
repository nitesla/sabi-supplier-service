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
import com.sabi.supplier.service.repositories.ProductVariantRepository;
import com.sabi.supplier.service.repositories.SupplierGoodRepository;
import com.sabi.supplier.service.repositories.SupplierProductRepository;
import com.sabi.suppliers.core.dto.request.SupplierGoodDto;
import com.sabi.suppliers.core.dto.response.SupplierGoodResponseDto;
import com.sabi.suppliers.core.models.ProductVariant;
import com.sabi.suppliers.core.models.SupplierGood;
import com.sabi.suppliers.core.models.SupplierProduct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierGoodService {

    @Autowired
    private SupplierGoodRepository supplierGoodRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierGoodService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    public SupplierGoodResponseDto createSupplierGood(SupplierGoodDto request) {
        validations.validateSupplierGood(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierGood supplierGood = mapper.map(request,SupplierGood.class);
        SupplierGood supplierGoodExist = supplierGoodRepository.findSupplierGoodById(request.getId());
        if(supplierGoodExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier goods already exist");
        }
        supplierGood.setCreatedBy(userCurrent.getId());
        supplierGood.setIsActive(true);
        supplierGood = supplierGoodRepository.save(supplierGood);
        log.debug("Create new Supplier goods - {}"+ new Gson().toJson(supplierGood));
        return mapper.map(supplierGood, SupplierGoodResponseDto.class);
    }

    public SupplierGoodResponseDto updateSupplierGood(SupplierGoodDto request) {
        validations.validateSupplierGood(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierGood supplier = supplierGoodRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier goods Id does not exist!"));
        mapper.map(request, supplier);
        supplier.setUpdatedBy(userCurrent.getId());
        supplierGoodRepository.save(supplier);
        log.debug("Supplier goods record updated - {}"+ new Gson().toJson(supplier));
        return mapper.map(supplier, SupplierGoodResponseDto.class);
    }

    public SupplierGoodResponseDto findSupplierGood(Long id){
        SupplierGood supplier = supplierGoodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier goods Id does not exist!"));
        return mapper.map(supplier,SupplierGoodResponseDto.class);
    }

    public Page<SupplierGood> findAll(Long supplierProductId, Long variantId, PageRequest pageRequest ){
        Page<SupplierGood> supplierGoods = supplierGoodRepository.findSupplierGoods(supplierProductId,variantId,pageRequest);
        if(supplierGoods == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return supplierGoods;
    }

    public void enableDisEnable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierGood state = supplierGoodRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier goods Id does not exist!"));
        state.setIsActive(request.isActive());
        state.setUpdatedBy(userCurrent.getId());
        supplierGoodRepository.save(state);

    }


    public List<SupplierGood> getAll(Boolean isActive){
        List<SupplierGood> states = supplierGoodRepository.findByIsActive(isActive);
        return states;

    }
}