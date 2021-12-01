package com.sabi.supplier.service.services;

import com.google.gson.Gson;
import com.sabi.framework.exceptions.ConflictException;
import com.sabi.framework.exceptions.NotFoundException;
import com.sabi.framework.models.User;
import com.sabi.framework.service.TokenService;
import com.sabi.framework.utils.CustomResponseCode;
import com.sabi.supplier.service.helper.Validations;
import com.sabi.supplier.service.repositories.PreferenceRepository;
import com.sabi.supplier.service.repositories.SupplierRepository;
import com.sabi.suppliers.core.dto.request.PreferenceDto;
import com.sabi.suppliers.core.dto.response.PreferenceResponseDto;
import com.sabi.suppliers.core.models.Preference;
import com.sabi.suppliers.core.models.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("ALL")
@Slf4j
@Service
public class PreferenceService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper mapper;
    @Autowired
    private Validations validations;
    @Autowired
    private PreferenceRepository repository;

    public PreferenceService(SupplierRepository supplierRepository, ModelMapper mapper, Validations validations, PreferenceRepository repository) {
        this.supplierRepository = supplierRepository;
        this.mapper = mapper;
        this.validations = validations;
        this.repository = repository;
    }

    public PreferenceResponseDto createPreference(PreferenceDto request) {
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Preference preference = mapper.map(request,Preference.class);
        Preference productExists = repository.findBySupplierId(request.getSupplierId());
        if(productExists != null){
            throw new ConflictException(CustomResponseCode.CONFLICT_EXCEPTION, "Preference already exist");
        }
        Supplier savedSupplier = supplierRepository.getOne(request.getSupplierId());
        if (savedSupplier == null) {
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested supplier Id does not exist!");
        }
        preference.setCreatedBy(userCurrent.getId());
        preference.setIsActive(true);
        preference = repository.save(preference);
        log.debug("Create new preference - {}"+ new Gson().toJson(preference));
        PreferenceResponseDto productResponseDto =  mapper.map(preference, PreferenceResponseDto.class);
        return productResponseDto;

    }

    public PreferenceResponseDto updatePreference(PreferenceDto request) {
//        validations.validateProduct(request);
        User userCurrent = TokenService.getCurrentUserFromSecurityContext();
        Preference preference = repository.findBySupplierId(request.getSupplierId());
        if (preference == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested preference for supplier Id does not exist!");
        }
        mapper.map(request, preference);

        preference.setUpdatedBy(userCurrent.getId());
        preference.setIsActive(true);
        repository.save(preference);
        log.debug("preference record updated - {}"+ new Gson().toJson(preference));
        PreferenceResponseDto preferenceResponseDto =  mapper.map(preference, PreferenceResponseDto.class);
        return preferenceResponseDto;

    }

    public PreferenceResponseDto findPreferenceByPartnerId(Long id){
        Preference preference  = repository.findBySupplierId(id);
        if (preference == null){
            throw new NotFoundException(CustomResponseCode.NOT_FOUND_EXCEPTION,
                    "Requested Preference for supplier id does not exist!");
        }
        PreferenceResponseDto productResponseDto =  mapper.map(preference, PreferenceResponseDto.class);
        return productResponseDto;
    }
}
