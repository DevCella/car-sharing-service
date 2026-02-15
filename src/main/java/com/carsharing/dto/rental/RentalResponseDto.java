package com.carsharing.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Information about a specific rental record")
public record RentalResponseDto(
        @Schema(example = "1")
        Long id,

        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(example = "15-02-2026")
        LocalDate rentalDate,

        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(example = "20-02-2026")
        LocalDate returnDate,

        @JsonFormat(pattern = "dd-MM-yyyy")
        @Schema(example = "19-02-2026", description = "Date when car was actually returned")
        LocalDate actualReturnDate,

        @Schema(example = "5")
        Long carId,

        @Schema(example = "10")
        Long userId
) {
}
