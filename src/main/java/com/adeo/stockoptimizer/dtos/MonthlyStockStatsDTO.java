package com.adeo.stockoptimizer.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class MonthlyStockStatsDTO {

    private String month;
    private double averageStock;
    private int minStock;
    private int maxStock;

    public void setMonth(int year, int month) {
        this.month = LocalDate.of(year, month, 1)
                .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
    }
}
