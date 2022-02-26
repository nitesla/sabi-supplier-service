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
import com.sabi.supplier.service.repositories.ProductCategoryRepository;
import com.sabi.supplier.service.repositories.ProductRepository;
import com.sabi.supplier.service.repositories.ProductVariantRepository;
import com.sabi.suppliers.core.dto.request.ProductVariantDto;
import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.ProductCategory;
import com.sabi.suppliers.core.models.ProductVariant;
import com.sabi.suppliers.core.models.response.ProductVariantResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public ProductVariantService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    @Autowired
   private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public ProductVariantResponseDto createProductVariant(ProductVariantDto request) {
        validations.validateProductVariant(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductVariant productVariant = mapper.map(request,ProductVariant.class);
        ProductVariant productVariantExist = productVariantRepository.findByName(request.getName());
        if(productVariantExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Supplier Category already exist");
        }
        Product savedProduct = productRepository.findProductById(request.getProductId());
        if (savedProduct == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Product of this variant not found");
        }
        ProductCategory savedProductCategory = productCategoryRepository.findProductCategoryById(savedProduct.getProductCategoryId());
        if (savedProductCategory == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Product Category associated with this variant not found");
        }
        productVariant.setCreatedBy(userCurrent.getId());
        productVariant.setIsActive(true);
        productVariant.setProductName(savedProduct.getName());
        productVariant.setProductCategory(savedProductCategory.getName());
        productVariant = productVariantRepository.save(productVariant);
        log.debug("Create new product variant - {}"+ new Gson().toJson(productVariant));
        return mapper.map(productVariant, ProductVariantResponseDto.class);
    }


    public ProductVariantResponseDto updateProductVariant(ProductVariantDto request) {
        validations.validateProductVariant(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductVariant productVariant = productVariantRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product variant Id does not exist!"));
        mapper.map(request, productVariant);
        productVariant.setUpdatedBy(userCurrent.getId());
        productVariantRepository.save(productVariant);
        log.debug("Supplier product variant record updated - {}"+ new Gson().toJson(productVariant));
        return mapper.map(productVariant, ProductVariantResponseDto.class);
    }

    public ProductVariantResponseDto findProductVariantById(Long id){
        ProductVariant supplierCategory = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product variable Id does not exist!"));
        return mapper.map(supplierCategory,ProductVariantResponseDto.class);
    }

    public Page<ProductVariant> findAll(String name,Long productId, String picture, Integer rowPerPack, Integer pieceaPerRow,  PageRequest pageRequest ){
        Page<ProductVariant> productVariant = productVariantRepository.findProductVariant(name,productId, picture,rowPerPack,pieceaPerRow,pageRequest);
        if(productVariant == null || productVariant.isEmpty()){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return productVariant;
    }

    public void enableDisEnable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductVariant product = productVariantRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product variable Id does not exist!"));
        product.setIsActive(request.isActive());
        product.setUpdatedBy(userCurrent.getId());
        productVariantRepository.save(product);

    }


    public List<ProductVariant> getAll(Boolean isActive){
        List<ProductVariant> productVariants = productVariantRepository.findByIsActiveOrderByIdDesc(isActive);
        return productVariants;

    }

}
