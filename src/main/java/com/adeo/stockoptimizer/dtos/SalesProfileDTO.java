package com.adeo.stockoptimizer.dtos;

import lombok.Data;

@Data
public class SalesProfileDTO {

    private Long id;
    private String dayOfWeek;
    private int quantitySold;
}
