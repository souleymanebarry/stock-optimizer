package com.adeo.stockoptimizer.controllers;

import com.adeo.stockoptimizer.dtos.MonthlyStockStatsDTO;
import com.adeo.stockoptimizer.dtos.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Stock Optimization Resource")
@RequestMapping("/api/optimization")
public interface OptimizationController {

    /**
     * Lance le calcul des commandes pour l'année 2025.
     * Paramètres simplifiés : stockInitial = 20, productId.
     */
    @Operation(summary = "Calculate orders for the year 2025")
    @PostMapping(path = "/calculate", produces = "application/json")
    ResponseEntity<List<OrderDTO>> calculateOrders(
            @RequestParam(defaultValue = "20")int initialStock,
            @RequestParam Long productId);


    /**
     * Recherche d'un multiple plus optimal >= 5
     */
    @Operation(summary = "Find best multiple >= 5 to minimize stock")
    @GetMapping(path ="/optimal-multiple", produces = "application/json")
    ResponseEntity<Integer> findOptimalMultiple(
            @RequestParam(defaultValue = "20") int initialStock,
            @RequestParam Long productId);


    /**
     * Retrieves monthly stock statistics (minimum, maximum and average per month)
     * for the year 2025.
     *
     * @param initialStock Initial stock at the beginning of the year (default: 20)
     * @param productId Identifier of the product to be analyzed.
     * @return A list of monthly statistics including minimum, maximum and average stock levels.
     */
    @Operation(summary = "Get monthly stock statistics (min, max, average)")
    @GetMapping(path = "/monthly-stock-stats", produces = "application/json")
    ResponseEntity<List<MonthlyStockStatsDTO>> getMonthlyStockStats(
            @RequestParam(defaultValue = "20") int initialStock,
            @RequestParam Long productId);
}
