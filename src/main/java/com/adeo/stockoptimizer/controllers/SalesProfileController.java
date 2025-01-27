package com.adeo.stockoptimizer.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import com.adeo.stockoptimizer.dtos.SalesProfileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Sales Profile Resource")
@RequestMapping("/api/sales-profile")
public interface SalesProfileController {

    @Operation(summary = "Get all sales profiles for a product")
    @GetMapping(produces = "application/json")
    ResponseEntity<List<SalesProfileDTO>> getSalesProfiles(@RequestParam Long productId);

    @Operation(summary = "Create or update a sales profile")
    @PostMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<SalesProfileDTO> createOrUpdateSalesProfile(@RequestBody SalesProfileDTO salesProfileDTO);

    @Operation(summary = "Delete a sales profile by ID")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSalesProfile(@PathVariable Long id);
}
