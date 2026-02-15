package com.carsharing.service;

import static com.carsharing.util.CarTestUtil.createCar;
import static com.carsharing.util.CarTestUtil.createCarCreateRequestDto;
import static com.carsharing.util.CarTestUtil.createCarInfoResponseDto;
import static com.carsharing.util.CarTestUtil.createCarResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import com.carsharing.mapper.CarMapper;
import com.carsharing.model.Car;
import com.carsharing.repository.CarRepository;
import com.carsharing.service.impl.CarServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("findAll should return correct amount of cars")
    void findAll_TwoCars_True() {
        Car firstCar = createCar();
        Car secondCar = createCar();
        List<Car> cars = List.of(firstCar, secondCar);
        CarResponseDto firstDto = createCarResponseDto();
        CarResponseDto secondDto = createCarResponseDto();
        List<CarResponseDto> dtos = List.of(firstDto, secondDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(cars, pageable, 2);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(cars.get(0))).thenReturn(dtos.get(0));
        when(carMapper.toDto(cars.get(1))).thenReturn(dtos.get(1));
        Page<CarResponseDto> result = carService.findAll(pageable);

        Page<CarResponseDto> expected = new PageImpl<>(dtos, pageable, 2);
        assertEquals(expected, result);
        verify(carRepository).findAll(pageable);
    }

    @Test
    @DisplayName("findById should return correct Car")
    void findById_CarWithIdOne_True() {
        Long id = 1L;
        Car car = createCar();
        car.setId(id);

        // Створюємо новий об'єкт через конструктор замість setId()
        CarInfoResponseDto baseDto = createCarInfoResponseDto();
        CarInfoResponseDto expected = new CarInfoResponseDto(
                id,
                baseDto.model(),
                baseDto.brand(),
                baseDto.type(),
                baseDto.inventory(),
                baseDto.dailyFee()
        );

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carMapper.toInfoDto(car)).thenReturn(expected);
        CarInfoResponseDto result = carService.findById(id);

        assertEquals(expected, result);
        verify(carRepository).findById(id);
    }

    @Test
    @DisplayName("save should return correct Car dto")
    void save_CarInfoResponseDtoWithIdOne_True() {
        CarCreateRequestDto createDto = createCarCreateRequestDto();
        Car car = createCar();
        CarInfoResponseDto expected = createCarInfoResponseDto();

        when(carMapper.toModel(createDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toInfoDto(car)).thenReturn(expected);
        CarInfoResponseDto result = carService.save(createDto);

        assertEquals(expected, result);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("update should return correct Car dto")
    void update_CarCreateRequestDtoChangeModel_True() {
        Long id = 1L;
        String newModel = "newModel";
        Car car = createCar();
        Car changedCar = createCar();
        changedCar.setModel(newModel);

        CarCreateRequestDto baseRequest = createCarCreateRequestDto();
        CarCreateRequestDto requestDto = new CarCreateRequestDto(
                newModel,
                baseRequest.brand(),
                baseRequest.type(),
                baseRequest.inventory(),
                baseRequest.dailyFee()
        );

        CarInfoResponseDto baseInfo = createCarInfoResponseDto();
        CarInfoResponseDto expected = new CarInfoResponseDto(
                id,
                newModel,
                baseInfo.brand(),
                baseInfo.type(),
                baseInfo.inventory(),
                baseInfo.dailyFee()
        );

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        doNothing().when(carMapper).updateFromDto(car, requestDto);
        when(carRepository.save(car)).thenReturn(changedCar);
        when(carMapper.toInfoDto(changedCar)).thenReturn(expected);

        CarInfoResponseDto result = carService.update(id, requestDto);

        assertEquals(expected, result);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("delete should remove correct car")
    void delete_CarWithIdOne_True() {
        Long id = 1L;
        carService.deleteById(id);
        verify(carRepository).deleteById(id);
    }
}
