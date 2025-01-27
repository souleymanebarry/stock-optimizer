package com.adeo.stockoptimizer.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class StockOptimizerUtils {

    private StockOptimizerUtils() {}

    /**
     *  Checks if the given date is a Monday
     */
    public static boolean isMonday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.MONDAY;
    }

    /**
     * Round the quantity 'needed' to the next higher multiple of 'multiple'
     */
    public static int roundUpToMultiple(int needed, int multiple) {
        if (needed <= 0) return 0;
        if (needed % multiple == 0) return needed;
        return ((needed / multiple) + 1) * multiple;
    }

}
