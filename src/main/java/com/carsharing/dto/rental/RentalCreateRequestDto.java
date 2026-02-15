package com.carsharing.dto.rental;

import com.carsharing.validation.RentalDateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@RentalDateRange
@Schema(description = "Request data for creating a car rental")
public record RentalCreateRequestDto(
        @NotNull
        @FutureOrPresent
        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(example = "20-02-2026", description = "The start date of the rental")
        LocalDate rentalDate,

        @NotNull
        @Future
        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(example = "25-02-2026", description = "The expected return date")
        LocalDate returnDate,

        @NotNull
        @Positive
        @Schema(example = "1", description = "ID of the car to be rented")
        Long carId
) {
}
