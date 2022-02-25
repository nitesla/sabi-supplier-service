package com.sabi.supplier.service.services;


import com.sabi.framework.helpers.API;
import com.sabi.suppliers.core.dto.request.AssetTypeRequest;
import com.sabi.suppliers.core.models.response.AssetTypeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("All")
@Service
@Slf4j
public class AssetTypeService {

    @Autowired
    private API api;
    @Value("${asset.type.url}")
    private String assetType;



    public AssetTypeResponse assetTypes (AssetTypeRequest request) throws IOException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(assetType)
                // Add query parameter
                .queryParam("isActive",request.getIsActive());
        Map map=new HashMap();
        AssetTypeResponse response = api.get(builder.toUriString(), AssetTypeResponse.class,map);
        return response;
    }


}
