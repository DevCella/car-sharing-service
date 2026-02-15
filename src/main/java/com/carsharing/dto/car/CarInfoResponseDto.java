package com.carsharing.dto.car;

import com.carsharing.model.enums.CarType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Full information about a car in the fleet")
public record CarInfoResponseDto(@Schema(example = "1") Long id,
        @Schema(description = "Car model name", example = "Model S") String model,
        @Schema(description = "Car brand name", example = "Tesla") String brand,
        @Schema(description = "Category of the car", example = "SEDAN") CarType type,
        @Schema(description = "Number of cars currently available", example = "5") int inventory,
        @Schema(description = "Rental price per day", example = "150.00") BigDecimal dailyFee) {
}
