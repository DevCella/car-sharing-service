package com.carsharing.util;

import static com.carsharing.util.RentalTestUtil.createRental;

import com.carsharing.dto.payment.PaymentDetailsDto;
import com.carsharing.dto.payment.PaymentRequestDto;
import com.carsharing.dto.payment.PaymentResponseDto;
import com.carsharing.dto.payment.PaymentSummaryDto;
import com.carsharing.model.Payment;
import com.carsharing.model.enums.PaymentType;
import com.carsharing.model.enums.Status;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;

public class PaymentTestUtil {
    private static final String CURRENCY = "usd";
    private static final String PRODUCT_NAME = "Car Rental";
    private static final String SESSION_ID = "session_id";
    private static final String SESSION_URL = "sessionurl.com";

    public static Payment createPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Status.PENDING);
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(createRental());
        payment.setSessionUrl(SESSION_URL);
        payment.setSessionId(SESSION_ID);
        payment.setAmountToPay(BigDecimal.valueOf(3100));
        return payment;
    }

    public static PaymentSummaryDto createPaymentSummaryDto() {
        return new PaymentSummaryDto(
                1L,
                Status.PENDING,
                PaymentType.PAYMENT,
                BigDecimal.valueOf(3100)
        );
    }

    public static PaymentDetailsDto createPaymentDetailsDto() {
        return new PaymentDetailsDto(
                1L,
                Status.PENDING,
                PaymentType.PAYMENT,
                1L,
                SESSION_URL,
                SESSION_ID,
                BigDecimal.valueOf(3100)
        );
    }

    public static PaymentRequestDto createPaymentRequestDto() {
        return new PaymentRequestDto(
                1L,
                PaymentType.PAYMENT
        );
    }

    public static PaymentResponseDto createPaymentResponseDto() {
        return new PaymentResponseDto(
                SESSION_URL,
                SESSION_ID
        );
    }

    public static SessionCreateParams createSessionParams(BigDecimal amount) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(amount.multiply(
                                                        BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(PRODUCT_NAME)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    public static Session createStripeSession() {
        Session session = new Session();
        session.setId(SESSION_ID);
        session.setUrl(SESSION_URL);
        return session;
    }
}
