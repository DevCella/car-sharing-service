package com.carsharing.validation;

import com.carsharing.dto.user.UserRegRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator
        implements ConstraintValidator<PasswordMatch, UserRegRequestDto> {
    @Override
    public boolean isValid(UserRegRequestDto userRegRequestDto,
            ConstraintValidatorContext constraintValidatorContext) {
        if (userRegRequestDto.password() == null
                || userRegRequestDto.repeatPassword() == null) {
            return false;
        }
        return userRegRequestDto.password()
                .equals(userRegRequestDto.repeatPassword());
    }
}
