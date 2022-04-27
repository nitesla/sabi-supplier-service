package com.sabi.supplier.service.helper;


import com.sabi.framework.dto.requestDto.ChangePasswordDto;
import com.sabi.framework.exceptions.BadRequestException;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.Role;
import com.sabi.framework.models.User;
import com.sabi.framework.repositories.RoleRepository;
import com.sabi.framework.repositories.UserRepository;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.*;
import com.sabi.suppliers.core.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private  SupplyRequestRepository supplyRequestRepository;
    private  WareHouseRepository wareHouseRepository;
    private  WareHouseUserRepository wareHouseUserRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierCategoryRepository supplierCategoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private SupplierProductRepository supplierProductRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierGoodRepository supplierGoodRepository;

    @Autowired
    private WareHouseGoodRepository wareHouseGoodRepository;






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

//    public void validateLGA (LGADto lgaDto){
//        if (lgaDto.getName() == null || lgaDto.getName().isEmpty())
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
//
//        State state = stateRepository.findById(lgaDto.getId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid State id!"));
//
//    }


//    public void validateCountry(CountryDto countryDto) {
//        if (countryDto.getName() == null || countryDto.getName().isEmpty())
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
//        if(countryDto.getCode() == null || countryDto.getCode().isEmpty())
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Code cannot be empty");
//    }

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
                        " Enter a valid product category id!"));
        Manufacturer manufacturer = manufacturerRepository.findById(productDto.getManufacturerId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid manufacturer id!"));
    }

    public void validateProductSuggestion (ProductSuggestionRequestDto productSuggestionRequestDto){
        String valName = productSuggestionRequestDto.getName();
        char valCharName = valName.charAt(0);
        if (Character.isDigit(valCharName)){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name can not start with a number");
        }
        if (productSuggestionRequestDto.getName() == null || productSuggestionRequestDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
        if (productSuggestionRequestDto.getDescription() == null || productSuggestionRequestDto.getDescription().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Description cannot be empty");
        if (!("Approved".equalsIgnoreCase(productSuggestionRequestDto.getStatus()) || "Declined".equalsIgnoreCase(productSuggestionRequestDto.getStatus())))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Enter the correct product suggestion Status");
    }

    public void validateSupplyRequestCounterOffer (SupplyRequestCounterOfferRequestDto counterOfferRequestDto){
        if (counterOfferRequestDto.getSupplyRequestId() == null || counterOfferRequestDto.getSupplyRequestId() <= 0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Supply request Id can not be less than 1");
        SupplyRequest supplyRequest = supplyRequestRepository.findById(counterOfferRequestDto.getSupplyRequestId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid supply Request id!"));
        if (counterOfferRequestDto.getPrice() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Price cannot be empty");
        if (counterOfferRequestDto.getQuantity() == null || counterOfferRequestDto.getQuantity() <= 0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity cannot be empty");

    }

    public void validateSupplyRequestCounterOfferUpdate (SupplyRequestCounterOfferRequestDto counterOfferRequestDto){
        if (counterOfferRequestDto.getSupplyRequestId() != null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Supply request Id can not be updated");
        if (counterOfferRequestDto.getPrice() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Price cannot be empty");
        if (counterOfferRequestDto.getQuantity() == null || counterOfferRequestDto.getQuantity() <= 0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity cannot be empty");
        if (counterOfferRequestDto.getUserId() != null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION,"User Id can not be updated");

        }

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

//        if (request.getIsActive() == null )
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "IsActive cannot be empty");

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

//        State state =  stateRepository.findById(request.getStateID())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid State!")
//                );

        Supplier supplier = supplierRepository.findById(request.getSupplierID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid Supplier!"));

    }

    public void validateSupplier(SupplierRequestDto request) {

        if (request.getName() == null || request.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");
        if (!Utility.validateName(request.getName().toString()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for Name ");

//        if (request.getStateId() == null )
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "StateID cannot be empty");
//        if (!Utility.isNumeric(request.getStateID().toString()))
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for StateID");

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



//
//        State state =  stateRepository.findById(request.getStateID())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid State!")
//                );

        SupplierCategory supplierCategory = supplierCategoryRepository.findById(request.getSupplierCategoryID())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid Supplier Category ID!"));

    }

    public void validateSupplierGood(SupplierGoodDto supplierGoodDto) {
        if (supplierGoodDto.getPrice() <= 0.0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Price cannot be Less that 0.0");
        Supplier supplier = supplierRepository.findById(supplierGoodDto.getSupplierId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid supplier id!"));
        ProductVariant variant = productVariantRepository.findById(supplierGoodDto.getVariantId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested product variant Id does not exist!"));
    }

        public void validateSupplierProduct(SupplierProductDto supplierProductDto){
            Product product = productRepository.findById(supplierProductDto.getProductId())
                    .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                            " Enter a valid product id!"));
            Supplier supplier = supplierRepository.findById(supplierProductDto.getSupplierId())
                    .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                            " Enter a valid supplier id!"));
        }

    public void validateProductVariant(ProductVariantDto productVariantDto){
        Product product = productRepository.findById(productVariantDto.getProductId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid product id!"));
        String valName = productVariantDto.getName();
        char valCharName = valName.charAt(0);
        if (Character.isDigit(valCharName)){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name can not start with a number");
        }
        if (productVariantDto.getName() == null || productVariantDto.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

        if (productVariantDto.getPicture() == null || productVariantDto.getPicture().isEmpty())
        throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "picture cannot be empty");
        if (productVariantDto.getPieceaPerRow() <= 0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Piece per row cannot be empty");
        if (productVariantDto.getRowPerPack() <= 0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "row per pack cannot be empty");
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
        if (request.getSupplierId() != null){
            supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    " Enter a valid Supplier ID!"));
         List<WareHouse> savedWarehouse = wareHouseRepository.findBySupplierId(request.getSupplierId());
//         savedWarehouse.forEach(wareHouse -> {
//             wareHouseRepository.findById(wareHouse.getId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                     " Enter a valid Warehouse ID!"));
//         });
            if (savedWarehouse .isEmpty()){
                throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                     " Enter a valid Warehouse ID!");
            }
        }
//        if (request.getWarehouseId() != null) {
//            wareHouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                    " Enter a valid Warehouse ID!"));
//        }
        if (request.getStatus() == null || request.getStatus().isEmpty() )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Delivery Status cannot be empty");
        if (!("Awaiting_Shippment".equalsIgnoreCase(request.getStatus()) || "Shipped".equalsIgnoreCase(request.getStatus()) || "Cancelled".equalsIgnoreCase(request.getStatus()) ||"Opened".equalsIgnoreCase(request.getStatus())||"Accepted".equalsIgnoreCase(request.getStatus()) ||"Pending".equalsIgnoreCase(request.getStatus()) ||"Rejected".equalsIgnoreCase(request.getStatus())))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Enter the correct Delivery Status");

    }

    public void validateSupplyRequestResponse(SupplyRequestResponseRequest request) {
        supplyRequestRepository.findById(request.getSupplyRequestId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supply Request ID!"));
    }

    public void validateWareHouse(WareHouseRequest request) {
        if (request.getProductId() != null) {
            productRepository.findById(request.getProductId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    " Enter a valid Product ID!"));
        }
        supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier ID!"));
        //todo confirm warehouse userId validation
//        stateRepository.findById(request.getStateId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                " Enter a valid State ID!"));
        userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid USER ID!"));
//        lgaRepository.findById(request.getLgaId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                " Enter a valid LGA ID!"));
    }


    public void validateSupplierUser(SupplierUserDto request){
        if (request.getFirstName() == null || request.getFirstName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "First name cannot be empty");
        if (request.getFirstName().length() < 2 || request.getFirstName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid first name  length");

        if (request.getLastName() == null || request.getLastName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Last name cannot be empty");
        if (request.getLastName().length() < 2 || request.getLastName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid last name  length");

        if (request.getEmail() == null || request.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "email cannot be empty");
        if (!Utility.validEmail(request.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        User user = userRepository.findByEmail(request.getEmail());
        if(user !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " Email already exist");
        }
        if (request.getPhone() == null || request.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (request.getPhone().length() < 8 || request.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(request.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");
        User userExist = userRepository.findByPhone(request.getPhone());
        if(userExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "  user phone already exist");
        }
        if(request.getRoleId() == null){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Role id cannot be empty");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid role id!"));
    }


    public void validateSupplier(SupplierSignUpRequestDto request){
        if (request.getFirstName() == null || request.getFirstName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "First name cannot be empty");
        if (request.getFirstName().length() < 2 || request.getFirstName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid first name  length");
        if (request.getLastName() == null || request.getLastName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Last name cannot be empty");
        if (request.getLastName().length() < 2 || request.getLastName().length() > 100)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid last name  length");

        if (request.getEmail() == null || request.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "email cannot be empty");
        if (!Utility.validEmail(request.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        if (request.getPhone() == null || request.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (request.getPhone().length() < 8 || request.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(request.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");
        if (request.getName() == null || request.getName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name cannot be empty");

        if (request.getPassword() == null || request.getPassword().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "password cannot be empty");
        if (request.getPassword().length() < 6 || request.getPassword().length() > 20)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid password length");
    }



    public void validateCompleteSignUp(CompleteSignUpDto request){
        if(request.getDeliveryType() == null || request.getDeliveryType().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Delivery type cannot be empty");

        if (request.getDeliveryType() != null || !request.getDeliveryType().isEmpty()) {

            if (!SupplierConstant.ME.equals(request.getDeliveryType())
                    && !SupplierConstant.SABI.equals(request.getDeliveryType()) && !SupplierConstant.MY_PARTNER.equals(request.getDeliveryType()))
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid User category type");
        }

        if(request.getStateId() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "State cannot be empty");

//        State state =  stateRepository.findById(request.getStateId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid State!")
//                );

        if(request.getLgaId() == null )
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "LGA cannot be empty");
//        LGA lga =  lgaRepository.findById(request.getLgaId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid Lga!")
//                );

        if(request.getEmail() == null || request.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
        if (!Utility.validEmail(request.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        if (request.getPhone() == null || request.getPhone().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phone number cannot be empty");
        if (request.getPhone().length() < 8 || request.getPhone().length() > 14)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid phone number  length");
        if (!Utility.isNumeric(request.getPhone()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid data type for phone number ");

    }



    public void validateSupplierUserActivation (SupplierUserActivation request){
        if (request.getEmail() == null || request.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
        if (!Utility.validEmail(request.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        if(request.getActivationUrl()== null || request.getActivationUrl().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Activation url cannot be empty");

    }



    public void validateSupplierPasswordActivation (ChangePasswordDto request){
        if (request.getPassword() == null || request.getPassword().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Password cannot be empty");
        if (request.getPassword().length() < 6 || request.getPassword().length() > 20)// NAME LENGTH*********
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid password length");

    }

    public void validateShipmentItem(ShipmentItemDto request) {
        supplyRequestRepository.findById(request.getSupplierRequestId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid supplier Request ID!"));
        if (request.getShipmentId() != null) {
            shipmentRepository.findById(request.getShipmentId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    " Enter a valid shipment ID!"));
        }
        if (request.getAcceptedQuality() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " accepted quantity can not be empty");
        if (request.getDeliveryDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "delivery date can not be empty");
        if (request.getPrice() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "price can not be empty");
        if (request.getQuantity() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
//        if (request.getStatus() == null || request.getStatus().isEmpty())
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
    }

    public void validateShipment(ShipmentDto shipmentDto) {
        WareHouse wareHouse = wareHouseRepository.findWareHouseById(shipmentDto.getWarehouseId());
        if (wareHouse == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"Enter a valid warehouse id!");
        }
        if (shipmentDto.getDeliveryDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Delivery date can not be empty");
        if (shipmentDto.getExpectedDeliveryDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "expected date can not be empty");
        if (shipmentDto.getLogisticPartnerId() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "end date can not be empty");
        if (shipmentDto.getLogisticPartnerName() == null || shipmentDto.getLogisticPartnerName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "logistic partner name can not be empty");
        if (shipmentDto.getPhoneNumber() == null || shipmentDto.getPhoneNumber().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phonenumber can not be empty");
        if (shipmentDto.getQuantity() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
        if (shipmentDto.getTotalAmount() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "total amount not be empty");
        if (shipmentDto.getVehicle() == null || shipmentDto.getVehicle().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "vehicle can not be empty");
        if (shipmentDto.getStatus() == null || shipmentDto.getStatus().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
        if (!("Awaiting_Shipment".equalsIgnoreCase(shipmentDto.getStatus()) || "Shipped".equalsIgnoreCase(shipmentDto.getStatus()) ||"Delivered".equalsIgnoreCase(shipmentDto.getStatus())))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Enter the correct Shipment Status");
        if (shipmentDto.getTotalAmount() == null || shipmentDto.getStatus().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
        if (shipmentDto.getFeedStatus() == null || shipmentDto.getFeedStatus().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Feed status can not be empty");
        if (!("Pending".equalsIgnoreCase(shipmentDto.getFeedStatus()) || "Sent".equalsIgnoreCase(shipmentDto.getFeedStatus()) ||"Failed".equalsIgnoreCase(shipmentDto.getFeedStatus())))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Enter the correct Feed Status");
        if (!("Paid".equalsIgnoreCase(shipmentDto.getPaymentStatus()) || "Unpaid".equalsIgnoreCase(shipmentDto.getPaymentStatus())))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Enter the correct Payment Status");
    }

    public void validateStock(StockDto request) {
        wareHouseGoodRepository.findById(request.getWareHouseGoodId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid warehousr good ID!"));
        userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid shipment ID!"));
        if (request.getActionDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " action date can not be empty");
        if (request.getAction() == null || request.getAction().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "action can not be empty");
        if (request.getInitialQuantity() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Initia quantity not be empty");
        if (request.getQuantity() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
        if (request.getFinalQuantity() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "final quantity can not be empty");
    }

    public void validateWarehouseGood(WareHouseGoodDto request) {
        wareHouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Warehouse ID!"));
    supplierGoodRepository.findById(request.getSupplierGoodId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier Goods ID!"));
//    if (request.getQty() < 1){
//        throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
//    }
    if (request.getQtyAvaliable() < 1){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity avaliable  can not be empty");
    }
//        if (request.getQtySold() < 1){
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity sold can not be empty");
//        }
        if (request.getPrice() < 1){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "price can not be empty");
        }
    }

    public void validateInventory(InventoryDto request) {
        wareHouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Warehouse ID!"));
        supplierGoodRepository.findById(request.getSupplierGoodId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier Goods ID!"));
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().isEmpty()){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "delivery address can not be empty");
        }
        if (request.getName() == null || request.getName().isEmpty()){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "name can not be empty");
        }
        String valName = request.getName();
        char valCharName = valName.charAt(0);
        if (Character.isDigit(valCharName)){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Name can not start with a number");
        }
        if (request.getStatus() == null || request.getStatus().isEmpty()){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
        }
    }

    public void validateShipmentAndShipmentItem(ShipmentShipmentItemDto request){

        WareHouse wareHouse = wareHouseRepository.findWareHouseById(request.getWarehouseId());
        if (wareHouse == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,"Enter a valid warehouse id!");
        }
        if (request.getDeliveryDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Delivery date can not be empty");
        if (request.getExpectedDeliveryDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "expected date can not be empty");
        if (request.getLogisticPartnerId() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "end date can not be empty");
        if (request.getLogisticPartnerName() == null || request.getLogisticPartnerName().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "logistic partner name can not be empty");
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Phonenumber can not be empty");
        if (request.getQuantity() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
        if (request.getTotalAmount() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "total amount not be empty");
        if (request.getVehicle() == null || request.getVehicle().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "vehicle can not be empty");
        if (request.getFeedStatus() == null || request.getFeedStatus().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
        if (!("Pending".equalsIgnoreCase(request.getFeedStatus()) || "Sent".equalsIgnoreCase(request.getFeedStatus()) ||"Failed".equalsIgnoreCase(request.getFeedStatus())));
//        if (request.getStatus() == null || request.getStatus().isEmpty())
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
//        if (request.getTotalAmount() == null || request.getStatus().isEmpty())
//            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
//        request.getShipmentItemDtoList().forEach(shipmentItemDto -> {
//            supplierRepository.findById(shipmentItemDto.getSupplierRequestId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                    " Enter a valid supplier ID!"));
//            shipmentRepository.findById(shipmentItemDto.getShipmentId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                    " Enter a valid shipment ID!"));
//            if (shipmentItemDto.getAcceptedQuality() < 1)
//                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " accepted quantity can not be empty");
//            if (shipmentItemDto.getDeliveryDate() == null)
//                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "delivery date can not be empty");
//            if (shipmentItemDto.getPrice() == null)
//                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "price can not be empty");
//            if (shipmentItemDto.getQuantity() < 1)
//                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
//            if (shipmentItemDto.getStatus() == null || shipmentItemDto.getStatus().isEmpty())
//                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
//        });
    }


    public void validatesupplierBank(SupplierBankRequest request) {
        supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier ID!"));
    }
}


