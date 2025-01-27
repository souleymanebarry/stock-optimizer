package com.adeo.stockoptimizer.utils;

import java.time.LocalDate;

/**
 * Record pour la simulation des commandes (évite de persister).
 */
public record SimOrder(LocalDate orderDate, LocalDate deliveryDate, int quantityOrdered) {
    public SimOrder {
        if (quantityOrdered < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (deliveryDate.isBefore(orderDate)) {
            throw new IllegalArgumentException("Delivery date must be after order date");
        }
    }
}

