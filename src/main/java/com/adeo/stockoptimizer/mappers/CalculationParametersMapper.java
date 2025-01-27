package com.adeo.stockoptimizer.mappers;


import com.adeo.stockoptimizer.dtos.CalculationParametersDTO;
import com.adeo.stockoptimizer.models.CalculationParameters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CalculationParametersMapper {

    CalculationParametersDTO calculationParametersToCalculationParametersDTO(CalculationParameters calculationParameters);

   @Mapping(target = "product", ignore = true)
   CalculationParameters calculationParametersDtoToCalculationParameters(CalculationParametersDTO calculationParametersDto);
}
