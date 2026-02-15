package com.carsharing.service.impl.payment;

import com.carsharing.dto.payment.PaymentDetailsDto;
import com.carsharing.dto.payment.PaymentRequestDto;
import com.carsharing.dto.payment.PaymentResponseDto;
import com.carsharing.dto.payment.PaymentSummaryDto;
import com.carsharing.exception.EntityNotFoundException;
import com.carsharing.exception.PaymentProcessException;
import com.carsharing.mapper.PaymentMapper;
import com.carsharing.model.Payment;
import com.carsharing.model.Rental;
import com.carsharing.model.enums.PaymentType;
import com.carsharing.model.enums.Status;
import com.carsharing.repository.PaymentRepository;
import com.carsharing.repository.RentalRepository;
import com.carsharing.service.PaymentService;
import com.carsharing.service.StripePaymentService;
import com.carsharing.service.impl.payment.calculators.CalculatorFactory;
import com.carsharing.telegram.NotificationService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final RentalRepository rentalRepository;
    private final CalculatorFactory calculatorFactory;
    private final StripePaymentService stripePaymentService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Override
    public Page<PaymentSummaryDto> findByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByRentalUserId(userId, pageable)
                .map(paymentMapper::toSummaryDto);
    }

    @Override
    public PaymentDetailsDto findById(Long paymentId) {
        return paymentMapper.toDetailsDto(paymentRepository.findById(paymentId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Could not find Payment with id: " + paymentId)));
    }

    @Override
    public PaymentResponseDto createSession(Long userId, PaymentRequestDto requestDto) {
        Rental rental = rentalRepository.findByIdAndUserId(requestDto.rentalId(), userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Could not find Rental by id: " + requestDto.rentalId()
                                + " and User id: " + userId));

        PaymentType paymentType = determineType(rental, requestDto);

        Payment payment =
                paymentRepository.findByRentalIdAndRentalUserId(requestDto.rentalId(), userId)
                        .map(p -> {
                            if (p.getStatus().equals(Status.PAID)) {
                                throw new PaymentProcessException("This Rental is already paid!");
                            }
                            return p;
                        }).orElseGet(() -> paymentMapper.toModel(requestDto));

        BigDecimal amount = calculatorFactory.getCalculator(paymentType).calculate(rental);
        SessionCreateParams sessionCreateParams = stripePaymentService.createSessionParams(amount);
        Session session = stripePaymentService.makeSession(sessionCreateParams);

        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setAmountToPay(amount);
        payment.setType(paymentType);
        payment.setStatus(Status.PENDING);
        payment.setRental(rental);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public void paymentSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Could not find Payment with session id: " + sessionId));

        if (!stripePaymentService.isSessionPaid(sessionId)) {
            notificationService.sendFailedPaymentNotification(payment);
            throw new PaymentProcessException(
                    "Payment for session id: " + sessionId + " is not successful!");
        }

        payment.setStatus(Status.PAID);
        paymentRepository.save(payment);
        notificationService.sendSuccessfulPaymentNotification(payment);
    }

    @Override
    public void paymentCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Could not find Payment with session id: " + sessionId));

        if (Status.PENDING.equals(payment.getStatus())) {
            payment.setStatus(Status.CANCELED);
            paymentRepository.save(payment);
            notificationService.sendCanceledPaymentNotification(payment);
        }
    }

    private PaymentType determineType(Rental rental, PaymentRequestDto requestDto) {
        PaymentType paymentType;
        if (rental.getActualReturnDate() != null
                && rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            paymentType = PaymentType.FINE;
        } else {
            paymentType = PaymentType.PAYMENT;
        }

        if (!paymentType.equals(requestDto.type())) {
            throw new PaymentProcessException(
                    "Requested PaymentType " + requestDto.type() + " is invalid! Expected: "
                            + paymentType);
        }
        return paymentType;
    }
}
