package com.carsharing.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharing.dto.rental.RentalCreateRequestDto;
import com.carsharing.dto.rental.RentalResponseDto;
import com.carsharing.model.User;
import com.carsharing.repository.UserRepository;
import com.carsharing.service.RentalService;
import com.carsharing.telegram.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RentalService rentalService;
    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private NotificationService notificationService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("addRental should create a new rental and return 201")
    @Sql(scripts = {
            "classpath:database/clear-db.sql",
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-user.sql",
            "classpath:database/cars/add-car.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addRental_ValidRequest_Created() throws Exception {
        User userEntity = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        var auth = new UsernamePasswordAuthenticationToken(
                userEntity, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        RentalCreateRequestDto requestDto = new RentalCreateRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                1L
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/rentals")
                        .with(org.springframework.security.test.web.servlet
                                .request.SecurityMockMvcRequestPostProcessors.authentication(auth))
                        .with(org.springframework.security
                                .test.web.servlet.request
                                .SecurityMockMvcRequestPostProcessors.csrf())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.carId").value(1L));
    }

    @Test
    @DisplayName("getRentalById should return rental for authorized customer")
    @Sql(scripts = {
            "classpath:database/clear-db.sql",
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-user.sql",
            "classpath:database/cars/add-car.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getRentalById_OwnerUser_Success() throws Exception {
        User userEntity = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        RentalCreateRequestDto createDto = new RentalCreateRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                1L
        );
        RentalResponseDto savedRental = rentalService.save(createDto, userEntity.getId());

        var auth = new UsernamePasswordAuthenticationToken(
                userEntity, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        mockMvc.perform(get("/rentals/" + savedRental.id())
                        .with(org.springframework.security
                                .test.web.servlet.request
                                .SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRental.id()))
                .andExpect(jsonPath("$.userId").value(userEntity.getId()));
    }

    @Test
    @DisplayName("setActualReturnDate should return 403 for Customer")
    @Sql(scripts = "classpath:database/roles"
            + "/add-roles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void setActualReturnDate_Customer_Forbidden() throws Exception {
        User user = new User();
        user.setEmail("customer@example.com");
        var auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        java.time.format.DateTimeFormatter formatter = java.time.format
                .DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.now().plusDays(1).format(formatter);

        mockMvc.perform(post("/rentals/1/return")
                        .with(org.springframework.security.test.web.servlet
                                .request.SecurityMockMvcRequestPostProcessors.authentication(auth))
                        .with(org.springframework.security.test
                                .web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actualReturnDate\":\"" + formattedDate + "\"}"))
                .andExpect(status().isForbidden());
    }
}
