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
import com.sabi.supplier.service.repositories.WareHouseRepository;
import com.sabi.suppliers.core.dto.request.SupplierGoodDto;
import com.sabi.suppliers.core.models.ProductVariant;
import com.sabi.suppliers.core.models.SupplierGood;
import com.sabi.suppliers.core.models.WareHouse;
import com.sabi.suppliers.core.models.response.SupplierGoodResponseDto;
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
    @Autowired
    private ProductVariantRepository variantRepository;
    @Autowired
    private WareHouseRepository wareHouseRepository;
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
        SupplierGood supplierGoodExist = supplierGoodRepository.findByVariantIdAndSupplierId(request.getVariantId(),request.getSupplierId());
        if(supplierGoodExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier goods already exist");
        }
        ProductVariant productVariant = variantRepository.getOne(request.getVariantId());
        supplierGood.setVariantName(productVariant.getName());
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
        ProductVariant productVariant = variantRepository.getOne(supplier.getVariantId());
        supplier.setVariantName(productVariant.getName());
        supplier.setVariantPicture(productVariant.getPicture());
        return mapper.map(supplier,SupplierGoodResponseDto.class);
    }

    public Page<SupplierGood> findAll(Long supplierId, Long variantId,String variantName, PageRequest pageRequest ){
        Page<SupplierGood> supplierGoods = supplierGoodRepository.findSupplierGoods(supplierId,variantId,variantName,pageRequest);
        if(supplierGoods == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        supplierGoods.forEach(supplierGood -> {
            ProductVariant productVariant = variantRepository.getOne(supplierGood.getVariantId());
            supplierGood.setVariantName(productVariant.getName());
            supplierGood.setVariantPicture(productVariant.getPicture());
        });
        return supplierGoods;
    }

    public void enableDisEnable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierGood goods = supplierGoodRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier goods Id does not exist!"));
        goods.setIsActive(request.isActive());
        goods.setUpdatedBy(userCurrent.getId());
        supplierGoodRepository.save(goods);

    }


    public List<SupplierGood> getAll(Boolean isActive,Long supplierId){
        List<SupplierGood> goods = supplierGoodRepository.findByIsActive(isActive,supplierId);
        return goods;

    }
}
