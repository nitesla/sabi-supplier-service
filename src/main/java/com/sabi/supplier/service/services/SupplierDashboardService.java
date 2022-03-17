package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.*;
import com.sabi.suppliers.core.dto.request.SupplierDashboardRequestDto;
import com.sabi.suppliers.core.models.*;
import com.sabi.suppliers.core.models.response.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    public SupplierDashboardService(ModelMapper mapper, Validations validations) {
        this.mapper = mapper;
        this.validations = validations;
    }

    public SupplierDashbaordResponseDto createDashboardInfo(Long supplierId,LocalDateTime startDate, LocalDateTime endDate) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierDashbaordResponseDto dashbaord = new SupplierDashbaordResponseDto();
//        Integer warehouseCountS = wareHouseRepository.countAllBySupplierId(supplierId);
        Integer warehouseCount = wareHouseRepository.countAllBySupplierId(supplierId,startDate,endDate);
//        Integer supplierProductsCounts = supplierGoodRepository.countAllBySupplierId(supplierId);
        Integer supplierProductsCount = supplierGoodRepository.countAllBySupplierId(supplierId,startDate,endDate);
//        Integer awaitingShippmentCount = supplyRequestRepository.countAllByStatus("Awaiting_Shippment");
        Integer awaitingShippmentCount = supplyRequestRepository.countAllByStatus("Awaiting_Shipment",startDate,endDate);
        Integer acceptedCount = supplyRequestRepository.countAllByStatus("Accepted",startDate,endDate);
        Integer totalPendingCount = awaitingShippmentCount+acceptedCount;
        Integer shippedCount = supplyRequestRepository.countAllByStatus("Shipped",startDate,endDate);
        Integer cancelledOrder = supplyRequestRepository.countAllByStatus("Cancelled",startDate,endDate);
        Integer rejectedOrder = supplyRequestRepository.countAllByStatus("Rejected",startDate,endDate);
        Integer totalCancelledOrder = cancelledOrder + rejectedOrder;
//       List<SupplyRequest> savedSupplyRequest = supplyRequestRepository.findBySupplierId(supplierId);
       List<Shipment> savedSupplyRequest = shipmentRepository.findShipmentBySupplierId(supplierId,startDate,endDate);
        List<Shipment> savedSupplyRequestForOutstandingPayment = shipmentRepository.findShipmentByPaymentStatus("Unpaid",startDate,endDate);
       log.info("Saved supply request {} ::::::::::::::: " +savedSupplyRequest);
       List<BigDecimal> prices = savedSupplyRequest
               .stream()
               .map(savedSupply ->
                      {
                          log.info("Total price {} ::::::::::::::::::::: " + savedSupply.getTotalAmount());
                         return savedSupply.getTotalAmount();
                      })
               .collect(Collectors.toList());
       BigDecimal totalPrice = prices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        log.info("Second logged total price {} :::::::::::::::: " + totalPrice);
        dashbaord.setTotalSales(totalPrice);

        List<BigDecimal> status = savedSupplyRequestForOutstandingPayment
                .stream()
                .map(savedSupply ->
                {
                    log.info("Total price {} ::::::::::::::::::::: " + savedSupply.getTotalAmount());
                    return savedSupply.getTotalAmount();
                })
                .collect(Collectors.toList());
        BigDecimal totalOutstandingPayment = status.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        dashbaord.setOutstandingPayment(totalOutstandingPayment);
       dashbaord.setCompletedOrder(shippedCount);
       dashbaord.setWarehouses(warehouseCount);
       dashbaord.setProducts(supplierProductsCount);
       dashbaord.setPendingOrder(totalPendingCount);
       dashbaord.setCompletedOrder(shippedCount);
       dashbaord.setCancelledOrder(totalCancelledOrder);
       dashbaord.setOngoingDelivery(acceptedCount);
        dashbaord.setCreatedBy(userCurrent.getId());
//        dashbaord = supplierDashboardRepository.save(dashbaord);
        log.debug("Supplier dashboard summary info - {}"+ new Gson().toJson(dashbaord));
        return dashbaord;
    }

    public List<DashboardProductResponse> fetchTopProduct(Long supplierId,LocalDateTime startDate, LocalDateTime endDate) {
        List<DashboardProductResponse> responseDtos = new ArrayList<>();
        List<SupplyRequest> savedSupplyRequest = supplyRequestRepository.findSupplyRequestBySupplierId(supplierId,startDate,endDate);
        log.info("info fetched from supplier request {} ::::::::::::::: " + savedSupplyRequest);
        savedSupplyRequest.forEach(supplyRequest -> {
            List<ProductVariant> savedProductVariant = productVariantRepository.findProductVariantByProductId(supplyRequest.getProductId());
            DashboardProductResponse response = new DashboardProductResponse();
            response.setName(supplyRequest.getProductName());
            response.setPrice(supplyRequest.getPrice());
            response.setProductId(supplyRequest.getProductId());
            savedProductVariant.forEach(productVariant -> {
                response.setProductImage(productVariant.getPicture());
            });

            responseDtos.add(response);
        });
        return responseDtos;
    }

//    public List<DashboardWarehouseResponse> fetchWarehouseProduct(Long productId) {
//        List<DashboardWarehouseResponse> responseDtos = new ArrayList<>();
//        List<SupplyRequest> savedSupplyRequest = supplyRequestRepository.findSupplyRequestByProductId(productId);
//        log.info("info fetched from supplier request {} ::::::::::::::: " + savedSupplyRequest);
//        savedSupplyRequest.forEach(supplyRequest -> {
//            DashboardWarehouseResponse response = new DashboardWarehouseResponse();
//            response.setProductName(supplyRequest.getProductName());
//            response.setPrice(supplyRequest.getPrice());
//            WareHouse savedWarehouse = wareHouseRepository.findWareHouseById(supplyRequest.getWarehouseId());
//            response.setWarehouseName(savedWarehouse.getName());
//            responseDtos.add(response);
//        });
//        return responseDtos;
//    }

    public List<DashboardWarehouseResponse> fetchWarehouseProductDate(Long productId,LocalDateTime startDate, LocalDateTime endDate) {
        List<DashboardWarehouseResponse> responseDtos = new ArrayList<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<SupplyRequest> savedSupplyRequest = supplyRequestRepository.findSupplyRequestByProductId(productId,startDate,endDate);
        log.info("info fetched from supplier request {} ::::::::::::::: " + savedSupplyRequest);
        savedSupplyRequest.forEach(supplyRequest -> {
            DashboardWarehouseResponse response = new DashboardWarehouseResponse();
            response.setProductName(supplyRequest.getProductName());
            response.setPrice(supplyRequest.getPrice());
            WareHouse savedWarehouse = wareHouseRepository.findWareHouseById(supplyRequest.getWarehouseId());
            response.setWarehouseName(savedWarehouse.getName());
            responseDtos.add(response);
        });
        return responseDtos;
    }

}
