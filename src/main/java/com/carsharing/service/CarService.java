package com.carsharing.service;

import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    Page<CarResponseDto> findAll(Pageable pageable);

    CarInfoResponseDto findById(Long carId);

    CarInfoResponseDto save(CarCreateRequestDto requestDto);

    CarInfoResponseDto update(Long carId, CarCreateRequestDto requestDto);

    void deleteById(Long carId);
}
