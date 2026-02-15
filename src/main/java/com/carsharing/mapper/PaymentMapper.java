package com.carsharing.mapper;

import com.carsharing.config.MapperConfig;
import com.carsharing.dto.payment.PaymentDetailsDto;
import com.carsharing.dto.payment.PaymentRequestDto;
import com.carsharing.dto.payment.PaymentResponseDto;
import com.carsharing.dto.payment.PaymentSummaryDto;
import com.carsharing.model.Payment;
import com.carsharing.model.Rental;
import com.carsharing.model.enums.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);

    PaymentSummaryDto toSummaryDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDetailsDto toDetailsDto(Payment payment);

    @Mapping(target = "rental", ignore = true)
    Payment toModel(PaymentRequestDto requestDto);

    @AfterMapping
    default void finishToModelMapping(
            @MappingTarget Payment payment, PaymentRequestDto requestDto) {
        Rental rental = new Rental();
        rental.setId(requestDto.rentalId());
        payment.setRental(rental);
        payment.setStatus(Status.PENDING);
    }
}
