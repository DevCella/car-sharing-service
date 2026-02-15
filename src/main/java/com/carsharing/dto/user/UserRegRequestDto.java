package com.carsharing.dto.user;

import com.carsharing.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
@Schema(description = "Request data for new user registration")
public record UserRegRequestDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @Schema(example = "alex.doe@example.com")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        @Schema(example = "strong_pwd_123", type = "string", format = "password")
        String password,

        @NotBlank(message = "Please repeat your password")
        @Schema(example = "strong_pwd_123", type = "string", format = "password")
        String repeatPassword,

        @NotBlank(message = "First name is required")
        @Size(max = 255)
        @Schema(example = "Alex")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 255)
        @Schema(example = "Doe")
        String lastName
) {
}
