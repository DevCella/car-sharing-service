package com.carsharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Basic user profile information")
public record UserResponseDto(
        @Schema(example = "1")
        Long id,

        @Schema(example = "alex.doe@example.com")
        String email,

        @Schema(example = "Alex")
        String firstName,

        @Schema(example = "Doe")
        String lastName
) {
}
