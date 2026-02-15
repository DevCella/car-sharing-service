package com.carsharing.service;

import com.carsharing.dto.payment.PaymentDetailsDto;
import com.carsharing.dto.payment.PaymentRequestDto;
import com.carsharing.dto.payment.PaymentResponseDto;
import com.carsharing.dto.payment.PaymentSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentSummaryDto> findByUserId(Long userId, Pageable pageable);

    PaymentDetailsDto findById(Long paymentId);

    PaymentResponseDto createSession(Long userId,
                                     PaymentRequestDto requestDto);

    void paymentSuccess(String sessionId);

    void paymentCancel(String sessionId);
}
