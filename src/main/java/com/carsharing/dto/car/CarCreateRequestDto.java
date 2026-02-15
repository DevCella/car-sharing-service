package com.carsharing.dto.car;

import com.carsharing.model.enums.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CarCreateRequestDto(
        @NotBlank(message = "Model cannot be blank") @Size(min = 1, max = 100,
                message = "Model name must be between 1 and 100 characters") String model,
        @NotBlank(message = "Brand cannot be blank") @Size(min = 1, max = 100,
                message = "Brand name must be between 1 and 100 characters") String brand,
        @NotNull(message = "Car type is required") CarType type,
        @PositiveOrZero(message = "Inventory must be 0 or more") int inventory,
        @NotNull(message = "Daily fee is required") @Positive(
                message = "Daily fee must be greater than zero") BigDecimal dailyFee) {
}
