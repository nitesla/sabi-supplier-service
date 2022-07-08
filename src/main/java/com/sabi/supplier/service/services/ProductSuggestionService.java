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
import com.sabi.supplier.service.repositories.ProductSuggestionRepository;
import com.sabi.suppliers.core.dto.request.ProductSuggestionRequestDto;
import com.sabi.suppliers.core.dto.response.ProductSuggestionResponseDto;
import com.sabi.suppliers.core.models.ProductSuggestion;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductSuggestionService {

    @Autowired
    private ProductSuggestionRepository productSuggestionRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public ProductSuggestionService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
    }

    /** <summary>
     * product suggestion creation
     * </summary>
     * <remarks>this method is responsible for creation of new product suggestion</remarks>
     */

    public ProductSuggestionResponseDto createProductSuggestion(ProductSuggestionRequestDto request) {
        validations.validateProductSuggestion(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductSuggestion productSuggestion = mapper.map(request,ProductSuggestion.class);
        ProductSuggestion productSuggestionExist = productSuggestionRepository.findByName(request.getName());
        if(productSuggestionExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " product suggestion already exist");
        }
        productSuggestion.setCreatedBy(userCurrent.getId());
        productSuggestion.setIsActive(true);
        productSuggestion = productSuggestionRepository.save(productSuggestion);
        log.debug("Create new product suggestion - {}"+ new Gson().toJson(productSuggestion));
        return mapper.map(productSuggestion, ProductSuggestionResponseDto.class);
    }


    /** <summary>
     * Product suggestion update
     * </summary>
     * <remarks>this method is responsible for updating already existing product suggestion</remarks>
     */

    public ProductSuggestionResponseDto updateProductSuggestion(ProductSuggestionRequestDto request) {
        validations.validateProductSuggestion(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductSuggestion productSuggestion = productSuggestionRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product suggestion Id does not exist!"));
        mapper.map(request, productSuggestion);
        productSuggestion.setUpdatedBy(userCurrent.getId());
        productSuggestionRepository.save(productSuggestion);
        log.debug("product record updated - {}"+ new Gson().toJson(productSuggestion));
        return mapper.map(productSuggestion, ProductSuggestionResponseDto.class);
    }


    /** <summary>
     * Find Product suggestion
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public ProductSuggestionResponseDto findProductSuggestion(Long id){
        ProductSuggestion productSuggestion = productSuggestionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product suggestion Id does not exist!"));
        return mapper.map(productSuggestion,ProductSuggestionResponseDto.class);
    }


    /** <summary>
     * Find all Product suggestion
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<ProductSuggestion> findAllProductSuggestions(String name,String manufacturer,String status, PageRequest pageRequest ){
        Page<ProductSuggestion> productSuggestion = productSuggestionRepository.findProductSuggestions(name,manufacturer, status,pageRequest);
        if(productSuggestion == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return productSuggestion;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a product suggestion</remarks>
     */
    public void enableDisEnableState (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        ProductSuggestion productSuggestion = productSuggestionRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product suggestion Id does not exist!"));
        productSuggestion.setIsActive(request.getIsActive());
        productSuggestion.setUpdatedBy(userCurrent.getId());
        productSuggestionRepository.save(productSuggestion);

    }
}
