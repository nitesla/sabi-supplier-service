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
import com.sabi.suppliers.core.dto.request.ProductCategoryDto;
import com.sabi.suppliers.core.dto.response.ProductCategoryResponseDto;
import com.sabi.suppliers.core.models.ProductCategory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductCategoryService {

    private ProductCategoryRepository productCategoryRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository, ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.productCategoryRepository = productCategoryRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    /** <summary>
     * State creation
     * </summary>
     * <remarks>this method is responsible for creation of new product category</remarks>
     */

    public ProductCategoryResponseDto createProduct(ProductCategoryDto request) {
        validations.validateProductCategory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductCategory productCategory = mapper.map(request,ProductCategory.class);
        ProductCategory productCategoryExist = productCategoryRepository.findByName(request.getName());
        if(productCategoryExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " product category already exist");
        }
        productCategory.setCreatedBy(userCurrent.getId());
        productCategory.setIsActive(true);
        productCategory = productCategoryRepository.save(productCategory);
        log.debug("Create new product category - {}"+ new Gson().toJson(productCategory));
        return mapper.map(productCategory, ProductCategoryResponseDto.class);
    }


    /** <summary>
     * State update
     * </summary>
     * <remarks>this method is responsible for updating already existing product category</remarks>
     */

    public ProductCategoryResponseDto updateProduct(ProductCategoryDto request) {
        validations.validateProductCategory(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductCategory product = productCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product category Id does not exist!"));
        mapper.map(request, product);
        product.setUpdatedBy(userCurrent.getId());
        productCategoryRepository.save(product);
        log.debug("product category record updated - {}"+ new Gson().toJson(product));
        return mapper.map(product, ProductCategoryResponseDto.class);
    }


    /** <summary>
     * Find State
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public ProductCategoryResponseDto findProduct(Long id){
        ProductCategory product = productCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product category Id does not exist!"));
        return mapper.map(product,ProductCategoryResponseDto.class);
    }


    /** <summary>
     * Find all State
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<ProductCategory> findAllProduct(String name, PageRequest pageRequest ){
        Page<ProductCategory> products = productCategoryRepository.findProductCategories(name,pageRequest);
        if(products == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return products;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a product category</remarks>
     */
    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductCategory productCategory = productCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product category Id does not exist!"));
        productCategory.setIsActive(request.getIsActive());
        productCategory.setUpdatedBy(userCurrent.getId());
        productCategoryRepository.save(productCategory);

    }


    public List<ProductCategory> getAll(Boolean isActive){
        List<ProductCategory> productCategories = productCategoryRepository.findByIsActiveOrderByIdDesc(isActive);
        return productCategories;

    }
}
