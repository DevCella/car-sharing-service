package com.carsharing.service;

import static com.carsharing.util.RoleTestUtil.createAdminRole;
import static com.carsharing.util.RoleTestUtil.createCustomerRole;
import static com.carsharing.util.UserTestUtil.createCustomerUser;
import static com.carsharing.util.UserTestUtil.createUserRegRequestDto;
import static com.carsharing.util.UserTestUtil.createUserResponseDto;
import static com.carsharing.util.UserTestUtil.createUserResponseWithRolesDto;
import static com.carsharing.util.UserTestUtil.createUserUpdateProfileRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateProfileRequestDto;
import com.carsharing.dto.user.UserUpdateRolesRequestDto;
import com.carsharing.exception.RegistrationException;
import com.carsharing.mapper.UserMapper;
import com.carsharing.model.Role;
import com.carsharing.model.User;
import com.carsharing.model.enums.RoleName;
import com.carsharing.repository.RoleRepository;
import com.carsharing.repository.UserRepository;
import com.carsharing.service.impl.UserServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("register should return correct UserResponseDto")
    void register_CorrectData_True() throws RegistrationException {
        UserRegRequestDto requestDto = createUserRegRequestDto();
        User user = createCustomerUser();
        Role role = createCustomerRole();
        UserResponseDto expected = createUserResponseDto();
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);
        UserResponseDto result = userService.register(requestDto);
        assertEquals(expected, result);
        verify(userRepository).existsByEmail(requestDto.email());
    }

    @Test
    @DisplayName("getUserProfile should return correct UserResponseWithRolesDto")
    void getUserProfile_UserWithIdThree_True() {
        Long userId = 3L;
        User user = createCustomerUser();
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();
        when(userRepository.findWithRolesById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDtoWithRoles(user)).thenReturn(expected);
        UserResponseWithRolesDto result = userService.getUserProfile(userId);
        assertEquals(expected, result);
        verify(userRepository).findWithRolesById(userId);
    }

    @Test
    @DisplayName("updateUserProfile should return correct UserResponseWithRolesDto")
    void updateUserProfile_ChangeFirstNameToJohn_True() {
        Long userId = 3L;
        User user = createCustomerUser();
        UserUpdateProfileRequestDto requestDto = createUserUpdateProfileRequestDto();
        // Створюємо новий рекорд замість setFirstName
        UserResponseWithRolesDto base = createUserResponseWithRolesDto();
        UserResponseWithRolesDto expected =
                new UserResponseWithRolesDto(base.id(), base.email(), "John", base.lastName(),
                        base.roles());
        when(userRepository.findWithRolesById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserProfile(user, requestDto);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoWithRoles(user)).thenReturn(expected);
        UserResponseWithRolesDto result = userService.updateUserProfile(userId, requestDto);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("updateUserRoles should return UserResponseWithRolesDto with correct roles")
    void updateUserRoles_AddAdminRole_True() {
        Long userId = 3L;
        User user = createCustomerUser();
        Set<RoleName> newRoles =
                Stream.of(RoleName.USER, RoleName.ADMIN).collect(Collectors.toSet());
        UserUpdateRolesRequestDto requestDto = new UserUpdateRolesRequestDto(newRoles);
        UserResponseWithRolesDto base = createUserResponseWithRolesDto();
        Set<String> expectedRolesNames =
                Stream.of(RoleName.USER.name(), RoleName.ADMIN.name()).collect(Collectors.toSet());
        UserResponseWithRolesDto expected =
                new UserResponseWithRolesDto(base.id(), base.email(), base.firstName(),
                        base.lastName(), expectedRolesNames);
        when(userRepository.findWithRolesById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(requestDto.roles())).thenReturn(
                List.of(createCustomerRole(), createAdminRole()));
        when(userMapper.toDtoWithRoles(user)).thenReturn(expected);
        UserResponseWithRolesDto result = userService.updateUserRoles(userId, requestDto);
        assertEquals(expected, result);
        verify(roleRepository).findByNameIn(requestDto.roles());
    }
}
