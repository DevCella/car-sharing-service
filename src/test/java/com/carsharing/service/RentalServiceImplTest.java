package com.carsharing.service;

import static com.carsharing.util.CarTestUtil.createCar;
import static com.carsharing.util.RentalTestUtil.createActualReturnDateDto;
import static com.carsharing.util.RentalTestUtil.createFirstRentalResponseDto;
import static com.carsharing.util.RentalTestUtil.createRental;
import static com.carsharing.util.RentalTestUtil.createRentalRequestDto;
import static com.carsharing.util.UserTestUtil.createCustomerUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.carsharing.dto.rental.RentalCreateRequestDto;
import com.carsharing.dto.rental.RentalResponseDto;
import com.carsharing.dto.rental.RentalSetActualReturnDateDto;
import com.carsharing.mapper.RentalMapper;
import com.carsharing.model.Car;
import com.carsharing.model.Rental;
import com.carsharing.model.User;
import com.carsharing.repository.CarRepository;
import com.carsharing.repository.RentalRepository;
import com.carsharing.repository.UserRepository;
import com.carsharing.service.impl.RentalServiceImpl;
import com.carsharing.telegram.NotificationService;
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
class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("findById should return correct Rental dto")
    void findById_RentalWithIdOne_True() {
        Long rentalId = 1L;
        Long authUserId = 3L;
        boolean isAdmin = false;
        Rental rental = createRental();
        RentalResponseDto expected = createFirstRentalResponseDto();

        when(rentalRepository.findWithCarAndUserById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expected);
        RentalResponseDto result = rentalService.findById(authUserId, rentalId, isAdmin);

        assertEquals(expected, result);
        verify(rentalRepository).findWithCarAndUserById(rentalId);
    }

    @Test
    @DisplayName("save should return correct Rental dto")
    void save_RentalWithCorrectCarAndUser_True() {
        Long carId = 4L;
        Long userId = 3L;
        Car car = createCar();
        User user = createCustomerUser();
        RentalCreateRequestDto requestDto = createRentalRequestDto();
        Rental rental = createRental();
        RentalResponseDto expected = createFirstRentalResponseDto();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rentalMapper.toModel(requestDto)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(rental);
        doNothing().when(notificationService).sendNewRentalNotification(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expected);

        RentalResponseDto result = rentalService.save(requestDto, userId);
        int expectedInventory = 4;

        assertEquals(expectedInventory, car.getInventory());
        assertEquals(expected, result);
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("setActualReturnDate should return correct Rental dto")
    void setActualReturnDate_RentalWithCorrectActualReturnDate_True() {
        Long rentalId = 1L;
        Rental rental = createRental();
        Car car = createCar();
        rental.setCar(car);
        RentalSetActualReturnDateDto requestDto = createActualReturnDateDto();
        RentalResponseDto expectedDto = createFirstRentalResponseDto();

        when(rentalRepository.findWithCarById(rentalId)).thenReturn(Optional.of(rental));
        doNothing().when(rentalMapper).setActualReturnDate(rental, requestDto);
        when(rentalRepository.save(rental)).thenReturn(rental);
        doNothing().when(notificationService).sendReturnedRentalNotification(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expectedDto);

        RentalResponseDto result = rentalService.setActualReturnDate(rentalId, requestDto);
        int expectedInventory = 6;

        assertEquals(expectedInventory, car.getInventory());
        assertFalse(rental.isActive());
        assertEquals(expectedDto, result);
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("findByUserId should return correct Rental dto")
    void findByUserId_RentalWithUserIdThree_True() {
        Long userId = 3L;
        boolean isActive = true;
        boolean isAdmin = false;
        Pageable pageable = PageRequest.of(0, 10);
        Rental firstRental = createRental();
        Rental secondRental = createRental();
        List<Rental> rentalList = List.of(firstRental, secondRental);
        Page<Rental> rentals = new PageImpl<>(rentalList, pageable, 2);

        RentalResponseDto firstDto = createFirstRentalResponseDto();
        RentalResponseDto secondDto = createFirstRentalResponseDto();
        List<RentalResponseDto> dtos = List.of(firstDto, secondDto);

        when(rentalRepository.findAllByUserIdAndIsActive(userId, isActive, pageable))
                .thenReturn(rentals);
        when(rentalMapper.toDto(rentalList.get(0))).thenReturn(dtos.get(0));
        when(rentalMapper.toDto(rentalList.get(1))).thenReturn(dtos.get(1));

        Page<RentalResponseDto> result = rentalService.findByUserId(userId, isAdmin,
                userId, isActive, pageable);
        Page<RentalResponseDto> expectedPage = new PageImpl<>(dtos, pageable, 2);

        assertEquals(expectedPage, result);
        verify(rentalRepository).findAllByUserIdAndIsActive(userId, isActive, pageable);
    }
}
