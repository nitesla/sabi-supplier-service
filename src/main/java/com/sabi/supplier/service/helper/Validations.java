package com.sabi.supplier.service.helper;


import com.sabi.framework.exceptions.BadRequestException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.repositories.UserRepository;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.*;
import com.sabi.suppliers.core.models.Manufacturer;
import com.sabi.suppliers.core.models.Product;
import com.sabi.suppliers.core.models.ProductCategory;
import com.sabi.suppliers.core.models.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("All")
@Slf4j
@Service
public class Validations {



    private StateRepository stateRepository;
    private LGARepository lgaRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private ProductCategoryRepository productCategoryRepository;
    private ManufacturerRepository manufacturerRepository;

    public Validations(StateRepository stateRepository, LGARepository lgaRepository, UserRepository userRepository, ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ManufacturerRepository manufacturerRepository) {
        this.stateRepository = stateRepository;
        this.lgaRepository = lgaRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.manufacturerRepository = manufacturerRepository;
    }

    public void validateState(StateDto stateDto) {
        if (stateDto.getName() == null || stateDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
    }




//

    public void validateLGA (LGADto lgaDto){
        if (lgaDto.getName() == null || lgaDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

        State state = stateRepository.findById(lgaDto.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid State id!"));

    }


    public void validateCountry(CountryDto countryDto) {
        if (countryDto.getName() == null || countryDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
        if(countryDto.getCode() == null || countryDto.getCode().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Code cannot be empty");
    }

    public void validateProduct (ProductDto productDto){
        String valName = productDto.getName();
        char valCharName = valName.charAt(0);
        if (Character.isDigit(valCharName)){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name can not start with a number");
        }
        if (productDto.getName() == null || productDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

        ProductCategory productCategory = productCategoryRepository.findById(productDto.getProductCategoryId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid product id!"));
        Manufacturer manufacturer = manufacturerRepository.findById(productDto.getManufacturerId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid manufacturer id!"));
    }

    public void validateProductCategory(ProductCategoryDto productCategoryDto){
        String valName = productCategoryDto.getName();
        char valCharName = valName.charAt(0);
        if (Character.isDigit(valCharName)){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name can not start with a number");
        }
        if (productCategoryDto.getName() == null || productCategoryDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
    }

    public void validateManufacturer(ManufacturerDto manufacturerDto){
        String valName = manufacturerDto.getName();
        char valCharName = valName.charAt(0);
        if (Character.isDigit(valCharName)){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name can not start with a number");
        }
        if (manufacturerDto.getName() == null || manufacturerDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
        if (manufacturerDto.getAddress() == null || manufacturerDto.getAddress().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Address cannot be empty");
        if (manufacturerDto.getEmail() == null || manufacturerDto.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Address cannot be empty");
        if (manufacturerDto.getWebsite() == null || manufacturerDto.getWebsite().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Website cannot be empty");
        State state =  stateRepository.findById(manufacturerDto.getStateID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid State!"));
    }


}


