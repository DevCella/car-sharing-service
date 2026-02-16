package com.carsharing.service.impl;

import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import com.carsharing.exception.EntityNotFoundException;
import com.carsharing.mapper.CarMapper;
import com.carsharing.model.Car;
import com.carsharing.repository.CarRepository;
import com.carsharing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toDto);
    }

    @Override
    public CarInfoResponseDto findById(Long carId) {
        return carMapper.toInfoDto(carRepository.findById(carId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Could not find car by id: " + carId)));
    }

    @Override
    public CarInfoResponseDto save(CarCreateRequestDto requestDto) {
        return carMapper.toInfoDto(
                carRepository.save(
                        carMapper.toModel(requestDto)));
    }

    @Override
    public CarInfoResponseDto update(Long carId, CarCreateRequestDto requestDto) {
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Car with id: " + carId));
        carMapper.updateFromDto(car, requestDto);
        return carMapper.toInfoDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long carId) {
        carRepository.deleteById(carId);
    }
}
