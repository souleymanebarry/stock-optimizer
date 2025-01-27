package com.adeo.stockoptimizer.controllers.impl;

import com.adeo.stockoptimizer.controllers.SalesProfileController;
import com.adeo.stockoptimizer.dtos.SalesProfileDTO;
import com.adeo.stockoptimizer.mappers.SalesProfileMapper;
import com.adeo.stockoptimizer.models.SalesProfile;
import com.adeo.stockoptimizer.repositories.SalesProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class SalesProfileControllerImpl implements SalesProfileController {

    private final SalesProfileRepository salesProfileRepository;
    private final SalesProfileMapper salesProfileMapper;

    @Override
    public ResponseEntity<List<SalesProfileDTO>> getSalesProfiles(Long productId) {
        List<SalesProfile> profiles = salesProfileRepository.findByProductId(productId);
        List<SalesProfileDTO> profileDTOs = profiles.stream()
                .map(salesProfileMapper::salesProfileToSalesProfileDTO)
                .toList();
        return ResponseEntity.ok(profileDTOs);
    }

    @Override
    public ResponseEntity<SalesProfileDTO> createOrUpdateSalesProfile(SalesProfileDTO salesProfileDTO) {
        SalesProfile salesProfile = salesProfileMapper.salesProfileDtoToSalesProfile(salesProfileDTO);
        SalesProfile savedProfile = salesProfileRepository.save(salesProfile);
        SalesProfileDTO savedProfileDTO = salesProfileMapper.salesProfileToSalesProfileDTO(savedProfile);
        return ResponseEntity.ok(savedProfileDTO);
    }

    @Override
    public ResponseEntity<Void> deleteSalesProfile(Long id) {
        salesProfileRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
