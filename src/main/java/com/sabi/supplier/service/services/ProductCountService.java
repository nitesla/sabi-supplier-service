package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.ProductCountRepository;
import com.sabi.suppliers.core.dto.request.InventoryDto;
import com.sabi.suppliers.core.dto.request.ProductCountDto;
import com.sabi.suppliers.core.models.*;
import com.sabi.suppliers.core.models.response.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductCountService {

    private final ModelMapper mapper;

    @Autowired
    private ProductCountRepository repository;

    public ProductCountService(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public List<ProductCountResponse> createProductCount(List<ProductCount> productCountList) {
        List<ProductCountResponse> responseDtos = new ArrayList<>();
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        productCountList.forEach(request->{
            int quantity = 0;
            ProductCount productCount = mapper.map(request,ProductCount.class);
            quantity += 1;
            productCount.setQuantity(Long.valueOf(quantity));
            productCount.setCreatedBy(userCurrent.getId());
            productCount.setIsActive(true);
            ProductCount savedProductCounts = this.repository.findProductCountByNameAndShipmentId(productCount.getName(), productCount.getShipmentId());
            if (savedProductCounts != null){
                return;
            } else {

                productCount = repository.save(productCount);
                quantity = repository.countAllByNameAndShipmentId(productCount.getName(),productCount.getShipmentId());
                log.info("Quantity :::::::::::::::::::: {} " + quantity);
                productCount.setQuantity((long) quantity);
                updateProductCount(productCount);
            }
            log.debug("Create new asset picture - {}"+ new Gson().toJson(productCount));
            responseDtos.add(mapper.map(productCount, ProductCountResponse.class));
        });
        return responseDtos;
    }

    public ProductCountResponse updateProductCount(ProductCount request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductCount productCount = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Product count Id does not exist!"));
//        mapper.map(request, productCount);
        productCount.setUpdatedBy(userCurrent.getId());
        repository.save(request);
        log.debug("Product count record updated - {}"+ new Gson().toJson(productCount));
        return mapper.map(productCount, ProductCountResponse.class);
    }

    public ProductResponseDto findProductCountById(Long id){
        ProductCount savedProductCount = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product variable Id does not exist!"));
        return mapper.map(savedProductCount,ProductResponseDto.class);
    }

    public ProductCount getAll(String name, Long supplierId){
        ProductCount count = repository.findProductCountByNameAndShipmentId(name,supplierId);
        return count;
    }
}
