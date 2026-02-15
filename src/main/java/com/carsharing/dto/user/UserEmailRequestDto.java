package com.carsharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request containing user email for identification")
public record UserEmailRequestDto(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        @Size(min = 5, max = 255)
        @Schema(example = "user@example.com", description = "Unique email address of the user")
        String email
) {
}
