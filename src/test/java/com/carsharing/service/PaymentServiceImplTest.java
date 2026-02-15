package com.carsharing.service;

import static com.carsharing.util.PaymentTestUtil.createPayment;
import static com.carsharing.util.PaymentTestUtil.createPaymentDetailsDto;
import static com.carsharing.util.PaymentTestUtil.createPaymentRequestDto;
import static com.carsharing.util.PaymentTestUtil.createPaymentResponseDto;
import static com.carsharing.util.PaymentTestUtil.createPaymentSummaryDto;
import static com.carsharing.util.PaymentTestUtil.createSessionParams;
import static com.carsharing.util.PaymentTestUtil.createStripeSession;
import static com.carsharing.util.RentalTestUtil.createRental;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carsharing.dto.payment.PaymentDetailsDto;
import com.carsharing.dto.payment.PaymentRequestDto;
import com.carsharing.dto.payment.PaymentResponseDto;
import com.carsharing.dto.payment.PaymentSummaryDto;
import com.carsharing.exception.EntityNotFoundException;
import com.carsharing.exception.PaymentProcessException;
import com.carsharing.mapper.PaymentMapper;
import com.carsharing.model.Payment;
import com.carsharing.model.Rental;
import com.carsharing.model.enums.Status;
import com.carsharing.repository.PaymentRepository;
import com.carsharing.repository.RentalRepository;
import com.carsharing.service.impl.payment.PaymentServiceImpl;
import com.carsharing.service.impl.payment.calculators.CalculatorFactory;
import com.carsharing.service.impl.payment.calculators.PaymentCalculator;
import com.carsharing.telegram.NotificationService;
import com.carsharing.util.PaymentTestUtil;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private static final String SESSION_ID = "session_id";
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentCalculator calculator;
    @Mock
    private CalculatorFactory calculatorFactory;
    @Mock
    private StripePaymentService stripePaymentService;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("findByUserId should return correct Payments")
    void findByUserId_PaymentsWithUserIdThree_True() {
        Long userId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        Payment firstPayment = PaymentTestUtil.createPayment();
        Payment secondPayment = PaymentTestUtil.createPayment();
        List<Payment> paymentList = List.of(firstPayment, secondPayment);
        Page<Payment> payments = new PageImpl<>(paymentList, pageable, 2);
        PaymentSummaryDto firstDto = createPaymentSummaryDto();
        PaymentSummaryDto secondDto = createPaymentSummaryDto();
        List<PaymentSummaryDto> summaryDtos = List.of(firstDto, secondDto);

        when(paymentRepository.findByRentalUserId(userId, pageable)).thenReturn(payments);
        when(paymentMapper.toSummaryDto(paymentList.get(0))).thenReturn(summaryDtos.get(0));
        when(paymentMapper.toSummaryDto(paymentList.get(1))).thenReturn(summaryDtos.get(1));
        Page<PaymentSummaryDto> result = paymentService.findByUserId(userId, pageable);
        Page<PaymentSummaryDto> expected = new PageImpl<>(summaryDtos, pageable, 2);

        assertEquals(expected, result);
        verify(paymentRepository).findByRentalUserId(userId, pageable);
        verify(paymentMapper).toSummaryDto(firstPayment);
        verify(paymentMapper).toSummaryDto(secondPayment);
    }

    @Test
    @DisplayName("findById should return correct Payment dto")
    void findById_PaymentWithIdOne_True() {
        Long paymentId = 1L;
        Payment payment = createPayment();
        PaymentDetailsDto expected = createPaymentDetailsDto();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDetailsDto(payment)).thenReturn(expected);
        PaymentDetailsDto result = paymentService.findById(paymentId);

        assertEquals(expected, result);
        verify(paymentRepository).findById(paymentId);
        verify(paymentMapper).toDetailsDto(payment);
    }

    @Test
    @DisplayName("createSession should return correct Payment dto")
    void createSession_NewPayment_True() {
        Long rentalId = 1L;
        Long userId = 3L;
        PaymentRequestDto requestDto = createPaymentRequestDto();
        Rental rental = createRental();
        Payment payment = createPayment();
        BigDecimal amount = BigDecimal.valueOf(1000);
        SessionCreateParams createParams = createSessionParams(amount);
        Session session = createStripeSession();
        PaymentResponseDto expected = createPaymentResponseDto();

        when(rentalRepository.findByIdAndUserId(rentalId, userId))
                .thenReturn(Optional.of(rental));
        when(paymentRepository.findByRentalIdAndRentalUserId(rentalId, userId))
                .thenReturn(Optional.empty());
        when(paymentMapper.toModel(requestDto)).thenReturn(payment);
        when(calculatorFactory.getCalculator(payment.getType())).thenReturn(calculator);
        when(calculator.calculate(rental)).thenReturn(amount);
        when(stripePaymentService.createSessionParams(amount)).thenReturn(createParams);
        when(stripePaymentService.makeSession(createParams)).thenReturn(session);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(expected);
        PaymentResponseDto result = paymentService.createSession(userId, requestDto);

        assertEquals(expected, result);
        verify(rentalRepository).findByIdAndUserId(rentalId, userId);
        verify(paymentRepository).findByRentalIdAndRentalUserId(rentalId, userId);
        verify(paymentMapper).toModel(requestDto);
        verify(calculatorFactory).getCalculator(payment.getType());
        verify(calculator).calculate(rental);
        verify(stripePaymentService).createSessionParams(amount);
        verify(stripePaymentService).makeSession(createParams);
        verify(paymentRepository).save(payment);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    @DisplayName("paymentSuccess should not throw any exceptions")
    void paymentSuccess_CorrectData_True() {
        Payment payment = createPayment();
        boolean isSessionPaid = true;

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));
        when(stripePaymentService.isSessionPaid(SESSION_ID)).thenReturn(isSessionPaid);
        when(paymentRepository.save(payment)).thenReturn(payment);
        doNothing().when(notificationService).sendSuccessfulPaymentNotification(payment);
        paymentService.paymentSuccess(SESSION_ID);

        verify(paymentRepository).findBySessionId(SESSION_ID);
        verify(stripePaymentService).isSessionPaid(SESSION_ID);
        verify(paymentRepository).save(payment);
        verify(notificationService).sendSuccessfulPaymentNotification(payment);
    }

    @Test
    @DisplayName("paymentSuccess should return PaymentProcessException")
    void paymentSuccess_isSessionPaid_False() {
        Payment payment = createPayment();
        boolean isSessionPaid = false;

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));
        when(stripePaymentService.isSessionPaid(SESSION_ID)).thenReturn(isSessionPaid);
        doNothing().when(notificationService).sendFailedPaymentNotification(payment);
        String expected = "Payment for session id: "
                + SESSION_ID + " is not successful!";

        String actual = assertThrows(PaymentProcessException.class,
                () -> paymentService.paymentSuccess(SESSION_ID))
                .getMessage();

        assertEquals(expected, actual);
        verify(paymentRepository).findBySessionId(SESSION_ID);
        verify(stripePaymentService).isSessionPaid(SESSION_ID);
        verify(notificationService).sendFailedPaymentNotification(payment);
    }

    @Test
    @DisplayName("paymentCancel should not throw any exceptions")
    void paymentCancel_PaymentIsPending_True() {
        Payment payment = createPayment();

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        doNothing().when(notificationService).sendCanceledPaymentNotification(payment);
        paymentService.paymentCancel(SESSION_ID);

        assertEquals(Status.CANCELED, payment.getStatus());
        verify(paymentRepository).findBySessionId(SESSION_ID);
        verify(paymentRepository).save(payment);
        verify(notificationService).sendCanceledPaymentNotification(payment);
    }

    @Test
    @DisplayName("paymentCancel should throw EntityNotFoundException")
    void paymentCancel_PaymentDoesNotExist_True() {
        String fakeSessionId = "fake_id";
        String expected = "Could not find Payment with session id: "
                + fakeSessionId;

        when(paymentRepository.findBySessionId(fakeSessionId)).thenReturn(Optional.empty());

        String actual = assertThrows(EntityNotFoundException.class,
                () -> paymentService.paymentCancel(fakeSessionId))
                .getMessage();

        assertEquals(expected, actual);
        verify(paymentRepository).findBySessionId(fakeSessionId);
    }
}
