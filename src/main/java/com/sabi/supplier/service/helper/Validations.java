package com.sabi.supplier.service.helper;


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

    public void validateSupplierGood(SupplierGoodDto supplierGoodDto) {
        if (supplierGoodDto.getPrice() <= 0.0)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Price cannot be Less that 0.0");
        SupplierProduct supplierProduct = supplierProductRepository.findById(supplierGoodDto.getSupplierProductId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid supplier product id!"));
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
    }



    public void validateCompleteSignUp(CompleteSignUpDto request){
        if(request.getDeliveryType() == null || request.getDeliveryType().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Delivery type cannot be empty");

        if (request.getDeliveryType() != null || !request.getDeliveryType().isEmpty()) {

            if (!SupplierConstant.ME.equals(request.getDeliveryType())
                    && !SupplierConstant.SABI.equals(request.getDeliveryType()) && !SupplierConstant.MY_PARTNER.equals(request.getDeliveryType()))
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid User category type");
        }
    }



    public void validateSupplierUserActivation (SupplierUserActivation request){
        if (request.getEmail() == null || request.getEmail().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Email cannot be empty");
        if (!Utility.validEmail(request.getEmail().trim()))
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Invalid Email Address");
        if(request.getActivationUrl()== null || request.getActivationUrl().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "Activation url cannot be empty");

    }

    public void validateShipmentItem(ShipmentItemDto request) {
        supplierRepository.findById(request.getSupplierRequestId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid supplier ID!"));
        shipmentRepository.findById(request.getShipmentId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid shipment ID!"));
        if (request.getAcceptedQuality() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " accepted quantity can not be empty");
        if (request.getDeliveryDate() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "delivery date can not be empty");
        if (request.getPrice() == null)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "price can not be empty");
        if (request.getQuantity() < 1)
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
        if (request.getStatus() == null || request.getStatus().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
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
        if (shipmentDto.getTotalAmount() == null || shipmentDto.getStatus().isEmpty())
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "status can not be empty");
    }

    public void validateStock(StockDto request) {
        supplierGoodRepository.findById(request.getSupplierGoodId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid supplier goods ID!"));
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

    public void validateWarehouseGood(WarehouseGoodDto request) {
        wareHouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Warehouse ID!"));
    supplierGoodRepository.findById(request.getSupplierGoodId()).orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                " Enter a valid Supplier Goods ID!"));
    if (request.getQty() < 1){
        throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity can not be empty");
    }
    if (request.getQtyAvaliable() < 1){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity avaliable  can not be empty");
    }
        if (request.getQtySold() < 1){
            throw new BadRequestException(CustomResponseCode.BAD_REQUEST, "quantity sold can not be empty");
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



}


