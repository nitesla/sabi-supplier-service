package com.sabi.supplier.service.services;


import com.sabi.framework.helpers.API;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.supplier.service.repositories.SupplierUserRepository;
import com.sabi.suppliers.core.dto.request.CompleteSignUpDto;
import com.sabi.suppliers.core.dto.response.ExternalDetailsResponse;
import com.sabi.suppliers.core.dto.response.PartnerSignUpResponse;
import com.sabi.suppliers.core.models.SupplierUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("All")
@Service
@Slf4j
public class PartnerSignUpService {

    @Autowired
    private API api;
    @Value("${partner.url}")
    private String partnerSignUp;

    @Autowired
    private SupplierUserRepository supplierUserRepository;


    public PartnerSignUpResponse partnerSignUp (CompleteSignUpDto request)   {
        Map map=new HashMap();
        PartnerSignUpResponse response = api.post(partnerSignUp+"externalsignup" ,request, PartnerSignUpResponse.class,map);
        return response;
    }

    public ExternalDetailsResponse partnerDetails ()  {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierUser supplierUser = supplierUserRepository.findByUserId(userCurrent.getId());
        Map map=new HashMap();
        ExternalDetailsResponse response = api.get(partnerSignUp+"details/"+ supplierUser.getSupplierId(), ExternalDetailsResponse.class,map);
        return response;
    }
}
