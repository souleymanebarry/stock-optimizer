package com.adeo.stockoptimizer.controllers;

import com.adeo.stockoptimizer.dtos.CalculationParametersDTO;
import com.adeo.stockoptimizer.models.CalculationParameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Configuration Resource")
@RequestMapping("/api/config")
public interface ConfigurationController {

    /**
     * Retrieves the list of basic calculation parameters
     */
    @Operation(summary = "get all calculation parameters")
    @GetMapping(produces = "application/json")
    ResponseEntity<List<CalculationParameters>> getAllParameters();

    @Operation(summary = "Create or update calculation parameters")
    @PostMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<CalculationParameters> createOrUpdateParameters(@RequestBody CalculationParametersDTO calculationParametersDTO);
}
