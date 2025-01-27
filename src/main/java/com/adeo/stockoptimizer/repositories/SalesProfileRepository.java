package com.adeo.stockoptimizer.repositories;

import com.adeo.stockoptimizer.models.SalesProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesProfileRepository extends JpaRepository<SalesProfile, Long> {

    List<SalesProfile> findByProductId(Long productId);
}
