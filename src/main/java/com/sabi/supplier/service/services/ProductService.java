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
import com.sabi.supplier.service.repositories.ManufacturerRepository;
import com.sabi.supplier.service.repositories.ProductCategoryRepository;
import com.sabi.supplier.service.repositories.ProductRepository;
import com.sabi.suppliers.core.dto.request.ProductDto;
import com.sabi.suppliers.core.dto.response.ProductResponseDto;
import com.sabi.suppliers.core.models.Manufacturer;
import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.ProductCategory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public ProductService(ProductRepository productRepository, ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }



    /** <summary>
     * State creation
     * </summary>
     * <remarks>this method is responsible for creation of new product</remarks>
     */

    public ProductResponseDto createProduct(ProductDto request) {
        validations.validateProduct(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Product product = mapper.map(request,Product.class);
        Product productExist = productRepository.findByName(request.getName());
        if(productExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " product already exist");
        }
        product.setCreatedBy(userCurrent.getId());
        product.setIsActive(true);
        product = productRepository.save(product);
        log.debug("Create new product - {}"+ new Gson().toJson(product));
        return mapper.map(product, ProductResponseDto.class);
    }


    /** <summary>
     * State update
     * </summary>
     * <remarks>this method is responsible for updating already existing product</remarks>
     */

    public ProductResponseDto updateProduct(ProductDto request) {
        validations.validateProduct(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product Id does not exist!"));
        mapper.map(request, product);
        product.setUpdatedBy(userCurrent.getId());
        productRepository.save(product);
        log.debug("product record updated - {}"+ new Gson().toJson(product));
        return mapper.map(product, ProductResponseDto.class);
    }


    /** <summary>
     * Find State
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public ProductResponseDto findProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product Id does not exist!"));
       Manufacturer savedManufacturer = manufacturerRepository.findManufacturerById(product.getManufacturerId());
       if (savedManufacturer == null){
           throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"Requested Manufacturer Id does not exist!");
       }
        ProductCategory savedProductCategory = productCategoryRepository.findProductCategoryById(product.getProductCategoryId());
        if (savedProductCategory == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"Requested product category Id does not exist!");
        }
        product.setProductCategoryName(savedProductCategory.getName());
        product.setManufactureName(savedManufacturer.getName());
        return mapper.map(product,ProductResponseDto.class);
    }


    /** <summary>
     * Find all State
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<Product> findAllProduct(String name, Long productCategoryId, PageRequest pageRequest ){
        Page<Product> products = productRepository.findProducts(name, productCategoryId,pageRequest);
        if(products == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        products.forEach(product -> {
            Manufacturer savedManufacturer = manufacturerRepository.findManufacturerById(product.getManufacturerId());
            if (savedManufacturer == null){
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"Requested Manufacturer Id does not exist!");
            }
            ProductCategory savedProductCategory = productCategoryRepository.findProductCategoryById(product.getProductCategoryId());
            if (savedProductCategory == null){
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"Requested product category Id does not exist!");
            }
            product.setProductCategoryName(savedProductCategory.getName());
            product.setManufactureName(savedManufacturer.getName());
        });
        return products;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a product</remarks>
     */
    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product Id does not exist!"));
        product.setIsActive(request.getIsActive());
        product.setUpdatedBy(userCurrent.getId());
        productRepository.save(product);

    }


    public List<Product> getAll(Boolean isActive){
        List<Product> products = productRepository.findByIsActiveOrderByIdDesc(isActive);
        return products;

    }
}
