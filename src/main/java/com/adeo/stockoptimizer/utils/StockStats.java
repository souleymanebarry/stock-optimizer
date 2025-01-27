package com.adeo.stockoptimizer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing inventory statistics for a given month.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockStats {
    private int minStock;
    private int maxStock;
    private double avgStock;

    /**
     * Updates statistics with a new stock value.
     * @param stock The new stock value.
     */
    public void updateStats(int stock) {
        if (minStock == 0 || stock < minStock) {
            minStock = stock; // Mise à jour du minimum
        }
        if (stock > maxStock) {
            maxStock = stock; // Mise à jour du maximum
        }
        avgStock = (avgStock + stock) / 2.0; // Mise à jour de la moyenne
    }
}

