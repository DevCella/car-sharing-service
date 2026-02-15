package com.carsharing.service;

import com.carsharing.dto.rental.RentalCreateRequestDto;
import com.carsharing.dto.rental.RentalResponseDto;
import com.carsharing.dto.rental.RentalSetActualReturnDateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto findById(Long authUserId, Long rentalId, boolean isAdmin);

    RentalResponseDto save(RentalCreateRequestDto requestDto, Long userId);

    RentalResponseDto setActualReturnDate(Long rentalId, RentalSetActualReturnDateDto requestDto);

    Page<RentalResponseDto> findByUserId(Long authUserId, boolean isAdmin, Long userId,
            Boolean isActive, Pageable pageable);
}
