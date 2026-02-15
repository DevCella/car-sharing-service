package com.carsharing.util;

import static com.carsharing.util.RoleTestUtil.createAdminRole;
import static com.carsharing.util.RoleTestUtil.createCustomerRole;

import com.carsharing.dto.user.UserLoginRequestDto;
import com.carsharing.dto.user.UserLoginResponseDto;
import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateProfileRequestDto;
import com.carsharing.dto.user.UserUpdateRolesRequestDto;
import com.carsharing.model.Role;
import com.carsharing.model.User;
import com.carsharing.model.enums.RoleName;
import java.util.Set;

public class UserTestUtil {
    private static final String USER_EMAIL = "test@gmail.com";
    private static final String USER_NAME = "test";
    private static final String USER_PASSWORD = "123456";

    public static User createUser(Long userId, Role role) {
        User user = new User();
        user.setId(userId);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setRoles(Set.of(role));
        return user;
    }

    public static User createCustomerUser() {
        User user = new User();
        user.setId(3L);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_NAME);
        user.setLastName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setRoles(Set.of(createCustomerRole()));
        return user;
    }

    public static User createAdminUser() {
        User user = new User();
        user.setId(4L);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_NAME);
        user.setLastName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setRoles(Set.of(createAdminRole()));
        return user;
    }

    public static UserRegRequestDto createUserRegRequestDto() {
        return new UserRegRequestDto(
                USER_EMAIL,
                USER_NAME,
                USER_NAME,
                USER_PASSWORD,
                USER_PASSWORD
        );
    }

    public static UserResponseDto createUserResponseDto() {
        return new UserResponseDto(
                3L,
                USER_EMAIL,
                USER_NAME,
                USER_NAME
        );
    }

    public static UserResponseWithRolesDto createUserResponseWithRolesDto() {
        return new UserResponseWithRolesDto(
                3L,
                USER_EMAIL,
                USER_NAME,
                USER_NAME,
                Set.of(RoleName.USER.name())
        );
    }

    public static UserUpdateProfileRequestDto createUserUpdateProfileRequestDto() {
        return new UserUpdateProfileRequestDto(
                "John",
                USER_NAME
        );
    }

    public static UserUpdateRolesRequestDto createUserUpdateRolesRequestDto() {
        return new UserUpdateRolesRequestDto(
                Set.of(RoleName.USER)
        );
    }

    public static UserLoginRequestDto createUserLoginRequestDto() {
        return new UserLoginRequestDto(
                USER_EMAIL,
                USER_PASSWORD
        );
    }

    public static UserLoginResponseDto createUserLoginResponseDto() {
        return new UserLoginResponseDto("correctToken1234");
    }
}
