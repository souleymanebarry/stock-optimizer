package com.adeo.stockoptimizer.services.impl;

import com.adeo.stockoptimizer.models.CalculationParameters;
import com.adeo.stockoptimizer.models.Product;
import com.adeo.stockoptimizer.models.PurchaseOrder;
import com.adeo.stockoptimizer.models.SalesProfile;
import com.adeo.stockoptimizer.repositories.CalculationParametersRepository;
import com.adeo.stockoptimizer.repositories.ProductRepository;
import com.adeo.stockoptimizer.repositories.PurchaseOrderRepository;
import com.adeo.stockoptimizer.repositories.SalesProfileRepository;
import com.adeo.stockoptimizer.services.OrderOptimizationService;
import com.adeo.stockoptimizer.utils.SimOrder;
import com.adeo.stockoptimizer.utils.StockStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adeo.stockoptimizer.utils.StockOptimizerUtils.isMonday;
import static com.adeo.stockoptimizer.utils.StockOptimizerUtils.roundUpToMultiple;

@Service
@RequiredArgsConstructor
public class OrderOptimizationServiceImpl implements OrderOptimizationService {

    private final ProductRepository productRepository;
    private final CalculationParametersRepository calcParamRepository;
    private final SalesProfileRepository salesProfileRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    private static final LocalDate START_DATE = LocalDate.of(2025, 1, 6);
    private static final LocalDate END_DATE = LocalDate.of(2025, 12, 31);

    @Override
    public List<PurchaseOrder> calculateOrderPlan(int initialStock, Long productId) {
        Product product = validateAndLoadProduct(productId);
        CalculationParameters params = validateAndLoadParameters(productId);
        List<SalesProfile> weeklySalesProfiles = validateAndLoadSalesProfiles(productId);

        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        simulateAnnualOrders(initialStock, product, weeklySalesProfiles, params, purchaseOrders);

        purchaseOrderRepository.saveAll(purchaseOrders);
        return purchaseOrders;
    }

    @Override
    public int findOptimalMultiple(int initialStock, Long productId) {
        validateAndLoadProduct(productId);

        final int MIN_MULTIPLE = 5;
        final int MAX_MULTIPLE = 30;

        int bestMultiple = MIN_MULTIPLE;
        double lowestAverageStock = Double.MAX_VALUE;

        for (int multiple = MIN_MULTIPLE; multiple <= MAX_MULTIPLE; multiple++) {
            double averageStock = simulateAndCalculateAverageStock(initialStock, productId, multiple);
            if (averageStock < lowestAverageStock) {
                lowestAverageStock = averageStock;
                bestMultiple = multiple;
            }
        }
        return bestMultiple;
    }


    @Override
    public Map<String, StockStats> calculateMonthlyStockStats(int initialStock, Long productId) {
        CalculationParameters params = validateAndLoadParameters(productId);
        List<SalesProfile> weeklySalesProfiles = validateAndLoadSalesProfiles(productId);

        List<Integer> dailyStockLevels = simulateDailyStockLevels(initialStock, params, weeklySalesProfiles);

        return generateMonthlyStatistics(dailyStockLevels);
    }

    /**
     * Simulates orders and consumption over a year and generates actual orders.
     */
    private void simulateAnnualOrders(int initialStock, Product product, List<SalesProfile> weeklyProfiles,
                                      CalculationParameters params, List<PurchaseOrder> purchaseOrders) {
        int currentStock = initialStock;
        LocalDate currentDate = START_DATE;

        while (!currentDate.isAfter(END_DATE)) {
            currentStock += calculateDailyArrivals(currentDate, purchaseOrders);
            currentStock = updateStockAfterDailySales(currentStock, currentDate, weeklyProfiles);

            if (isMonday(currentDate)) {
                createRealOrderIfNeeded(currentDate, currentStock, product, weeklyProfiles, params, purchaseOrders);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Crée une commande réelle si nécessaire pour éviter les ruptures.
     */
    private void createRealOrderIfNeeded(LocalDate currentDate, int currentStock, Product product,
                                         List<SalesProfile> weeklyProfiles, CalculationParameters params,
                                         List<PurchaseOrder> purchaseOrders) {
        int neededQuantity = calculateWeeklyShortage(currentDate, currentStock, weeklyProfiles);
        if (neededQuantity > 0) {
            int roundedOrderQuantity = roundUpToMultiple(neededQuantity, params.getOrderMultiple());
            PurchaseOrder order = PurchaseOrder.builder()
                    .orderDate(currentDate)
                    .quantityOrdered(roundedOrderQuantity)
                    .deliveryDate(currentDate.plusDays(params.getDeliveryLeadTime()))
                    .product(product)
                    .build();
            purchaseOrders.add(order);
        }
    }

    /**
     * Simulates daily stock levels over a given period.
     *
     * @param initialStock   Initial stock at the start of the period.
     * @param params         Calculation parameters (delivery lead time, order multiple).
     * @param weeklyProfiles Weekly sales profile.
     * @return A list of stock levels for each day in the simulated period.
     */
    private List<Integer> simulateDailyStockLevels(int initialStock, CalculationParameters params,
                                                   List<SalesProfile> weeklyProfiles) {
        List<Integer> dailyStocks = new ArrayList<>();
        List<SimOrder> simulatedOrders = new ArrayList<>();
        int currentStock = initialStock;
        LocalDate currentDate = START_DATE;

        while (!currentDate.isAfter(END_DATE)) {
            currentStock += calculateSimulatedArrivals(currentDate, simulatedOrders);
            currentStock = Math.max(0, updateStockAfterDailySales(currentStock, currentDate, weeklyProfiles)); // Validation ici

            if (isMonday(currentDate)) {
                createSimulatedOrderIfNeeded(currentDate, currentStock, params, weeklyProfiles, simulatedOrders);
            }

            dailyStocks.add(currentStock);
            currentDate = currentDate.plusDays(1);
        }

        return dailyStocks;
    }

    /**
     * Creates a simulated order if a stock shortage is expected for the upcoming week.
     *
     * @param currentDate      The current date in the simulation.
     * @param currentStock     The current stock level.
     * @param params           Calculation parameters (delivery lead time, order multiple).
     * @param weeklyProfiles   Weekly sales profile.
     * @param simulatedOrders  List of simulated orders to which the new order is added if needed.
     */
    private void createSimulatedOrderIfNeeded(LocalDate currentDate, int currentStock,
                                              CalculationParameters params, List<SalesProfile> weeklyProfiles,
                                              List<SimOrder> simulatedOrders) {
        int neededQuantity = calculateWeeklyShortage(currentDate, currentStock, weeklyProfiles);
        if (neededQuantity > 0) {
            int roundedOrderQuantity = roundUpToMultiple(neededQuantity, params.getOrderMultiple());
            simulatedOrders.add(new SimOrder(currentDate, currentDate.plusDays(params.getDeliveryLeadTime()),
                    roundedOrderQuantity));
        }
    }

    /**
     * Simulates stock levels and calculates the average stock over the simulation period.
     *
     * @param initialStock  Initial stock at the start of the period.
     * @param productId     Identifier of the product.
     * @param orderMultiple Order multiple to be respected for simulated orders.
     * @return The average stock level during the simulation period.
     */
    private double simulateAndCalculateAverageStock(int initialStock, Long productId, int orderMultiple) {
        CalculationParameters params = validateAndLoadParameters(productId);
        List<SalesProfile> weeklyProfiles = validateAndLoadSalesProfiles(productId);

        List<Integer> dailyStocks = simulateDailyStockLevels(initialStock, params, weeklyProfiles);

        return dailyStocks.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
    }

    /**
     * Generates monthly stock statistics (minimum, maximum, and average) from daily stock levels.
     *
     * @param dailyStocks List of daily stock levels.
     * @return A map where the key is the month (in "YYYY-MM" format) and the value is the stock statistics for that month.
     */
    private Map<String, StockStats> generateMonthlyStatistics(List<Integer> dailyStocks) {
        Map<String, StockStats> monthlyStats = new HashMap<>();
        LocalDate currentDate = START_DATE;

        for (int stock : dailyStocks) {
            String monthKey = currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue());
            StockStats stats = monthlyStats.computeIfAbsent(monthKey, k -> new StockStats(0, Integer.MAX_VALUE, 0.0));
            stats.updateStats(stock);
            currentDate = currentDate.plusDays(1);
        }

        return monthlyStats;
    }

    /**
     * Calculates the total quantity of orders arriving on a given day.
     *
     * @param currentDate The current date in the simulation.
     * @param orders      List of purchase orders.
     * @return The total quantity of orders delivered on the specified date.
     */
    private int calculateDailyArrivals(LocalDate currentDate, List<PurchaseOrder> orders) {
        return orders.stream()
                .filter(order -> order.getDeliveryDate().equals(currentDate))
                .mapToInt(PurchaseOrder::getQuantityOrdered)
                .sum();
    }

    /**
     * Calculates the total quantity of simulated orders arriving on a given day.
     *
     * @param currentDate      The current date in the simulation.
     * @param simulatedOrders  List of simulated orders.
     * @return The total quantity of simulated orders delivered on the specified date.
     */
    private int calculateSimulatedArrivals(LocalDate currentDate, List<SimOrder> simulatedOrders) {
        return simulatedOrders.stream()
                .filter(order -> order.deliveryDate().equals(currentDate))
                .mapToInt(SimOrder::quantityOrdered)
                .sum();
    }

    /**
     * Updates the stock level after accounting for daily sales.
     *
     * @param currentStock   The current stock level.
     * @param date           The current date in the simulation.
     * @param weeklyProfiles The weekly sales profile defining daily sales quantities.
     * @return The updated stock level after deducting daily sales.
     */
    private int updateStockAfterDailySales(int currentStock, LocalDate date, List<SalesProfile> weeklyProfiles) {
        int dailySales = weeklyProfiles.stream()
                .filter(profile -> profile.getDayOfWeek() == date.getDayOfWeek())
                .mapToInt(SalesProfile::getQuantitySold)
                .findFirst()
                .orElse(0);
        return Math.max(0, currentStock - dailySales);
    }

    /**
     * Calculates the stock shortage for the following week.
     *
     * @param monday         The starting date (Monday) of the week.
     * @param currentStock   The current stock level at the beginning of the week.
     * @param weeklyProfiles The weekly sales profile defining daily sales quantities.
     * @return The quantity needed to prevent a stockout during the week, or 0 if no shortage is expected.
     */
    private int calculateWeeklyShortage(LocalDate monday, int currentStock, List<SalesProfile> weeklyProfiles) {
        int projectedStock = currentStock;

        for (int i = 1; i <= 6; i++) {
            LocalDate day = monday.plusDays(i);
            if (day.isAfter(END_DATE)) break;

            int dailySales = weeklyProfiles.stream()
                    .filter(profile -> profile.getDayOfWeek() == day.getDayOfWeek())
                    .mapToInt(SalesProfile::getQuantitySold)
                    .findFirst()
                    .orElse(0);

            projectedStock -= dailySales;

            if (projectedStock < 0) {
                return Math.abs(projectedStock) + 1;
            }
        }
        return 0;
    }


    /**
     * Loads and validates the existence of a product by its ID.
     *
     * @param productId The ID of the product to load.
     * @return The {@link Product} if found.
     * @throws IllegalArgumentException If the product is not found.
     */
    private Product validateAndLoadProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }

    /**
     * Loads and validates the calculation parameters associated with a product.
     *
     * @param productId The ID of the product for which to load parameters.
     * @return The {@link CalculationParameters} if found.
     * @throws IllegalArgumentException If the calculation parameters are not found for the specified product.
     */
    private CalculationParameters validateAndLoadParameters(Long productId) {
        return calcParamRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Calculation parameters not found for product: " + productId));
    }

    /**
     * Loads and validates the sales profiles associated with a product.
     *
     * @param productId The ID of the product for which to load sales profiles.
     * @return A list of {@link SalesProfile} if found.
     * @throws IllegalArgumentException If no sales profiles are found for the specified product.
     */
    private List<SalesProfile> validateAndLoadSalesProfiles(Long productId) {
        List<SalesProfile> profiles = salesProfileRepository.findByProductId(productId);
        if (profiles.isEmpty()) {
            throw new IllegalArgumentException("No sales profiles found for product: " + productId);
        }
        return profiles;
    }
}
