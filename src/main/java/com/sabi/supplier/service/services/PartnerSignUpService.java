package com.sabi.supplier.service.services;


import com.sabi.framework.helpers.API;
import com.sabi.suppliers.core.dto.request.CompleteSignUpDto;
import com.sabi.suppliers.core.dto.response.PartnerSignUpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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


    public PartnerSignUpResponse partnerSignUp (CompleteSignUpDto request) throws IOException {
        Map map=new HashMap();
        PartnerSignUpResponse response = api.post(partnerSignUp ,request, PartnerSignUpResponse.class,map);
        return response;
    }
}
