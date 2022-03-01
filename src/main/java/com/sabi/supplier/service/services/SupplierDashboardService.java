package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.SupplierDashboardRepository;
import com.sabi.supplier.service.repositories.SupplierGoodRepository;
import com.sabi.supplier.service.repositories.SupplyRequestRepository;
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

    @Autowired
    private SupplyRequestRepository supplyRequestRepository;


    public SupplierDashboardService(ModelMapper mapper, Validations validations) {
        this.mapper = mapper;
        this.validations = validations;
    }

    public SupplierDashbaordResponseDto createDashboardInfo(Long supplierId) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierDashbaord dashbaord = new SupplierDashbaord();
       int warehouseCount = wareHouseRepository.countAllById(supplierId);
       int supplierProductsCount = supplierGoodRepository.countAllBySupplierId(supplierId);
       int awaitingShippmentCount = supplyRequestRepository.countAllByStatus("Awaiting_Shippment");
       int acceptedCount = supplyRequestRepository.countAllByStatus("Accepted");
       int totalPendingCount = awaitingShippmentCount+acceptedCount;
       int shippedCount = supplyRequestRepository.countAllByStatus("Shipped");
       int cancelledOrder = supplyRequestRepository.countAllByStatus("Cancelled");
       int rejectedOrder = supplyRequestRepository.countAllByStatus("Rejected");
       int totalCancelledOrder = cancelledOrder + rejectedOrder;
       dashbaord.setCompletedOrder(shippedCount);
       dashbaord.setWarehouses(warehouseCount);
       dashbaord.setProducts(supplierProductsCount);
       dashbaord.setPendingOrder(totalPendingCount);
       dashbaord.setCompletedOrder(shippedCount);
       dashbaord.setCancelledOrder(totalCancelledOrder);
//       dashbaord.setOngoingDelivery();
        dashbaord.setCreatedBy(userCurrent.getId());
        dashbaord = supplierDashboardRepository.save(dashbaord);
        log.debug("Supplier dashboard summary info - {}"+ new Gson().toJson(dashbaord));
        return mapper.map(dashbaord, SupplierDashbaordResponseDto.class);
    }
}
