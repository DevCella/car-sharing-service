package com.carsharing.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Full user profile information including assigned roles")
public record UserResponseWithRolesDto(
        @Schema(example = "1")
        Long id,

        @Schema(example = "admin@carsharing.com")
        String email,

        @Schema(example = "John")
        String firstName,

        @Schema(example = "Doe")
        String lastName,

        @Schema(description = "Set of user roles", example = "[\"ROLE_CUSTOMER\", \"ROLE_ADMIN\"]")
        Set<String> roles
) {
}
