package com.carsharing.controller;

import static com.carsharing.util.AuthenticationTestUtil.createAuthentication;
import static com.carsharing.util.RoleTestUtil.createCustomerRole;
import static com.carsharing.util.UserTestUtil.createUserResponseWithRolesDto;
import static com.carsharing.util.UserTestUtil.createUserUpdateProfileRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharing.dto.user.UserResponseWithRolesDto;
import com.carsharing.dto.user.UserUpdateRolesRequestDto;
import com.carsharing.model.enums.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @DisplayName("getUserProfile should return correct UserResponseWithRolesDto")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUserProfile_UserWithIdThree_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(userId, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();
        MvcResult mvcResult = mockMvc.perform(get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        UserResponseWithRolesDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        UserResponseWithRolesDto.class);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("updateUserProfile should update correct User and return UserResponseWithRolesDto")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserProfile_ChangeFirstNameToJohn_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(userId, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        String jsonRequestBody =
                objectMapper.writeValueAsString(createUserUpdateProfileRequestDto());
        MvcResult mvcResult = mockMvc.perform(
                        put("/users/me").content(jsonRequestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        UserResponseWithRolesDto base = createUserResponseWithRolesDto();
        UserResponseWithRolesDto expected = new UserResponseWithRolesDto(
                base.id(),
                base.email(),
                "John",
                base.lastName(),
                base.roles()
        );

        UserResponseWithRolesDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        UserResponseWithRolesDto.class);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("updateUserRoles should update correct User and return UserResponseWithRolesDto ")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRoles_AddRoleAdminForUserWithIdThree_True() throws Exception {
        Long userId = 3L;
        Set<RoleName> roles = Stream.of(RoleName.USER, RoleName.ADMIN)
                .collect(Collectors.toSet());
        UserUpdateRolesRequestDto requestDto = new UserUpdateRolesRequestDto(roles);

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
        MvcResult mvcResult = mockMvc.perform(put("/users/3/role").content(jsonRequestBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        UserResponseWithRolesDto base = createUserResponseWithRolesDto();
        Set<String> expectedRoleNames = Stream.of(RoleName.USER.name(), RoleName.ADMIN.name())
                .collect(Collectors.toSet());
        UserResponseWithRolesDto expected = new UserResponseWithRolesDto(
                base.id(),
                base.email(),
                base.firstName(),
                base.lastName(),
                expectedRoleNames
        );

        UserResponseWithRolesDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        UserResponseWithRolesDto.class);
        assertEquals(expected, result);
    }
}
