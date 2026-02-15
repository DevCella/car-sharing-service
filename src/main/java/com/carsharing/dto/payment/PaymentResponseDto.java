package com.carsharing.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing Stripe session details for user redirection")
public record PaymentResponseDto(
        @Schema(description = "Unique identifier for the Stripe checkout session",
                example = "cs_test_a1b2c3d4e5f6")
        String sessionId,

        @Schema(description = "URL to redirect the user to the Stripe checkout page",
                example = "https://checkout.stripe.com/pay/cs_test_...")
        String sessionUrl
) {
}
