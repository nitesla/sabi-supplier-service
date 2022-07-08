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
import com.sabi.supplier.service.repositories.SupplierBankRepository;
import com.sabi.suppliers.core.dto.request.SupplierBankRequest;
import com.sabi.suppliers.core.dto.response.SupplierBankResponse;
import com.sabi.suppliers.core.models.SupplierBank;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SupplierBankService {
    private final SupplierBankRepository supplierBankRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final Validations validations;

    public SupplierBankService(SupplierBankRepository supplierBankRepository, ModelMapper mapper, ObjectMapper objectMapper,Validations validations) {
        this.supplierBankRepository = supplierBankRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.validations = validations;

    }

    /** <summary>
     * supplierBank creation
     * </summary>
     * <remarks>this method is responsible for creation of new supplierBanks</remarks>
     */

    public SupplierBankResponse createsupplierBank(SupplierBankRequest request) {
        validations.validatesupplierBank(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierBank supplierBank = mapper.map(request, SupplierBank.class);
        SupplierBank supplierBankExist = supplierBankRepository.findBySupplierId(request.getSupplierId());
        if(supplierBankExist !=null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, " supplierBank already exist");
        }
        supplierBank.setCreatedBy(userCurrent.getId());
        supplierBank.setIsActive(true);
        supplierBank = supplierBankRepository.save(supplierBank);
        log.debug("Create new supplierBank - {}"+ new Gson().toJson(supplierBank));
        return mapper.map(supplierBank, SupplierBankResponse.class);
    }


    /** <summary>
     * supplierBank update
     * </summary>
     * <remarks>this method is responsible for updating already existing supplierBanks</remarks>
     */

    public SupplierBankResponse updatesupplierBank(SupplierBankRequest request) {
        validations.validatesupplierBank(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierBank supplierBank = supplierBankRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplierBank Id does not exist!"));
        mapper.map(request, supplierBank);
        supplierBank.setUpdatedBy(userCurrent.getId());
        supplierBankRepository.save(supplierBank);
        log.debug("supplierBank record updated - {}"+ new Gson().toJson(supplierBank));
        return mapper.map(supplierBank, SupplierBankResponse.class);
    }


    /** <summary>
     * Find supplierBank
     * </summary>
     * <remarks>this method is responsible for getting a single record</remarks>
     */
    public SupplierBankResponse findsupplierBank(Long id){
        SupplierBank supplierBank = supplierBankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplierBank Id does not exist!"));
        return mapper.map(supplierBank, SupplierBankResponse.class);
    }


    /** <summary>
     * Find all supplierBank
     * </summary>
     * <remarks>this method is responsible for getting all records in pagination</remarks>
     */
//    public Page<SupplierBank> findAll(String accountNumber, Long supplierId,PageRequest pageRequest ){
//        GenericSpecification<SupplierBank> genericSpecification = new GenericSpecification<>();
//
//        if (accountNumber != null && !accountNumber.isEmpty()) {
//            genericSpecification.add(new SearchCriteria("accountNumber", accountNumber, SearchOperation.MATCH));
//        }
//
//        if (supplierId != null ) {
//            genericSpecification.add(new SearchCriteria("supplierId", supplierId, SearchOperation.MATCH));
//        }
//        Page<SupplierBank> supplyRequests = supplierBankRepository.findAll(genericSpecification, pageRequest);
//    return supplyRequests;
//    }

    public Page<SupplierBank> findAll(String accountNumber, Long supplierId,PageRequest pageRequest){
        Page<SupplierBank> supplierBanks = supplierBankRepository.findSupplierBanks(accountNumber,supplierId,pageRequest);
        if(supplierBanks == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION, " No record found !");
        }
        return supplierBanks;
    }


    /** <summary>
     * Enable disenable
     * </summary>
     * <remarks>this method is responsible for enabling and dis enabling a supplierBank</remarks>
     */
    public void enableDisEnablesupplierBank (EnableDisEnableDto request){
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        SupplierBank supplierBank = supplierBankRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                        "Requested supplierBank Id does not exist!"));
        supplierBank.setIsActive(request.isActive());
        supplierBank.setUpdatedBy(userCurrent.getId());
        supplierBankRepository.save(supplierBank);

    }


    public List<SupplierBank> getAll(Boolean isActive){
        List<SupplierBank> supplierBanks = supplierBankRepository.findByIsActiveOrderByIdDesc(isActive);
        return supplierBanks;

    }


}
