package com.carsharing.validation;

import com.carsharing.dto.rental.RentalCreateRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RentalDateRangeValidator
        implements ConstraintValidator<RentalDateRange, RentalCreateRequestDto> {
    @Override
    public boolean isValid(RentalCreateRequestDto dto,
            ConstraintValidatorContext context) {
        if (dto.rentalDate() == null || dto.returnDate() == null) {
            return false;
        }
        return dto.returnDate().isAfter(dto.rentalDate())
                || dto.returnDate().isEqual(dto.rentalDate());
    }
}
