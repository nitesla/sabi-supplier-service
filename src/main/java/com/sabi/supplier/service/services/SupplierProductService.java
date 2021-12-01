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
import com.sabi.supplier.service.repositories.SupplierProductRepository;
import com.sabi.suppliers.core.dto.request.SupplierProductDto;
import com.sabi.suppliers.core.dto.response.SupplierProductResponseDto;
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
public class SupplierProductService {
    @Autowired
    private SupplierProductRepository supplierProductRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierProductService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    public SupplierProductResponseDto createSupplierProduct(SupplierProductDto request) {
        validations.validateSupplierProduct(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierProduct supplierProduct = mapper.map(request,SupplierProduct.class);
        SupplierProduct supplierProductExist = supplierProductRepository.findSupplierProductById(request.getId());
        if(supplierProductExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier product already exist");
        }
        supplierProduct.setCreatedBy(userCurrent.getId());
        supplierProduct.setIsActive(true);
        supplierProduct = supplierProductRepository.save(supplierProduct);
        log.debug("Create new Supplier - {}"+ new Gson().toJson(supplierProduct));
        return mapper.map(supplierProduct, SupplierProductResponseDto.class);
    }

    public SupplierProductResponseDto updateSupplierProduct(SupplierProductDto request) {
        validations.validateSupplierProduct(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierProduct supplierProduct = supplierProductRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier product Id does not exist!"));
        mapper.map(request, supplierProduct);
        supplierProduct.setUpdatedBy(userCurrent.getId());
        supplierProductRepository.save(supplierProduct);
        log.debug("Supplier record updated - {}"+ new Gson().toJson(supplierProduct));
        return mapper.map(supplierProduct, SupplierProductResponseDto.class);
    }

    public SupplierProductResponseDto findSupplierProduct(Long id){
        SupplierProduct supplier = supplierProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier product Id does not exist!"));
        return mapper.map(supplier,SupplierProductResponseDto.class);
    }

    public Page<SupplierProduct> findAll(Long supplierID, Long productId, PageRequest pageRequest ){
        Page<SupplierProduct> state = supplierProductRepository.findSupplierProducts(supplierID,productId,pageRequest);
        if(state == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return state;
    }

    public void enableDisEnable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierProduct product = supplierProductRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier product Id does not exist!"));
        product.setIsActive(request.isActive());
        product.setUpdatedBy(userCurrent.getId());
        supplierProductRepository.save(product);

    }


    public List<SupplierProduct> getAll(Boolean isActive){
        List<SupplierProduct> states = supplierProductRepository.findByIsActive(isActive);
        return states;

    }
}
