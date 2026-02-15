package com.carsharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request data for updating user's first and last name")
public record UserUpdateProfileRequestDto(
        @NotBlank(message = "First name cannot be blank")
        @Size(max = 255)
        @Schema(example = "John")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 255)
        @Schema(example = "Doe")
        String lastName
) {
}
