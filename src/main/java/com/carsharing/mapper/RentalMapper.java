package com.carsharing.mapper;

import com.carsharing.config.MapperConfig;
import com.carsharing.dto.rental.RentalCreateRequestDto;
import com.carsharing.dto.rental.RentalResponseDto;
import com.carsharing.dto.rental.RentalSetActualReturnDateDto;
import com.carsharing.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "user", ignore = true)
    Rental toModel(RentalCreateRequestDto requestDto);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toDto(Rental rental);

    void setActualReturnDate(@MappingTarget Rental rental, RentalSetActualReturnDateDto requestDto);
}
