package com.sabi.supplier.service.helper;


import com.sabi.framework.exceptions.BadRequestException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.repositories.UserRepository;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.*;
import com.sabi.suppliers.core.models.*;
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
    private final SupplyRequestRepository supplyRequestRepository;
    private final WareHouseRepository wareHouseRepository;
    private final WareHouseUserRepository wareHouseUserRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierCategoryRepository supplierCategoryRepository;



    public Validations(StateRepository stateRepository, LGARepository lgaRepository, UserRepository userRepository, ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ManufacturerRepository manufacturerRepository, SupplyRequestRepository supplyRequestRepository, WareHouseRepository wareHouseRepository, WareHouseUserRepository wareHouseUserRepository) {
        this.stateRepository = stateRepository;
        this.lgaRepository = lgaRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.supplyRequestRepository = supplyRequestRepository;
        this.wareHouseRepository = wareHouseRepository;
        this.wareHouseUserRepository = wareHouseUserRepository;
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


    public void validateWareHouseUser(WareHouseUserRequest request) {
        userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid USER ID!"));
        wareHouseRepository.findById(request.getWareHouseId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Warehouse ID!"));
    }

    public void validateSupplyRequest(SupplyRequestRequest request) {
        productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid PRODUCT ID!"));
        wareHouseRepository.findById(request.getWarehouseId()).orElseThrow(()-> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Warehouse ID!"));

    }

    public void validateSupplyRequestResponse(SupplyRequestResponseRequest request) {
        supplyRequestRepository.findById(request.getSupplyRequestId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supply Request ID!"));
    }

    public void validateWareHouse(WareHouseRequest request) {
        productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Product ID!"));
        supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier ID!"));
        //todo confirm warehouse userId validation
        stateRepository.findById(request.getStateId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid State ID!"));
        userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid USER ID!"));
        lgaRepository.findById(request.getLgaId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid LGA ID!"));
    }

    public void validatesupplierBank(SupplierBankRequest request) {
        supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier ID!"));
    }
}


