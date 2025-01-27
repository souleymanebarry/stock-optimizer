package com.adeo.stockoptimizer.repositories;

import com.adeo.stockoptimizer.models.CalculationParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalculationParametersRepository extends JpaRepository<CalculationParameters, Long> {

    Optional<CalculationParameters> findByProductId(Long productId);
}
