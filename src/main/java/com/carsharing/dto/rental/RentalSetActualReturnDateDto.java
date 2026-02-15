package com.carsharing.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Request data for setting the actual car return date")
public record RentalSetActualReturnDateDto(
        @NotNull(message = "Actual return date cannot be null")
        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(example = "25-02-2026", description = "The date when the car was actually returned")
        LocalDate actualReturnDate
) {
}
