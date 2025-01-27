package com.adeo.stockoptimizer.mappers;

import com.adeo.stockoptimizer.dtos.OrderDTO;
import com.adeo.stockoptimizer.models.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

     @Mapping(source = "product.id", target = "id")
    OrderDTO purchaseOrderToOrderDto(PurchaseOrder purchaseOrder);

    @Mapping(target = "product", ignore = true)
    PurchaseOrder orderDtoToPurchaseOrder(OrderDTO orderDto);
}
