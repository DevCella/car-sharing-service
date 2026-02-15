package com.carsharing.controller;

import static com.carsharing.util.CarTestUtil.createCarCreateRequestDto;
import static com.carsharing.util.CarTestUtil.createCarInfoResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
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

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @DisplayName("getAll should return all cars")
    @Sql(scripts = "classpath:database/cars/insert-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_TwoCars_True() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars").param("page", "0").param("size", "10")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        JsonNode content = root.get("content");
        CarResponseDto[] result = objectMapper.treeToValue(content, CarResponseDto[].class);

        assertThat(result).hasSize(5).extracting(CarResponseDto::id)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L);
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
    @DisplayName("createCar should should return correct CarInfoResponseDto")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:database/cars/delete-1-car.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCar_CarWithModelCivic_True() throws Exception {
        String jsonRequestBody = objectMapper.writeValueAsString(createCarCreateRequestDto());
        CarInfoResponseDto expected = createCarInfoResponseDto();
        MvcResult mvcResult = mockMvc.perform(
                        post("/cars").content(jsonRequestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        CarInfoResponseDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        CarInfoResponseDto.class);
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @DisplayName("updateCar should return correct CarInfoResponseDto")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:database/cars/insert-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-2-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCar_ChangeModelToViciv_True() throws Exception {
        String newModel = "Viciv";
        CarCreateRequestDto baseRequest = createCarCreateRequestDto();
        CarCreateRequestDto requestDto = new CarCreateRequestDto(
                newModel,
                baseRequest.brand(),
                baseRequest.type(),
                baseRequest.inventory(),
                baseRequest.dailyFee()
        );

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        CarInfoResponseDto baseInfo = createCarInfoResponseDto();
        // ТУТ БУЛА ПОМИЛКА: використовуйте CarInfoResponseDto замість UserResponseDto
        CarInfoResponseDto expected = new CarInfoResponseDto(
                4L,
                newModel,
                baseInfo.brand(),
                baseInfo.type(),
                baseInfo.inventory(),
                baseInfo.dailyFee()
        );

        MvcResult mvcResult = mockMvc.perform(
                        put("/cars/4").content(jsonRequestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        CarInfoResponseDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        CarInfoResponseDto.class);
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
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
