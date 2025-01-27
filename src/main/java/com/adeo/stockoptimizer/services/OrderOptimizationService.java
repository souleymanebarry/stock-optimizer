package com.adeo.stockoptimizer.services;

import com.adeo.stockoptimizer.models.PurchaseOrder;
import com.adeo.stockoptimizer.utils.StockStats;

import java.util.List;
import java.util.Map;

public interface OrderOptimizationService {

    /**
     * Calculates the list of orders for an entire year starting from January 6, 2025.
     * <p>
     * The calculation considers the following constraints:
     * - Orders can only be placed on Mondays.
     * - An order is placed only if a stockout is expected within the week.
     * - Ordered quantities respect the specified order multiple.
     * - Daily sales are defined by the sales history (SalesProfile).
     * - The starting stock is initialized with the value provided as a parameter.
     * - The simulation ends on December 31, 2025.
     *
     * @param initialStock Initial stock at the start of the period (e.g., 20 units)
     * @param productId    Identifier of the product to manage (simplified for this example)
     * @return A list of generated and persisted {@link PurchaseOrder}
     */
    List<PurchaseOrder> calculateOrderPlan(int initialStock, Long productId);

    /**
     * Finds the optimal order multiple to minimize the average stock.
     * <p>
     * The calculation considers the following constraints:
     * - The multiple must be greater than or equal to 5.
     * - The multiple must ensure no stockouts.
     * - The goal is to minimize the average stock over the period.
     * <p>
     * The process evaluates various multiples and simulates orders for each one.
     *
     * @param initialStock Initial stock at the start of the period
     * @param productId    Identifier of the product (simplified for this example)
     * @return The optimal multiple found
     */
    int findOptimalMultiple(int initialStock, Long productId);

    /**
     * Calculates monthly stock statistics for a given period.
     *
     * @param initialStock Initial stock at the start of the period
     * @param productId    Identifier of the product
     * @return A map where the key is the month (String), and the value is an object containing
     *         the minimum, maximum, and average stock levels for that month.
     */
    Map<String, StockStats> calculateMonthlyStockStats(int initialStock, Long productId);
}
