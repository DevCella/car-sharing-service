package com.carsharing.dto.payment;

import com.carsharing.model.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request data for creating a new Stripe payment session")
public record PaymentRequestDto(
        @NotNull(message = "Rental ID cannot be null")
        @Positive(message = "Rental ID must be a positive number")
        @Schema(description = "ID of the rental to pay for", example = "15")
        Long rentalId,

        @NotNull(message = "Payment type is required")
        @Schema(description = "Type of payment: PAYMENT or FINE", example = "PAYMENT")
        PaymentType type
) {
}
