package com.adeo.stockoptimizer.dtos;

import lombok.Data;

@Data
public class CalculationParametersDTO {

    private Long id;
    private int deliveryLeadTime;
    private int orderMultiple;
}
