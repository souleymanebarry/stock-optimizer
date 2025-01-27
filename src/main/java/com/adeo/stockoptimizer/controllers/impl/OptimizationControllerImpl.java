package com.adeo.stockoptimizer.controllers.impl;


import com.adeo.stockoptimizer.controllers.OptimizationController;
import com.adeo.stockoptimizer.dtos.MonthlyStockStatsDTO;
import com.adeo.stockoptimizer.dtos.OrderDTO;
import com.adeo.stockoptimizer.mappers.OrderMapper;
import com.adeo.stockoptimizer.models.PurchaseOrder;
import com.adeo.stockoptimizer.services.OrderOptimizationService;
import com.adeo.stockoptimizer.utils.StockStats;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
public class OptimizationControllerImpl implements OptimizationController {

    private final OrderOptimizationService optimizationService;
    private final OrderMapper orderMapper;

    public OptimizationControllerImpl(OrderOptimizationService optimizationService, OrderMapper orderMapper) {
        this.optimizationService = optimizationService;
        this.orderMapper = orderMapper;
    }

    @Override
    public ResponseEntity<List<OrderDTO>> calculateOrders(int initialStock,Long productId) {
        List<PurchaseOrder> purchaseOrders = optimizationService.calculateOrderPlan(initialStock, productId);
        List<OrderDTO> orderDTOs = purchaseOrders.stream()
                .map(orderMapper::purchaseOrderToOrderDto)
                .toList();
        return ResponseEntity.ok(orderDTOs);
    }

    @Override
    public ResponseEntity<Integer> findOptimalMultiple(int initialStock, Long productId) {
        int bestMultiple = optimizationService.findOptimalMultiple(initialStock, productId);
        return ResponseEntity.ok(bestMultiple);
    }

    @Override
    public ResponseEntity<List<MonthlyStockStatsDTO>> getMonthlyStockStats(int initialStock, Long productId) {
        Map<String, StockStats> monthlyStats = optimizationService.calculateMonthlyStockStats(initialStock, productId);

        // Convertir les résultats en DTO
        List<MonthlyStockStatsDTO> monthlyStatsDTOs = monthlyStats.entrySet().stream()
                .map(entry -> {
                    StockStats stats = entry.getValue();
                    MonthlyStockStatsDTO dto = new MonthlyStockStatsDTO();
                    dto.setMonth(entry.getKey());
                    dto.setAverageStock(stats.getAvgStock());
                    dto.setMinStock(stats.getMinStock());
                    dto.setMaxStock(stats.getMaxStock());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(monthlyStatsDTOs);
    }
}
