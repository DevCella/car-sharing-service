package com.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carsharing.dto.user.UserRegRequestDto;
import com.carsharing.dto.user.UserResponseDto;
import com.carsharing.exception.RegistrationException;
import com.carsharing.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(
        properties = {"telegrambots.enabled=false", "telegrambots.bots[0].username=test_bot",
                      "telegrambots.bots[0].token=12345:test_token", "stripe.secret.key=test_key",
                      "payment.success.url=http://localhost:8080/success",
                      "payment.cancel.url=http://localhost:8080/cancel"})
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @Test
    @DisplayName("Register: Successfully save user with USER role")
    @Sql(scripts = {"classpath:database/users/delete-users.sql",
                    "classpath:database/roles/add-roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void register_ValidRequest_ShouldReturnUserDto() throws RegistrationException {
        UserRegRequestDto requestDto =
                new UserRegRequestDto("bob@example.com", "Bob", "Alison", "password123",
                        "password123");
        UserResponseDto result = userService.register(requestDto);
        assertNotNull(result);
        assertEquals(requestDto.email(), result.email());
        assertEquals(requestDto.firstName(), result.firstName());
    }

    @Test
    @DisplayName("Register: Throw exception if email already exists")
    @Sql(scripts = {"classpath:database/users/delete-users.sql",
                    "classpath:database/roles/add-roles.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void register_DuplicateEmail_ShouldThrowException() throws RegistrationException {
        UserRegRequestDto requestDto =
                new UserRegRequestDto("bob@example.com", "Bob", "Alison", "password123",
                        "password123");
        userService.register(requestDto);
        assertThrows(RegistrationException.class, () -> userService.register(requestDto));
    }
}
