package com.sabi.supplier.service.helper;


import com.sabi.framework.exceptions.BadRequestException;
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
    private final SupplyRequestRepository supplyRequestRepository;
    private final WareHouseRepository wareHouseRepository;
    private final WareHouseUserRepository wareHouseUserRepository;

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
                        " Enter a valid product category id!"));
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

    public void validateSupplierUser(SupplierUserDto supplierUserDto){
        Role role = roleRepository.findById(supplierUserDto.getRoleId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid product id!"));
        User user = userRepository.findById(supplierUserDto.getUserId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid supplier id!"));
//        WareHouse wareHouse = wareHouseRepository.findById(supplierUserDto.getWareHouseId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid supplier id!"));
    }

    public void validateSupplierRole(SupplierRoleDto supplierRoleDto){
        Role role = roleRepository.findById(supplierRoleDto.getRoleId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        " Enter a valid product id!"));
//        P user = userRepository.findById(supplierRoleDto.getPartnerId())
//                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
//                        " Enter a valid supplier id!"));
//
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
}


