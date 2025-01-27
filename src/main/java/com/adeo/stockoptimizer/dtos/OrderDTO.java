package com.adeo.stockoptimizer.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderDTO {

    private Long id;
    private LocalDate orderDate;
    private int quantityOrdered;
    private LocalDate deliveryDate;
    private Long productId;
}
