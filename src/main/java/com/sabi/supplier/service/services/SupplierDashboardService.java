package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.SupplierDashboardRepository;
import com.sabi.supplier.service.repositories.SupplierGoodRepository;
import com.sabi.supplier.service.repositories.WareHouseRepository;
import com.sabi.suppliers.core.dto.request.SupplierDashboardRequestDto;
import com.sabi.suppliers.core.models.Stock;
import com.sabi.suppliers.core.models.SupplierDashbaord;
import com.sabi.suppliers.core.models.WareHouse;
import com.sabi.suppliers.core.models.response.StockResponseDto;
import com.sabi.suppliers.core.models.response.SupplierDashbaordResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SupplierDashboardService {

    private final ModelMapper mapper;
    private final Validations validations;

    @Autowired
    private SupplierDashboardRepository supplierDashboardRepository;

    @Autowired
   private WareHouseRepository wareHouseRepository;

    @Autowired
    private SupplierGoodRepository supplierGoodRepository;

    public SupplierDashboardService(ModelMapper mapper, Validations validations) {
        this.mapper = mapper;
        this.validations = validations;
    }

    public SupplierDashbaordResponseDto createStock(Long supplierId) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierDashbaord dashbaord = new SupplierDashbaord();
       int warehouseCount = wareHouseRepository.countAllById(supplierId);
       int supplierProductsCount = supplierGoodRepository.countAllBySupplierId(supplierId);
       dashbaord.setWarehouses(warehouseCount);
       dashbaord.setProducts(supplierProductsCount);
        dashbaord.setCreatedBy(userCurrent.getId());
        dashbaord = supplierDashboardRepository.save(dashbaord);
        log.debug("Supplier dashboard summary info - {}"+ new Gson().toJson(dashbaord));
        return mapper.map(dashbaord, SupplierDashbaordResponseDto.class);
    }
}
