package com.carsharing.controller;

import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateProfileRequestDto;
import com.carsharing.dto.user.UserUpdateRolesRequestDto;
import com.carsharing.model.User;
import com.carsharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Management", description = "Endpoints for managing user profiles and roles")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get current user profile",
            description = "Retrieve the profile information of the currently authenticated user.")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public UserResponseWithRolesDto getUserProfile(Authentication authentication) {
        // Виправлено: безпечне отримання ID з об'єкта User
        User user = (User) authentication.getPrincipal();
        return userService.getUserProfile(user.getId());
    }

    @Operation(summary = "Update current user profile",
            description = "Update the profile details (e.g., first name, "
                    + "last name) for the current user.")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public UserResponseWithRolesDto updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UserUpdateProfileRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateUserProfile(user.getId(), requestDto);
    }

    @Operation(summary = "Update user roles",
            description = "ADMIN only: Change the roles assigned to a specific user.")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseWithRolesDto updateUserRoles(
            @PathVariable("id") Long userId,
            @RequestBody @Valid UserUpdateRolesRequestDto requestDto) { // Додано @Valid
        return userService.updateUserRoles(userId, requestDto);
    }
}
