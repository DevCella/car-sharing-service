package com.carsharing.controller;

import com.carsharing.dto.user.UserLoginRequestDto;
import com.carsharing.dto.user.UserLoginResponseDto;
import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.exception.RegistrationException;
import com.carsharing.security.AuthenticationService;
import com.carsharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "Endpoints for user registration and authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration",
            description = "Endpoint for user registration")
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }

    @Operation(summary = "User authentication",
            description = "Endpoint for user authentication")
    @PostMapping("/login")
    public UserLoginResponseDto loginResponseDto(
            @RequestBody @Valid UserLoginRequestDto userRequestDto) {
        return authenticationService.authenticate(userRequestDto);
    }
}
