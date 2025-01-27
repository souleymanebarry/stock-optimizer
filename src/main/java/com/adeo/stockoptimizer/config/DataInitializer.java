package com.adeo.stockoptimizer.config;

import com.adeo.stockoptimizer.models.CalculationParameters;
import com.adeo.stockoptimizer.models.Product;
import com.adeo.stockoptimizer.models.SalesProfile;
import com.adeo.stockoptimizer.repositories.CalculationParametersRepository;
import com.adeo.stockoptimizer.repositories.ProductRepository;
import com.adeo.stockoptimizer.repositories.SalesProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class DataInitializer {

    /**
     * Initializes a default product, its calculation parameters,
     * and its sales profile (Monday to Sunday).
     */
    @Bean
    public CommandLineRunner initData(
            ProductRepository productRepository,
            CalculationParametersRepository calcParamRepository,
            SalesProfileRepository salesProfileRepository
    ) {
        return args -> {
            // Vérifier si nous avons déjà un product
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

                // 3) Créer 7 enregistrements SalesProfile pour ce product
                List<SalesProfile> salesProfiles = new ArrayList<>();
                salesProfiles.add(new SalesProfile(null, DayOfWeek.MONDAY,    5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.TUESDAY,   5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.WEDNESDAY, 5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.THURSDAY,  5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.FRIDAY,    5, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.SATURDAY, 10, defaultProduct));
                salesProfiles.add(new SalesProfile(null, DayOfWeek.SUNDAY,   10, defaultProduct));

                salesProfileRepository.saveAll(salesProfiles);

                log.info("DataInitializer: Product + Params + SalesProfiles créés.");
            }
        };
    }
}
