package com.carsharing.mapper;

import com.carsharing.config.MapperConfig;
import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import com.carsharing.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toDto(Car car);

    Car toModel(CarCreateRequestDto requestDto);

    CarInfoResponseDto toInfoDto(Car car);

    void updateFromDto(@MappingTarget Car car, CarCreateRequestDto requestDto);
}
