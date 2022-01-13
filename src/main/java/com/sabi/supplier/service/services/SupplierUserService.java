package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.ChangePasswordDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.PreviousPasswords;
import com.sabi.framework.models.Role;
import com.sabi.framework.models.User;
import com.sabi.framework.notification.requestDto.NotificationRequestDto;
import com.sabi.framework.notification.requestDto.RecipientRequest;
import com.sabi.framework.notification.requestDto.WhatsAppRequest;
import com.sabi.framework.repositories.PreviousPasswordRepository;
import com.sabi.framework.repositories.RoleRepository;
import com.sabi.framework.repositories.UserRepository;
import com.sabi.framework.service.AuditTrailService;
import com.sabi.framework.service.NotificationService;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.service.WhatsAppService;
import com.sabi.framework.utils.AuditTrailFlag;
import com.sabi.framework.utils.Constants;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.framework.utils.Utility;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.SupplierUserRepository;
import com.sabi.suppliers.core.dto.request.SupplierUserActivation;
import com.sabi.suppliers.core.dto.request.SupplierUserDto;
import com.sabi.suppliers.core.dto.response.SupplierActivationResponse;
import com.sabi.suppliers.core.dto.response.SupplierUserResponseDto;
import com.sabi.suppliers.core.models.SupplierUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SupplierUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private SupplierUserRepository supplierUserRepository;
    private UserRepository userRepository;
    private PreviousPasswordRepository previousPasswordRepository;
    private RoleRepository roleRepository;
    private NotificationService notificationService;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private final AuditTrailService auditTrailService;
    private final WhatsAppService whatsAppService;

    public SupplierUserService(SupplierUserRepository supplierUserRepository,UserRepository userRepository,
                               PreviousPasswordRepository previousPasswordRepository,RoleRepository roleRepository,
                               NotificationService notificationService,ModelMapper mapper, ObjectMapper objectMapper,
                               Validations validations,AuditTrailService auditTrailService,WhatsAppService whatsAppService) {
        this.supplierUserRepository = supplierUserRepository;
        this.userRepository = userRepository;
        this.previousPasswordRepository = previousPasswordRepository;
        this.roleRepository = roleRepository;
        this.notificationService = notificationService;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
        this.auditTrailService = auditTrailService;
        this.whatsAppService = whatsAppService;
    }



    public SupplierUserResponseDto createSupplierUser(SupplierUserDto request,HttpServletRequest request1) {
        validations.validateSupplierUser(request);
        User user = mapper.map(request,User.class);

        User userExist = userRepository.findByEmailOrPhone(request.getEmail(),request.getPhone());
        if(userExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " User already exist");
        }
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        SupplierUser supplierUser = supplierUserRepository.findByUserId(userCurrent.getId());

        String password = Utility.getSaltString();
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(request.getEmail());
        user.setCreatedBy(userCurrent.getId());
        user.setUserCategory(Constants.OTHER_USER);
        user.setClientId(supplierUser.getSupplierId());
        user.setIsActive(false);
        user.setLoginAttempts(0l);
        user = userRepository.save(user);
        log.debug("Create new supplier user - {}"+ new Gson().toJson(user));

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        SupplierUser supplier = new SupplierUser();
        supplier.setSupplierId(supplierUser.getSupplierId());
        supplier.setUserId(user.getId());
        supplier.setCreatedBy(userCurrent.getId());
        supplier.setIsActive(true);
        supplierUserRepository.save(supplier);
        log.debug("save to supplier user table - {}"+ new Gson().toJson(supplier));

        auditTrailService
                .logEvent(userCurrent.getUsername(),
                        "Create new SupplierUser by :" + userCurrent.getUsername(),
                        AuditTrailFlag.CREATE,
                        " Create new SupplierUser for:" + user.getUsername() ,1, Utility.getClientIp(request1));
        return mapper.map(user, SupplierUserResponseDto.class);
    }





    public  void activateSupplierUser (SupplierUserActivation request) {
        validations.validateSupplierUserActivation(request);
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, "Invalid email");
        }
        user.setResetToken(Utility.registrationCode("HHmmss"));
        user.setResetTokenExpirationDate(Utility.tokenExpiration());
        userRepository.save(user);

        String msg = "Hello " + " " + user.getFirstName() + " " + user.getLastName() + "<br/>"
                + "Username :" + " "+ user.getUsername() + "<br/>"
                + "Activation OTP :" + " "+ user.getResetToken() + "<br/>"
                + " Kindly click the link below to complete your registration " + "<br/>"
                + "<a href=\"" + request.getActivationUrl() +  "\">Activate your account</a>";

        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        User emailRecipient = userRepository.getOne(user.getId());
        notificationRequestDto.setMessage(msg);
        List<RecipientRequest> recipient = new ArrayList<>();
        recipient.add(RecipientRequest.builder()
                .email(emailRecipient.getEmail())
                .build());
        notificationRequestDto.setRecipient(recipient);
        notificationService.emailNotificationRequest(notificationRequestDto);

        WhatsAppRequest whatsAppRequest = WhatsAppRequest.builder()
                .message(msg)
                .phoneNumber(emailRecipient.getPhone())
                .build();
        whatsAppService.whatsAppNotification(whatsAppRequest);

    }



    public SupplierActivationResponse supplierPasswordActivation(ChangePasswordDto request) {

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested user id does not exist!"));
        mapper.map(request, user);

        String password = request.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChangedOn(LocalDateTime.now());
        user = userRepository.save(user);

        PreviousPasswords previousPasswords = PreviousPasswords.builder()
                .userId(user.getId())
                .password(user.getPassword())
                .createdDate(LocalDateTime.now())
                .build();
        previousPasswordRepository.save(previousPasswords);

        SupplierUser supplier = supplierUserRepository.findByUserId(user.getId());

        SupplierActivationResponse response = SupplierActivationResponse.builder()
                .userId(user.getId())
                .supplierId(supplier.getSupplierId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

        return response;
    }



    public Page<User> findByClientId(String firstName, String phone, String email, String username,
                                     Long roleId,Boolean isActive, String lastName, PageRequest pageRequest ){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierUser supplier = supplierUserRepository.findByUserId(userCurrent.getId());
        Page<User> users = userRepository.findByClientId(firstName,phone,email,username,roleId,supplier.getSupplierId(),isActive,lastName,pageRequest);
        if(users == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        users.getContent().forEach(partnerUsers ->{
            User user = userRepository.getOne(partnerUsers.getId());
            if(user.getRoleId() !=null){
                Role role = roleRepository.getOne(user.getRoleId());
                partnerUsers.setRoleName(role.getName());
            }
        });
        return users;

    }


    public List<User> getAll(Boolean isActive){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();

        SupplierUser supplier = supplierUserRepository.findByUserId(userCurrent.getId());
        List<User> users = userRepository.findByIsActiveAndClientId(isActive,supplier.getSupplierId());
        for (User partnerUsers : users
                ) {
            if(partnerUsers.getRoleId() !=null){
                Role role = roleRepository.getOne(partnerUsers.getRoleId());
                partnerUsers.setRoleName(role.getName());
            }
        }
        return users;
    }


}
