package com.adeo.stockoptimizer.controllers.impl;

import com.adeo.stockoptimizer.controllers.ConfigurationController;
import com.adeo.stockoptimizer.dtos.CalculationParametersDTO;
import com.adeo.stockoptimizer.models.CalculationParameters;
import com.adeo.stockoptimizer.repositories.CalculationParametersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class ConfigurationControllerImpl implements ConfigurationController {

    private final CalculationParametersRepository calculationParametersRepository;


    @Override
    public ResponseEntity<List<CalculationParameters>> getAllParameters() {
        List<CalculationParameters> allParams = calculationParametersRepository.findAll();
        return ResponseEntity.ok(allParams);
    }

    @Override
    public ResponseEntity<CalculationParameters> createOrUpdateParameters(CalculationParametersDTO dto) {
        CalculationParameters params;
        if (dto.getId() != null) {
            params = calculationParametersRepository.findById(dto.getId())
                    .orElse(new CalculationParameters());
        } else {
            params = new CalculationParameters();
        }

        params.setDeliveryLeadTime(dto.getDeliveryLeadTime());
        params.setOrderMultiple(dto.getOrderMultiple());

        CalculationParameters saved = calculationParametersRepository.save(params);

        return ResponseEntity.ok(saved);
    }
}
