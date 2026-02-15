package com.carsharing.controller;

import com.carsharing.dto.payment.PaymentDetailsDto;
import com.carsharing.dto.payment.PaymentRequestDto;
import com.carsharing.dto.payment.PaymentResponseDto;
import com.carsharing.dto.payment.PaymentSummaryDto;
import com.carsharing.model.User;
import com.carsharing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Management",
        description = "Endpoints for handling car rental payments via Stripe")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get user payments",
            description = "ADMIN only: Get a paginated list of payments for a specific user ID.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PaymentSummaryDto> getUserPayments(@RequestParam("user_id") Long userId,
            @ParameterObject Pageable pageable) { // Додано @ParameterObject для Swagger
        return paymentService.findByUserId(userId, pageable);
    }

    @Operation(summary = "Get payment details",
            description = "ADMIN only:Retrieve information about a specific payment by its ID.")
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDetailsDto getById(@PathVariable Long paymentId) {
        return paymentService.findById(paymentId);
    }

    @Operation(summary = "Create Stripe session",
            description = "Initiates a Stripe checkout session for a specific car rental.")
    @ApiResponse(responseCode = "201", description = "Session created successfully")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createSession(Authentication authentication,
            @RequestBody @Valid PaymentRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return paymentService.createSession(user.getId(), requestDto);
    }

    @Operation(summary = "Stripe success callback",
            description = "Redirect endpoint for Stripe after a "
                    + "successful transaction. Updates payment status.")
    @GetMapping("/success")
    public String paymentSuccess(
            @Parameter(description = "Stripe session ID") @RequestParam("session_id")
            String sessionId) {
        paymentService.paymentSuccess(sessionId);
        return "Payment successful! Your rental is confirmed. You may close this tab.";
    }

    @Operation(summary = "Stripe cancel callback",
            description = "Redirect endpoint for Stripe when payment "
                    + "is canceled. Payment stays PENDING.")
    @GetMapping("/cancel")
    public String paymentCancel(
            @Parameter(description = "Stripe session ID") @RequestParam("session_id")
            String sessionId) {
        paymentService.paymentCancel(sessionId);
        return "The payment process was canceled. Your session is still active for 24 hours.";
    }
}
