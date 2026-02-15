package com.carsharing.dto.payment;

import com.carsharing.model.enums.PaymentType;
import com.carsharing.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Detailed information about the payment transaction")
public record PaymentDetailsDto(@Schema(example = "1") Long id,
        @Schema(description = "Current status of the payment", example = "PENDING") Status status,
        @Schema(description = "Type of payment", example = "PAYMENT") PaymentType type,
        @Schema(description = "Associated rental ID", example = "10") Long rentalId,
        @Schema(description = "Stripe checkout session URL",
                example = "https://checkout.stripe.com/pay/...") String sessionUrl,
        @Schema(description = "Unique Stripe session ID",
                example = "cs_test_a1b2c3d4") String sessionId,
        @Schema(description = "Total amount to be paid",
                example = "150.50") BigDecimal amountToPay) {
    public PaymentDetailsDto {
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(type, "Payment type cannot be null");
        if (amountToPay != null && amountToPay.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount to pay cannot be negative: " + amountToPay);
        }
    }
}
