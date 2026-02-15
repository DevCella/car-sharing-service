package com.carsharing.controller;

import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import com.carsharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car Management", description = "Endpoints for viewing and managing car inventory")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(summary = "Get all cars",
            description = "Retrieve a paginated list of available cars. Public access.")
    @GetMapping
    public Page<CarResponseDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @Operation(summary = "Get car by ID",
            description = "Retrieve full details of a specific car. Public access.")
    @GetMapping("/{carId}")
    public CarInfoResponseDto getCarById(@PathVariable Long carId) {
        return carService.findById(carId);
    }

    @Operation(summary = "Add a new car", description = "Create a new car record. ADMIN only.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CarInfoResponseDto createCar(@RequestBody @Valid CarCreateRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Update car details",
            description = "Update existing car information by its ID. ADMIN only.")
    @PutMapping("/{carId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CarInfoResponseDto updateCar(@PathVariable Long carId,
            @RequestBody @Valid CarCreateRequestDto requestDto) {
        return carService.update(carId, requestDto);
    }

    @Operation(summary = "Delete car",
            description = "Remove a car from the database by ID (Soft or Hard delete). ADMIN only.")
    @DeleteMapping("/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCar(@PathVariable Long carId) {
        carService.deleteById(carId);
    }
}
