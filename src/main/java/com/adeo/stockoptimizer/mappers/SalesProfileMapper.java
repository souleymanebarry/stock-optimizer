package com.adeo.stockoptimizer.mappers;

import com.adeo.stockoptimizer.dtos.SalesProfileDTO;
import com.adeo.stockoptimizer.models.SalesProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.DayOfWeek;

@Mapper(componentModel = "spring", imports = {DayOfWeek.class})
public interface SalesProfileMapper {

    @Mapping(source = "dayOfWeek", target = "dayOfWeek")
    SalesProfileDTO salesProfileToSalesProfileDTO(SalesProfile salesProfile);

    @Mapping(target = "product", ignore = true)
    @Mapping(source = "dayOfWeek", target = "dayOfWeek")
    SalesProfile salesProfileDtoToSalesProfile(SalesProfileDTO salesProfileDto);

    default String map(DayOfWeek dayOfWeek) {
        return dayOfWeek != null ? dayOfWeek.name() : null;
    }

    default DayOfWeek map(String dayOfWeek) {
        return dayOfWeek != null ? DayOfWeek.valueOf(dayOfWeek) : null;
    }

}
