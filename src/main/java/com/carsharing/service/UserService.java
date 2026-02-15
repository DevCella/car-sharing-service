package com.carsharing.service;

import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateProfileRequestDto;
import com.carsharing.dto.user.UserUpdateRolesRequestDto;
import com.carsharing.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegRequestDto userRegRequest)
            throws RegistrationException;

    UserResponseWithRolesDto getUserProfile(Long userId);

    UserResponseWithRolesDto updateUserProfile(
            Long userId, UserUpdateProfileRequestDto requestDto);

    UserResponseWithRolesDto updateUserRoles(
            Long userId, UserUpdateRolesRequestDto requestDto);
}
