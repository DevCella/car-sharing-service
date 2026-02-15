package com.carsharing.dto.car;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Short information about a car for list views")
public record CarResponseDto(@Schema(example = "1") Long id,
        @Schema(example = "Camry") String model, @Schema(example = "Toyota") String brand,
        @Schema(description = "Rental price per 24 hours", example = "55.00") BigDecimal dailyFee) {
}
