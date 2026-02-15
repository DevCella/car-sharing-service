package com.carsharing.dto.user;

import com.carsharing.model.enums.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

@Schema(description = "Request data for updating user roles (ADMIN only)")
public record UserUpdateRolesRequestDto(
        @NotEmpty(message = "Roles set cannot be empty")
        @Schema(description = "Set of roles to assign to the user",
                example = "[\"MANAGER\", \"ADMIN\"]")
        Set<RoleName> roles
) {
}
