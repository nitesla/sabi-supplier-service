package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.BadRequestException;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.PreviousPasswords;
import com.sabi.framework.models.User;
import com.sabi.framework.notification.requestDto.NotificationRequestDto;
import com.sabi.framework.notification.requestDto.RecipientRequest;
import com.sabi.framework.repositories.PreviousPasswordRepository;
import com.sabi.framework.repositories.UserRepository;
import com.sabi.framework.service.NotificationService;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.Constants;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.helper.SupplierConstant;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.LGARepository;
import com.sabi.supplier.service.repositories.SupplierLocationRepository;
import com.sabi.supplier.service.repositories.SupplierRepository;
import com.sabi.supplier.service.repositories.SupplierUserRepository;
import com.sabi.suppliers.core.dto.request.CompleteSignUpDto;
import com.sabi.suppliers.core.dto.request.PartnerAssetTypeRequest;
import com.sabi.suppliers.core.dto.request.SupplierRequestDto;
import com.sabi.suppliers.core.dto.request.SupplierSignUpRequestDto;
import com.sabi.suppliers.core.dto.response.CompleteSignUpResponse;
import com.sabi.suppliers.core.dto.response.PartnerSignUpResponse;
import com.sabi.suppliers.core.dto.response.SupplierResponseDto;
import com.sabi.suppliers.core.dto.response.SupplierSignUpResponse;
import com.sabi.suppliers.core.models.LGA;
import com.sabi.suppliers.core.models.Supplier;
import com.sabi.suppliers.core.models.SupplierLocation;
import com.sabi.suppliers.core.models.SupplierUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * This class is responsible for all business logic for Supplier
 */


@SuppressWarnings("ALL")
@Slf4j
@Service
public class SupplierService {


    @Autowired
    private PasswordEncoder passwordEncoder;
    private SupplierRepository supplierRepository;
    private UserRepository userRepository;
    private PreviousPasswordRepository previousPasswordRepository;
    private SupplierUserRepository supplierUserRepository;
    private SupplierLocationRepository supplierLocationRepository;
    private LGARepository lgaRepository;
    private PartnerSignUpService partnerSignUpService;
    private NotificationService notificationService;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierService(SupplierRepository supplierRepository,UserRepository userRepository,PreviousPasswordRepository previousPasswordRepository,
                           SupplierUserRepository supplierUserRepository,SupplierLocationRepository supplierLocationRepository,LGARepository lgaRepository,
                           PartnerSignUpService partnerSignUpService,NotificationService notificationService,
                           ModelMapper mapper, ObjectMapper objectMapper, Validations validations) {
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.previousPasswordRepository = previousPasswordRepository;
        this.supplierUserRepository = supplierUserRepository;
        this.supplierLocationRepository = supplierLocationRepository;
        this.lgaRepository = lgaRepository;
        this.partnerSignUpService = partnerSignUpService;
        this.notificationService = notificationService;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;

    }





    public SupplierSignUpResponse supplierSignUp(SupplierSignUpRequestDto request) {
        validations.validateSupplier(request);
        User user = mapper.map(request,User.class);

        User exist = userRepository.findByEmailOrPhone(request.getEmail(),request.getPhone());
        if(exist !=null && exist.getPasswordChangedOn()== null){

            Supplier supplierExist = supplierRepository.findByUserId(exist.getId());
            if(supplierExist !=null){
                SupplierSignUpResponse supplierSignUpResponse= SupplierSignUpResponse.builder()
                        .id(exist.getId())
                        .email(exist.getEmail())
                        .firstName(exist.getFirstName())
                        .lastName(exist.getLastName())
                        .phone(exist.getPhone())
                        .username(exist.getUsername())
                        .supplierId(supplierExist.getId())
                        .build();
                return supplierSignUpResponse;
            }else {
                throw new BadRequestException(CustomResponseCode.BAD_REQUEST, " supplier id does not exist");
            }

        }else if(exist !=null && exist.getPasswordChangedOn() !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "  user already exist");
        }
        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setUserCategory(Constants.OTHER_USER);
        user.setUsername(request.getEmail());
        user.setLoginAttempts(0l);
        user.setCreatedBy(0l);
        user.setIsActive(false);
        user = userRepository.save(user);
        log.debug("Create new agent user - {}"+ new Gson().toJson(user));

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);


        Supplier saveSupplier = new Supplier();
        saveSupplier.setName(request.getName());
        saveSupplier.setUserId(user.getId());
        saveSupplier.setIsActive(false);
        saveSupplier.setCreatedBy(user.getId());
        Supplier supplierResponse =supplierRepository.save(saveSupplier);

        SupplierUser supplierUser = new SupplierUser();
        supplierUser.setSupplierId(supplierResponse.getId());
        supplierUser.setUserId(user.getId());
        supplierUser.setCreatedBy(0l);
        supplierUser.setIsActive(true);
        supplierUserRepository.save(supplierUser);


        SupplierSignUpResponse response = SupplierSignUpResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .name(supplierResponse.getName())
                .supplierId(supplierResponse.getId())
                .build();

        return response;
    }






    public CompleteSignUpResponse completeSignUp(CompleteSignUpDto request) throws IOException {
        validations.validateCompleteSignUp(request);
        Supplier supplier = supplierRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplier Id does not exist!"));
        mapper.map(request, supplier);

        supplier.setUpdatedBy(supplier.getUserId());
        supplier.setIsActive(true);
        supplier.setDeliveryType(request.getDeliveryType());
        supplierRepository.save(supplier);
        log.debug("complete signup  - {}"+ new Gson().toJson(supplier));

        List<SupplierLocation> location = new ArrayList<>();
        request.getLocations().forEach(l -> {
            SupplierLocation supplierLocation = SupplierLocation.builder()
                    .stateId(l.getStateId())
                    .warehouse(l.getWarehouse())
                    .build();
            supplierLocation.setSupplierId(supplier.getId());
            supplierLocation.setCreatedBy(supplier.getUserId());
            supplierLocation.setIsActive(true);
            log.info(" location details " + supplierLocation);
            supplierLocationRepository.save(supplierLocation);
            location.add(supplierLocation);
        });
        User user = userRepository.getOne(supplier.getUserId());
        user.setIsActive(true);
        user.setUpdatedBy(supplier.getUserId());
        user.setPasswordChangedOn(LocalDateTime.now());
        userRepository.save(user);

        if(request.getDeliveryType().equalsIgnoreCase(SupplierConstant.ME)){
            request.setFirstName(user.getFirstName());
            request.setLastName(user.getLastName());
            request.setEmail(user.getEmail());
            request.setPhone(user.getPhone());
            request.setName(supplier.getName());
            String generatePassword= Utility.passwordGeneration();
            request.setPassword(generatePassword);
            request.getAssets().forEach(p -> {
                PartnerAssetTypeRequest tran = PartnerAssetTypeRequest.builder()
                        .assetTypeId(p.getAssetTypeId())
                        .total(p.getTotal())
                        .build();
            });
            PartnerSignUpResponse partnerSignUpResponse = partnerSignUpService.partnerSignUp(request);
            log.info(" partner details " + partnerSignUpResponse);


            String msg = "Hello " + " " + user.getFirstName() + " " + user.getLastName() + "<br/>"
                    + "Username :" + " "+ user.getUsername() + "<br/>"
                    + "Default password :" + " "+ generatePassword + "<br/>"
                    + " You have been profiled as a logistic partner,kindly login with the username and password" + "<br/>";

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            User emailRecipient = userRepository.getOne(user.getId());
            notificationRequestDto.setMessage(msg);
            List<RecipientRequest> recipient = new ArrayList<>();
            recipient.add(RecipientRequest.builder()
                    .email(emailRecipient.getEmail())
                    .build());
            notificationRequestDto.setRecipient(recipient);
            notificationService.emailNotificationRequest(notificationRequestDto);

//            SmsRequest smsRequest = SmsRequest.builder()
//                    .message(msg)
//                    .phoneNumber(emailRecipient.getPhone())
//                    .build();
//            notificationService.smsNotificationRequest(smsRequest);
        }

        CompleteSignUpResponse response = CompleteSignUpResponse.builder()
                .supplierId(supplier.getId())
                .email(supplier.getEmail())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .createdDate(supplier.getCreatedDate())
                .userId(supplier.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userEmail(user.getEmail())
                .userName(user.getUsername())
                .userPhone(user.getPhone())
                .build();
        return response;

    }




    /** <summary>
     * Supplier update
     * </summary>
     * <remarks>this method is responsible for updating already existing Supplier</remarks>
     */
    public SupplierResponseDto updateSupplier(SupplierRequestDto request) {
        validations.validateSupplier(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Supplier supplier = supplierRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!"));
        mapper.map(request, supplier);
        supplier.setUpdatedBy(userCurrent.getId());
        supplierRepository.save(supplier);
        log.debug("Supplier record updated - {}"+ new Gson().toJson(supplier));
        return mapper.map(supplier, SupplierResponseDto.class);
    }


    /** <summary>
     * Find Supplier
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public SupplierResponseDto findSupplier(Long id){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!"));
        return mapper.map(supplier,SupplierResponseDto.class);
    }


    public Page<Supplier> findAll(String name, PageRequest pageRequest ){
        Page<Supplier> supplierProperties = supplierRepository.findALLSupplier(name,pageRequest);
        if(supplierProperties == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        supplierProperties.getContent().forEach(supplier ->{
            LGA lga = lgaRepository.getOne(supplier.getLgaId());
            supplier.setLga(lga.getName());
        });
        return supplierProperties;

    }


    public List<Supplier> getAll(Boolean isActive){
        List<Supplier> supplierProperties = supplierRepository.findByIsActive(isActive);
        for (Supplier sup : supplierProperties
                ) {
            LGA lga = lgaRepository.getOne(sup.getLgaId());
            sup.setLga(lga.getName());

        }
        return supplierProperties;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a Supplier</remarks>
     */
    public void enableDisable (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Supplier supplier = supplierRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested Supplier Id does not exist!"));
        supplier.setIsActive(request.isActive());
        supplier.setUpdatedBy(userCurrent.getId());
        supplierRepository.save(supplier);

    }





}
