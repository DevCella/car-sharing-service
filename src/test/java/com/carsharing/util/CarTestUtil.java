package com.carsharing.util;

import com.carsharing.dto.car.CarCreateRequestDto;
import com.carsharing.dto.car.CarInfoResponseDto;
import com.carsharing.dto.car.CarResponseDto;
import com.carsharing.model.Car;
import com.carsharing.model.enums.CarType;
import java.math.BigDecimal;

public class CarTestUtil {
    public static Car createCar() {
        Car car = new Car();
        car.setId(4L);
        car.setModel("Civic");
        car.setBrand("Honda");
        car.setType(CarType.SEDAN);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(100));
        return car;
    }

    public static CarResponseDto createCarResponseDto() {
        return new CarResponseDto(
                4L,
                "baseModel",
                "baseBrand",
                BigDecimal.valueOf(100)
        );
    }

    public static CarInfoResponseDto createCarInfoResponseDto() {
        return new CarInfoResponseDto(
                4L,
                "Civic",
                "Honda",
                CarType.SEDAN,
                5,
                BigDecimal.valueOf(100)
        );
    }

    public static CarCreateRequestDto createCarCreateRequestDto() {
        return new CarCreateRequestDto(
                "Civic",
                "Honda",
                CarType.SEDAN,
                5,
                BigDecimal.valueOf(100)
        );
    }
}
