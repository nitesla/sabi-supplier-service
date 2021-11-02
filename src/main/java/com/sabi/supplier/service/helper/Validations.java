package com.sabi.supplier.service.helper;


import com.sabi.framework.exceptions.BadRequestException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.repositories.UserRepository;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.*;
import com.sabi.suppliers.core.models.State;
import com.sabi.suppliers.core.models.Supplier;
import com.sabi.suppliers.core.models.SupplierCategory;
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

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierCategoryRepository supplierCategoryRepository;



    public Validations(StateRepository stateRepository, LGARepository lgaRepository, UserRepository userRepository) {
        this.stateRepository = stateRepository;
        this.lgaRepository = lgaRepository;
        this.userRepository = userRepository;

    }

    public void validateState(StateDto stateDto) {
        if (stateDto.getName() == null || stateDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
    }

    public void validateSupplierCategory(SupplierCategoryRequestDto request) {

        if (request.getName() == null || request.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
        if (!Utility.validateName(request.getName().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Name ");

        if (request.getCreditPeriod() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Credit Period cannot be empty");
        if (!Utility.isNumeric(request.getCreditPeriod().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Credit Period");
        if (request.getCreditPeriod() < 1){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid request");
        }

        if (request.getIsActive() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "IsActive cannot be empty");

    }

    public void validateSupplierLocation(SupplierLocationRequestDto request) {

        if (request.getSupplierID() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "SupplierID cannot be empty");
        if (!Utility.isNumeric(request.getSupplierID().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for SupplierID ");

        if (request.getStateID() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "StateID cannot be empty");
        if (!Utility.isNumeric(request.getStateID().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for StateID");

        State state =  stateRepository.findById(request.getStateID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid State!")
        );

        Supplier supplier = supplierRepository.findById(request.getSupplierID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid Supplier!"));

    }

    public void validateSupplier(SupplierRequestDto request) {

        if (request.getName() == null || request.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
        if (!Utility.validateName(request.getName().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Name ");

        if (request.getStateID() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "StateID cannot be empty");
        if (!Utility.isNumeric(request.getStateID().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for StateID");

        if (request.getAddress() == null || request.getAddress().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Address cannot be empty");

        if (request.getPhone() == null || request.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone cannot be empty");
        if (!Utility.validatePhoneNumber(request.getPhone().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Phone ");

        if (request.getEmail() == null || request.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
        if (!Utility.validEmail(request.getEmail().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Email ");

        if (request.getWebsite() == null || request.getWebsite().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Website cannot be empty");

        if (request.getSupplierCategoryID() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "SupplierCategoryID cannot be empty");
        if (!Utility.isNumeric(request.getSupplierCategoryID().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for SupplierCategoryID");

        if (request.getContactPerson() == null || request.getContactPerson().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Contact Person cannot be empty");
        if (!Utility.validateName(request.getContactPerson().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Contact Person ");

        if (request.getContactPhone() == null || request.getContactPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Contact Phone cannot be empty");
        if (!Utility.validatePhoneNumber(request.getContactPhone().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Contact Phone ");

        if (request.getContactEmail() == null || request.getContactEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Contact Email cannot be empty");
        if (!Utility.validEmail(request.getContactEmail().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Contact Email ");

        if (request.getDiscountProvided() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Discount Provided cannot be empty");
        if (!Utility.isNumeric(request.getDiscountProvided().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Discount Provided");

        if (request.getIsActive() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "IsActive cannot be empty");



        State state =  stateRepository.findById(request.getStateID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid State!")
                );

        SupplierCategory supplierCategory = supplierCategoryRepository.findById(request.getSupplierCategoryID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid Supplier Category ID!"));

    }


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
        if (productDto.getName() == null || productDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

//        Product product = productRepository.findById(productDto.getId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid product id!"));
//        Manufacturer manufacturer = productRepository.findById(productDto.getManufacturerId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid manufacturer id!"));
//        ProductCategory productCategory = productRepository.findById(productDto.getProductCategoryId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid product id!"));
    }

    public void validateProductCategory(ProductCategoryDto productCategoryDto){
        if (productCategoryDto.getName() == null || productCategoryDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
    }
}


