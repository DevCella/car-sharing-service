package com.carsharing.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharing.dto.user.UserLoginRequestDto;
import com.carsharing.dto.user.UserRegRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Register: Successfully register a new user")
    @Sql(scripts = {
            "classpath:database/clear-db.sql",
            "classpath:database/roles/add-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void register_ValidRequest_ShouldReturnUserDto() throws Exception {
        UserRegRequestDto requestDto = new UserRegRequestDto(
                "test@example.com",
                "password123",
                "password123",
                "FirstName",
                "LastName"
        );

        mockMvc.perform(post("/auth/register")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("FirstName"));
    }

    @Test
    @DisplayName("Login: Successfully authenticate user and return token")
    @Sql(scripts = {
            "classpath:database/clear-db.sql",
            "classpath:database/roles/add-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_ValidCredentials_ShouldReturnToken() throws Exception {
        UserRegRequestDto regDto = new UserRegRequestDto(
                "login@example.com", "password123", "password123", "Name", "Surname");

        mockMvc.perform(post("/auth/register")
                        .content(objectMapper.writeValueAsString(regDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        UserLoginRequestDto loginDto = new UserLoginRequestDto("login@example.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
