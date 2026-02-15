package com.carsharing.dto.payment;

import com.carsharing.model.enums.PaymentType;
import com.carsharing.model.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Short summary of the payment for list views")
public record PaymentSummaryDto(
        @Schema(example = "1")
        Long id,

        @Schema(description = "Current status of the payment", example = "PAID")
        Status status,

        @Schema(description = "Payment category", example = "FINE")
        PaymentType type,

        @Schema(description = "Total amount charged", example = "100.00")
        BigDecimal amountToPay
) {
}
