package com.carsharing.controller;

import com.carsharing.dto.rental.RentalCreateRequestDto;
import com.carsharing.dto.rental.RentalResponseDto;
import com.carsharing.dto.rental.RentalSetActualReturnDateDto;
import com.carsharing.model.User;
import com.carsharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental Management", description = "Endpoints for car rental operations")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @Operation(summary = "Get rental by ID",
            description = "Retrieve details of a specific rental."
                    + " Customers can only view their own.")
    @GetMapping("/{rentalId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public RentalResponseDto getRentalById(Authentication authentication,
            @PathVariable Long rentalId) {
        User user = (User) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        return rentalService.findById(user.getId(), rentalId, isAdmin);
    }

    @Operation(summary = "Create a new rental",
            description = "Create a rental record. Automatically decreases car inventory by 1.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public RentalResponseDto addRental(Authentication authentication,
            @RequestBody @Valid RentalCreateRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.save(requestDto, user.getId());
    }

    @Operation(summary = "Return a car",
            description = "Set the actual return date for a rental. Increases car inventory by 1.")
    @PostMapping("/{rentalId}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public RentalResponseDto setActualReturnDate(@PathVariable Long rentalId,
            @RequestBody @Valid RentalSetActualReturnDateDto requestDto) {
        return rentalService.setActualReturnDate(rentalId, requestDto);
    }

    @Operation(summary = "Get rentals by user ID",
            description = "Retrieve rentals for a specific user. "
                    + "Admins can view any user's rentals.")
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public Page<RentalResponseDto> getRentalByUserId(Authentication authentication,
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") boolean isActive,
            @ParameterObject Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        boolean isAdmin = isAdmin(authentication);
        Long targetUserId = (isAdmin && userId != null) ? userId : user.getId();
        return rentalService.findByUserId(user.getId(), isAdmin, targetUserId, isActive, pageable);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }
}
