package com.adeo.stockoptimizer.config;

import com.adeo.stockoptimizer.models.CalculationParameters;
import com.adeo.stockoptimizer.models.Product;
import com.adeo.stockoptimizer.models.PurchaseOrder;
import com.adeo.stockoptimizer.models.SalesProfile;
import com.adeo.stockoptimizer.repositories.CalculationParametersRepository;
import com.adeo.stockoptimizer.repositories.ProductRepository;
import com.adeo.stockoptimizer.repositories.PurchaseOrderRepository;
import com.adeo.stockoptimizer.repositories.SalesProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class DataInitializer {

    /**
     * Initializes a default product, its calculation parameters,
     * its sales profile (Monday to Sunday), and some purchase orders.
     */
    @Bean
    public CommandLineRunner initData(
            ProductRepository productRepository,
            CalculationParametersRepository calcParamRepository,
            SalesProfileRepository salesProfileRepository,
            PurchaseOrderRepository purchaseOrderRepository
    ) {
        return args -> {
            // Vérifier si nous avons déjà un produit
            if (productRepository.count() == 0) {

                // 1) Créer un Product
                Product defaultProduct = Product.builder()
                        .name("Produit par défaut")
                        .initialStock(20)
                        .build();
                productRepository.save(defaultProduct);

                // 2) Créer ses CalculationParameters (OneToOne => product)
                CalculationParameters params = CalculationParameters.builder()
                        .deliveryLeadTime(3)  // Délai de livraison 3 jours
                        .orderMultiple(12)    // Multiple de commande 12
                        .product(defaultProduct)
                        .build();
                calcParamRepository.save(params);

                // 3) Créer 7 enregistrements SalesProfile pour ce produit
                List<SalesProfile> salesProfiles = new ArrayList<>();
                salesProfiles.add(new SalesProfile(null, DayOfWeek.MONDAY,    5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.TUESDAY,   5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.WEDNESDAY, 5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.THURSDAY,  5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.FRIDAY,    5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.SATURDAY, 10, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.SUNDAY,   10, defaultProduct));

                salesProfileRepository.saveAll(salesProfiles);

                // 4) Créer des enregistrements PurchaseOrder
                List<PurchaseOrder> purchaseOrders = new ArrayList<>();

                LocalDate startDate = LocalDate.of(2025, 1, 6); // Lundi 6 janvier 2025
                int initialStock = 20;
                int dailySales;
                int remainingStock = initialStock;
                int deliveryLeadTime = params.getDeliveryLeadTime();
                int orderMultiple = params.getOrderMultiple();

                // Générer des commandes pour un mois
                for (int i = 0; i < 30; i++) { // 30 jours
                    LocalDate currentDate = startDate.plusDays(i);
                    DayOfWeek currentDay = currentDate.getDayOfWeek();

                    // Trouver la quantité vendue pour le jour courant
                    dailySales = salesProfiles.stream()
                            .filter(profile -> profile.getDayOfWeek() == currentDay)
                            .findFirst()
                            .map(SalesProfile::getQuantitySold)
                            .orElse(0);

                    remainingStock -= dailySales;

                    // Si le stock est en dessous du seuil, passer une commande
                    if (remainingStock < 10) {
                        int orderQuantity = ((10 - remainingStock + orderMultiple - 1) / orderMultiple) * orderMultiple;
                        LocalDate deliveryDate = currentDate.plusDays(deliveryLeadTime);

                        // Créer une nouvelle commande
                        PurchaseOrder order = PurchaseOrder.builder()
                                .orderDate(currentDate)
                                .quantityOrdered(orderQuantity)
                                .deliveryDate(deliveryDate)
                                .product(defaultProduct)
                                .build();

                        purchaseOrders.add(order);
                        remainingStock += orderQuantity; // Mise à jour du stock après la commande
                    }
                }

                purchaseOrderRepository.saveAll(purchaseOrders);

                log.info("DataInitializer: Product + Params + SalesProfiles + PurchaseOrders créés.");
            }
        };
    }
}
