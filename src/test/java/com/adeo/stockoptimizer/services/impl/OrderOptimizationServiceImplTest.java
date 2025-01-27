package com.adeo.stockoptimizer.services.impl;

import com.adeo.stockoptimizer.models.CalculationParameters;
import com.adeo.stockoptimizer.models.Product;
import com.adeo.stockoptimizer.models.PurchaseOrder;
import com.adeo.stockoptimizer.models.SalesProfile;
import com.adeo.stockoptimizer.repositories.CalculationParametersRepository;
import com.adeo.stockoptimizer.repositories.ProductRepository;
import com.adeo.stockoptimizer.repositories.PurchaseOrderRepository;
import com.adeo.stockoptimizer.repositories.SalesProfileRepository;
import com.adeo.stockoptimizer.utils.StockStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderOptimizationServiceImplTest {

    @InjectMocks
    private OrderOptimizationServiceImpl service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CalculationParametersRepository calcParamRepository;

    @Mock
    private SalesProfileRepository salesProfileRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    private Product defaultProduct;
    private CalculationParameters defaultParams;
    private List<SalesProfile> defaultSalesProfiles;


    @BeforeEach
    void setUp() {
        // Création du produit par défaut
        defaultProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .initialStock(20)
                .build();

        // Création des paramètres de calcul par défaut
        defaultParams = CalculationParameters.builder()
                .id(1L)
                .deliveryLeadTime(3)
                .orderMultiple(12)
                .build();

        // Création des profils de ventes avec association au produit
        defaultSalesProfiles = List.of(
                SalesProfile.builder().id(1L).dayOfWeek(DayOfWeek.MONDAY).quantitySold(5).product(defaultProduct).build(),
                SalesProfile.builder().id(2L).dayOfWeek(DayOfWeek.TUESDAY).quantitySold(5).product(defaultProduct).build(),
                SalesProfile.builder().id(3L).dayOfWeek(DayOfWeek.WEDNESDAY).quantitySold(5).product(defaultProduct).build(),
                SalesProfile.builder().id(4L).dayOfWeek(DayOfWeek.THURSDAY).quantitySold(5).product(defaultProduct).build(),
                SalesProfile.builder().id(5L).dayOfWeek(DayOfWeek.FRIDAY).quantitySold(5).product(defaultProduct).build(),
                SalesProfile.builder().id(6L).dayOfWeek(DayOfWeek.SATURDAY).quantitySold(10).product(defaultProduct).build(),
                SalesProfile.builder().id(7L).dayOfWeek(DayOfWeek.SUNDAY).quantitySold(10).product(defaultProduct).build()
        );
    }

    @Test
    void shouldCalculateOrderPlanCorrectly() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        List<PurchaseOrder> orders = service.calculateOrderPlan(20, 1L);

        // Assert
        assertThat(orders).isNotEmpty();
        assertThat(orders).allSatisfy(order -> {
            assertThat(order.getOrderDate().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
            assertThat(order.getQuantityOrdered() % defaultParams.getOrderMultiple()).isZero();
        });

        verify(purchaseOrderRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldFindOptimalMultiple() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        int optimalMultiple = service.findOptimalMultiple(20, 1L);

        // Assert
        assertThat(optimalMultiple).isGreaterThanOrEqualTo(5).isLessThanOrEqualTo(30);
    }

    @Test
    void shouldCalculateMonthlyStockStats() {
        // Arrange
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        Map<String, StockStats> stats = service.calculateMonthlyStockStats(20, 1L);

        // Assert
        stats.values().forEach(stat -> {
            assertThat(stat.getMinStock()).isNotNegative(); // Checks that minStock is valid
            assertThat(stat.getAvgStock()).isNotNegative(); // Checks that avgStock is valid
        });
    }

    @Test
    void shouldNotAllowNegativeStockDuringSimulation() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        List<PurchaseOrder> orders = service.calculateOrderPlan(5, 1L);

        // Assert
        int simulatedStock = 5; // initial stock
        for (PurchaseOrder order : orders) {
            int sales = defaultSalesProfiles.stream()
                    .filter(profile -> profile.getDayOfWeek() == order.getOrderDate().getDayOfWeek())
                    .mapToInt(SalesProfile::getQuantitySold)
                    .sum();
            simulatedStock -= sales;
            assertThat(simulatedStock).isNotNegative();
            simulatedStock += order.getQuantityOrdered();
        }
    }



    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateOrderPlan(20, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Product not found: 1");
    }

    @Test
    void shouldThrowExceptionWhenParametersNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateOrderPlan(20, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("Calculation parameters not found for product: 1");
    }

    @Test
    void shouldThrowExceptionWhenSalesProfilesNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(List.of());

        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateOrderPlan(20, 1L)
        );

        assertThat(exception.getMessage()).isEqualTo("No sales profiles found for product: 1");
    }

    @Test
    void shouldGenerateOrderIfStockWillRunOutMidWeek() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        List<PurchaseOrder> orders = service.calculateOrderPlan(15, 1L);

        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getQuantityOrdered()).isPositive();
    }

    @Test
    void shouldGenerateOrdersWithCustomDeliveryLeadTime() {
        // Arrange
        defaultParams.setDeliveryLeadTime(5); // Delivery time changed to 5 days
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        List<PurchaseOrder> orders = service.calculateOrderPlan(20, 1L);

        // Assert
        assertThat(orders).isNotEmpty();
        assertThat(orders).allSatisfy(order -> {
            assertThat(order.getDeliveryDate()).isEqualTo(order.getOrderDate().plusDays(5)); // Check delivery times
            assertThat(order.getQuantityOrdered() % defaultParams.getOrderMultiple()).isZero(); // Check order multiple
        });
    }

    @Test
    void shouldHandleCustomSalesProfiles() {
        // Arrange
        List<SalesProfile> customSalesProfiles = List.of(
                SalesProfile.builder().id(1L).dayOfWeek(DayOfWeek.MONDAY).quantitySold(10).product(defaultProduct).build(),
                SalesProfile.builder().id(2L).dayOfWeek(DayOfWeek.TUESDAY).quantitySold(10).product(defaultProduct).build(),
                SalesProfile.builder().id(3L).dayOfWeek(DayOfWeek.WEDNESDAY).quantitySold(10).product(defaultProduct).build(),
                SalesProfile.builder().id(4L).dayOfWeek(DayOfWeek.THURSDAY).quantitySold(10).product(defaultProduct).build(),
                SalesProfile.builder().id(5L).dayOfWeek(DayOfWeek.FRIDAY).quantitySold(10).product(defaultProduct).build(),
                SalesProfile.builder().id(6L).dayOfWeek(DayOfWeek.SATURDAY).quantitySold(15).product(defaultProduct).build(),
                SalesProfile.builder().id(7L).dayOfWeek(DayOfWeek.SUNDAY).quantitySold(15).product(defaultProduct).build()
        );
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(customSalesProfiles);

        // Act
        List<PurchaseOrder> orders = service.calculateOrderPlan(20, 1L);

        // Assert
        assertThat(orders).isNotEmpty();
        assertThat(orders).allSatisfy(order -> {
            assertThat(order.getQuantityOrdered()).isPositive(); // Order required with higher sales
        });
    }

    @Test
    void shouldCalculateMonthlyStockCurve() {
        // Arrange
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        Map<String, StockStats> stats = service.calculateMonthlyStockStats(20, 1L);

        // Assert
        assertThat(stats).isNotEmpty();
        stats.forEach((month, stat) -> {
            assertThat(stat.getMinStock()).isNotNegative(); // No break
            assertThat(stat.getAvgStock()).isNotNegative(); // Moyenne cohérente
            assertThat(stat.getMaxStock()).isGreaterThanOrEqualTo(stat.getMinStock()); // Max >= Min
        });
    }

    @Test
    void shouldFindOptimalOrderMultiple() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        int optimalMultiple = service.findOptimalMultiple(20, 1L);

        // Assert
        assertThat(optimalMultiple)
                .as("The optimum multiple must lie between the defined limits (5 and 30)")
                .isGreaterThanOrEqualTo(5) // Check that the multiple is at least 5
                .isLessThanOrEqualTo(30); // Check that the multiple is at most 30
    }

    @Test
    void shouldGenerateOrdersWithShortDeliveryLeadTime() {
        // Arrange

        // Very short delivery time: 1 day
        defaultParams.setDeliveryLeadTime(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(defaultProduct));
        when(calcParamRepository.findByProductId(1L)).thenReturn(Optional.of(defaultParams));
        when(salesProfileRepository.findByProductId(1L)).thenReturn(defaultSalesProfiles);

        // Act
        List<PurchaseOrder> orders = service.calculateOrderPlan(20, 1L);

        // Assert
        assertThat(orders).isNotEmpty(); // Checks that commands are generated
        assertThat(orders).allSatisfy(order -> {
            assertThat(order.getDeliveryDate()).isEqualTo(order.getOrderDate().plusDays(1)); // Check 1-day delivery time
            assertThat(order.getQuantityOrdered() % defaultParams.getOrderMultiple()).isZero(); // Checks that the quantity ordered is within the multiple
        });
    }

}
