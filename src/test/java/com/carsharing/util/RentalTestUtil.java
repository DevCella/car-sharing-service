package com.carsharing.util;

import static com.carsharing.util.CarTestUtil.createCar;
import static com.carsharing.util.UserTestUtil.createCustomerUser;

import com.carsharing.dto.rental.RentalCreateRequestDto;
import com.carsharing.dto.rental.RentalResponseDto;
import com.carsharing.dto.rental.RentalSetActualReturnDateDto;
import com.carsharing.model.Rental;
import java.time.LocalDate;

public class RentalTestUtil {
    public static Rental createRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2025, 8, 1));
        rental.setReturnDate(LocalDate.of(2025, 9, 1));
        rental.setActualReturnDate(LocalDate.of(2025, 9, 1));
        rental.setCar(createCar());
        rental.setUser(createCustomerUser());
        rental.setActive(true);
        return rental;
    }

    public static RentalResponseDto createFirstRentalResponseDto() {
        return new RentalResponseDto(
                1L,
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 9, 1),
                4L,
                3L
        );
    }

    public static RentalCreateRequestDto createRentalRequestDto() {
        return new RentalCreateRequestDto(
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 9, 1),
                4L
        );
    }

    public static RentalSetActualReturnDateDto createActualReturnDateDto() {
        return new RentalSetActualReturnDateDto(
                LocalDate.of(2025, 9, 1)
        );
    }
}
