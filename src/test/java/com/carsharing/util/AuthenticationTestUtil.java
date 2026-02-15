package com.carsharing.util;

import static com.carsharing.util.UserTestUtil.createUser;

import com.carsharing.model.Role;
import com.carsharing.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationTestUtil {
    public static Authentication createAuthentication(Long userId, Role role) {
        User user = createUser(userId, role);
        return new UsernamePasswordAuthenticationToken(
                userId, null, user.getAuthorities());
    }
}
