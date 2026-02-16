package com.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharing.dto.car.CarResponseDto;
import com.carsharing.dto.user.UserRegRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final UserRegRequestDto regDto = new UserRegRequestDto(
            "login@example.com", "password123", "password123", "Name", "Surname");

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @DisplayName("getCarById should return correct car")
    @Sql(scripts = "classpath:database/cars/insert-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCarById_CarWithIdFour_True() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String expectedModel = "Civic";
        String actualModel = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                CarResponseDto.class).model();
        assertEquals(expectedModel, actualModel);
    }

    @Test
    @DisplayName("deleteCar should remove correct Car")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:database/cars/insert-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCar_CarWithIdFour_True() throws Exception {
        mockMvc.perform(delete("/cars/4")).andExpect(status().isNoContent());
        mockMvc.perform(get("/cars/4")).andExpect(status().isNotFound());
    }
}
