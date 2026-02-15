package com.carsharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the access token for authenticated user")
public record UserLoginResponseDto(
        @Schema(description = "JWT access token used for authorized requests",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
