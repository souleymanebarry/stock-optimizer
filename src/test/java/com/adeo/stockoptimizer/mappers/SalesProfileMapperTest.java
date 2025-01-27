package com.adeo.stockoptimizer.mappers;

import com.adeo.stockoptimizer.dtos.SalesProfileDTO;
import com.adeo.stockoptimizer.models.SalesProfile;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.assertj.core.api.Assertions.assertThat;

class SalesProfileMapperTest {

    private final SalesProfileMapperImpl mapper = new SalesProfileMapperImpl();

    @Test
    void shouldMapSalesProfileToDTO() {
        // Arrange
        SalesProfile profile = SalesProfile.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .quantitySold(5)
                .build();

        // Act
        SalesProfileDTO dto = mapper.salesProfileToSalesProfileDTO(profile);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDayOfWeek()).isEqualTo("MONDAY");
        assertThat(dto.getQuantitySold()).isEqualTo(5);
    }

    @Test
    void shouldMapDtoToSalesProfile() {
        // Arrange
        SalesProfileDTO dto = new SalesProfileDTO();
        dto.setId(1L);
        dto.setDayOfWeek("MONDAY");
        dto.setQuantitySold(5);

        // Act
        SalesProfile profile = mapper.salesProfileDtoToSalesProfile(dto);

        // Assert
        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(1L);
        assertThat(profile.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(profile.getQuantitySold()).isEqualTo(5);
    }

    @Test
    void shouldHandleNullDayOfWeekToEntity() {
        // Arrange
        SalesProfileDTO dto = new SalesProfileDTO();
        dto.setId(1L);
        dto.setDayOfWeek(null);
        dto.setQuantitySold(5);

        // Act
        SalesProfile profile = mapper.salesProfileDtoToSalesProfile(dto);

        // Assert
        assertThat(profile).isNotNull();
        assertThat(profile.getDayOfWeek()).isNull();
        assertThat(profile.getQuantitySold()).isEqualTo(5);
    }
}
