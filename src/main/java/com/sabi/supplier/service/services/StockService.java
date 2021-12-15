package com.sabi.supplier.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sabi.framework.dto.requestDto.EnableDisEnableDto;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.StockRepository;
import com.sabi.suppliers.core.dto.request.StockDto;
import com.sabi.suppliers.core.dto.response.StockResponseDto;
import com.sabi.suppliers.core.models.Stock;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StockService {


    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;
    private StockRepository stockRepository;

    public StockService(ModelMapper mapper, ObjectMapper objectMapper, Validations validations, StockRepository stockRepository) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;
        this.stockRepository = stockRepository;
    }

    /** <summary>
     * stock creation
     * </summary>
     * <remarks>this method is responsible for creation of new stock</remarks>
     */

    public StockResponseDto createStock(StockDto request) {
        validations.validateStock(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Stock stock = mapper.map(request,Stock.class);
        Stock stockExist = stockRepository.findStockByAction(request.getAction());
        if(stockExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " stock already exist");
        }
        stock.setCreatedBy(userCurrent.getId());
        stock.setIsActive(true);
        stock = stockRepository.save(stock);
        log.debug("Create new State - {}"+ new Gson().toJson(stock));
        return mapper.map(stock, StockResponseDto.class);
    }

//    public StockResponseDto createStock(StockDto request) {
//        validations.validateStock(request);
//        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
//        Stock shipment = mapper.map(request, Stock.class);
//        Stock shipmentExists = stockRepository.findStockByAction(request.getAction());
//        log.info("stock fetched from DB :::::::::::::::::::: " + shipmentExists);
//        if (shipmentExists != null) {
//            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "stock already exist");
//        }
//        log.info("stock request :::::::::::::::::::: " + request);
//        shipment.setCreatedBy(userCurrent.getId());
//        shipment.setIsActive(true);
//        shipment = stockRepository.save(shipment);
//        log.debug("Create new stock - {}" + new Gson().toJson(shipment));
//        StockResponseDto productResponseDto = mapper.map(shipment, StockResponseDto.class);
//        return productResponseDto;
//    }


    /** <summary>
     * stock update
     * </summary>
     * <remarks>this method is responsible for updating already existing stock</remarks>
     */

    public StockResponseDto updateStock(StockDto request) {
        validations.validateStock(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Stock stock = stockRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested stock Id does not exist!"));
        mapper.map(request, stock);
        stock.setUpdatedBy(userCurrent.getId());
        stockRepository.save(stock);
        log.debug("State record updated - {}"+ new Gson().toJson(stock));
        return mapper.map(stock, StockResponseDto.class);
    }


    /** <summary>
     * Find stock
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public StockResponseDto findStock(Long id){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested stock Id does not exist!"));
        return mapper.map(stock,StockResponseDto.class);
    }


    /** <summary>
     * Find all stock
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
    public Page<Stock> findAll(Long supplierGoodId,String action,Long userId, PageRequest pageRequest ){
        Page<Stock> stocks = stockRepository.findStocks(supplierGoodId,action,userId,pageRequest);
        if(stocks == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return stocks;

    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a stock</remarks>
     */
    public void enableDisEnableStock (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Stock stock = stockRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested State Id does not exist!"));
        stock.setIsActive(request.isActive());
        stock.setUpdatedBy(userCurrent.getId());
        stockRepository.save(stock);

    }



    public List<Stock> getAll(Boolean isActive){
        List<Stock> stocks = stockRepository.findByIsActive(isActive);
        return stocks;

    }
}
