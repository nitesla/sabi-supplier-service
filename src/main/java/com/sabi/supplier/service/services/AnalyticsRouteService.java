package com.sabi.supplier.service.services;

import com.sabi.supplier.service.repositories.InventoryRepository;
import com.sabi.supplier.service.repositories.SupplierUserRepository;
import com.sabi.supplier.service.repositories.WareHouseRepository;
import com.sabi.suppliers.core.dto.request.AnalyticsRouteRequest;
import com.sabi.suppliers.core.dto.response.AnalyticRouteResponseDto;
import com.sabi.suppliers.core.models.SupplierUser;
import com.sabi.suppliers.core.models.WareHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsRouteService {

    @Autowired
   private WareHouseRepository wareHouseRepository;

    @Autowired
    private SupplierUserRepository supplierUserRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

   public AnalyticRouteResponseDto analyticsRouteCheck(AnalyticsRouteRequest request) {
        AnalyticRouteResponseDto routeResponseDto  = new AnalyticRouteResponseDto();
        List<WareHouse> savedWarehouse = wareHouseRepository.findBySupplierId(request.getSupplierId());
        List<SupplierUser> savedSupplierUser = supplierUserRepository.findBySupplierId(request.getSupplierId());
        if (!savedWarehouse.isEmpty() || !savedSupplierUser.isEmpty()){
            routeResponseDto.setRoute(true);
        } else routeResponseDto.setRoute(false);
//        List<Inventory> savedInventory = inventoryRepository.

        return routeResponseDto;
    }

}
