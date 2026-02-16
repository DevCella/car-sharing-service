package com.carsharing.util;

import com.carsharing.model.Role;
import com.carsharing.model.User;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthenticationTestUtil {
    public static Authentication createAuthentication(Long userId, Role role) {
        User user = new User();
        user.setId(userId);
        user.setEmail("test@gmail.com");

        String authorityName = role.getName().name();
        if (!authorityName.startsWith("ROLE_")) {
            authorityName = "ROLE_" + authorityName;
        }

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(authorityName))
        );
    }
}
