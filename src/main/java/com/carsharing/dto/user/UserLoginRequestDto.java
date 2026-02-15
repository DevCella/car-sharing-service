package com.carsharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User credentials for authentication")
public record UserLoginRequestDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Must be a valid email address")
        @Schema(example = "admin@carsharing.com")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(example = "strong_password_123", type = "string", format = "password")
        String password
) {
}
