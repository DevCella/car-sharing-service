package com.carsharing.service.impl;

import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateProfileRequestDto;
import com.carsharing.dto.user.UserUpdateRolesRequestDto;
import com.carsharing.exception.EntityNotFoundException;
import com.carsharing.exception.RegistrationException;
import com.carsharing.mapper.UserMapper;
import com.carsharing.model.Role;
import com.carsharing.model.User;
import com.carsharing.model.enums.RoleName;
import com.carsharing.repository.RoleRepository;
import com.carsharing.repository.UserRepository;
import com.carsharing.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegRequestDto userRegRequest)
            throws RegistrationException {
        if (userRepository.existsByEmail(userRegRequest.email())) {
            throw new RegistrationException("User with email "
                    + userRegRequest.email() + " already exists");
        }
        String password = passwordEncoder.encode(userRegRequest.password());
        User user = userMapper.toModel(userRegRequest);
        user.setPassword(password);
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RegistrationException("Role "
                        + RoleName.USER + " not found"));
        user.setRoles(Set.of(role));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseWithRolesDto getUserProfile(Long userId) {
        User user = findUserWithRoles(userId);
        return userMapper.toDtoWithRoles(user);
    }

    @Transactional
    @Override
    public UserResponseWithRolesDto updateUserProfile(
            Long userId, UserUpdateProfileRequestDto requestDto) {
        User user = findUserWithRoles(userId);
        userMapper.updateUserProfile(user, requestDto);
        return userMapper.toDtoWithRoles(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseWithRolesDto updateUserRoles(
            Long userId, UserUpdateRolesRequestDto requestDto) {
        User user = findUserWithRoles(userId);
        Set<Role> roles = new HashSet<>(
                roleRepository.findByNameIn(requestDto.roles()));

        if (roles.size() != requestDto.roles().size()) {
            throw new EntityNotFoundException("Could not find 1 or more Roles with names: "
                    + requestDto.roles());
        }
        user.setRoles(roles);
        return userMapper.toDtoWithRoles(userRepository.save(user));
    }

    private User findUserWithRoles(Long userId) {
        return userRepository.findWithRolesById(userId).orElseThrow(() ->
                new EntityNotFoundException("Could not find User with id: "
                        + userId));
    }
}
