package com.adeo.stockoptimizer.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.DayOfWeek;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Day of week (MONDAY, TUESDAY, ...)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private int quantitySold;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
