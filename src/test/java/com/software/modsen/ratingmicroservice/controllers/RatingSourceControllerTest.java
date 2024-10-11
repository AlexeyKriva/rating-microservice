package com.software.modsen.ratingmicroservice.controllers;

import com.software.modsen.ratingmicroservice.controllers.RatingSourceController;
import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.driver.Sex;
import com.software.modsen.ratingmicroservice.entities.driver.car.Car;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarBrand;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarColor;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Currency;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.entities.ride.RideStatus;
import com.software.modsen.ratingmicroservice.mappers.RatingSourceMapper;
import com.software.modsen.ratingmicroservice.services.RatingSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RatingSourceControllerTest {
    @Mock
    RatingSourceService ratingSourceService;

    @Mock
    RatingSourceMapper ratingSourceMapper;

    @InjectMocks
    RatingSourceController ratingSourceController;

    @BeforeEach
    void setUp() {
        ratingSourceMapper = RatingSourceMapper.INSTANCE;
    }

    Passenger passengerWithIdAndIsDeleted(long id, boolean isDeleted) {
        return new Passenger(id, "name" + id, "name" + id + "@gmail.com",
                "+375299312345", isDeleted);
    }

    Driver driverWithIdAndIsDeleted(long id, boolean isDeleted) {
        return new Driver(id, "name" + id, "name" + id + "@gmail.com",
                "+375299312345", Sex.MALE, new Car(id, CarColor.GREEN, CarBrand.AUDI,
                "123" + id + "AB-1",
                isDeleted), isDeleted);
    }

    Ride rideWithId(long id, long passengerId, long driverId) {
        return new Ride(id, passengerWithIdAndIsDeleted(passengerId, false),
                driverWithIdAndIsDeleted(driverId, false),
                "Nezavisimosty 1", "Nezavisimosty 2", RideStatus.CREATED,
                LocalDateTime.of(2024, 10, 1, 12, 0, 0, 0),
                100f, Currency.BYN);
    }

    Rating ratingWithId(long id) {
        return new Rating(id, rideWithId(1, 1, 1),
                5, "New comment");
    }

    RatingSource defaultRatingSource() {
        return new RatingSource(1, ratingWithId(1), Source.DRIVER);
    }

    @Test
    void getAllRatingSourcesTest_ReturnsRatingSources() {
        //given
        List<RatingSource> ratingSources = List.of(
                new RatingSource(1L, ratingWithId(1L), Source.PASSENGER),
                new RatingSource(2L, ratingWithId(2L), Source.DRIVER)
        );
        doReturn(ratingSources).when(ratingSourceService).getAllRatingSources();

        //when
        ResponseEntity<List<RatingSource>> responseEntity = ratingSourceController.getAllRatingSources();

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratingSources, responseEntity.getBody());
        verify(ratingSourceService).getAllRatingSources();
    }

    @Test
    void getRatingSourceByIdTest_ReturnsRatingSource() {
        //given
        long ratingSourceId = 1;
        RatingSource ratingSource = defaultRatingSource();
        doReturn(ratingSource).when(ratingSourceService).getRatingSourceById(ratingSourceId);

        //when
        ResponseEntity<RatingSource> responseEntity = ratingSourceController.getRatingSourceById(ratingSourceId);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratingSource, responseEntity.getBody());
        verify(ratingSourceService).getRatingSourceById(ratingSourceId);
    }

    @Test
    void updateRatingSourceByIdTest_ReturnsRatingSource() {
        //given
        long ratingSourceId = 1;
        RatingSourceDto ratingSourceDto = new RatingSourceDto(1L, Source.PASSENGER);
        RatingSource ratingSource = ratingSourceMapper.fromRatingSourceDtoToRatingSource(ratingSourceDto);
        ratingSource.setId(ratingSourceId);
        ratingSource.setRating(ratingWithId(ratingSourceDto.getRatingId()));
        doReturn(ratingSource).when(ratingSourceService).updateRatingSource(ratingSourceId,
                ratingSourceDto.getRatingId(), ratingSourceMapper.fromRatingSourceDtoToRatingSource(ratingSourceDto));

        //when
        ResponseEntity<RatingSource> responseEntity = ratingSourceController.updateRatingSourceById(ratingSourceId,
                ratingSourceDto);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratingSource, responseEntity.getBody());
        verify(ratingSourceService).updateRatingSource(ratingSourceId,
                ratingSourceDto.getRatingId(), ratingSourceMapper.fromRatingSourceDtoToRatingSource(ratingSourceDto));
    }

    @Test
    void patchRatingSourceByIdTest_ReturnsRatingSource() {
        //given
        long ratingSourceId = 1;
        RatingSourcePatchDto ratingSourcePatchDto = new RatingSourcePatchDto(1L, Source.PASSENGER);
        RatingSource ratingSource = ratingSourceMapper.fromRatingSourcePatchDtoToRatingSource(ratingSourcePatchDto);
        ratingSource.setId(ratingSourceId);
        ratingSource.setRating(ratingWithId(ratingSourcePatchDto.getRatingId()));
        doReturn(ratingSource).when(ratingSourceService).patchRatingSource(ratingSourceId,
                ratingSourcePatchDto.getRatingId(),
                ratingSourceMapper.fromRatingSourcePatchDtoToRatingSource(ratingSourcePatchDto));

        //when
        ResponseEntity<RatingSource> responseEntity = ratingSourceController.patchRatingSourceById(ratingSourceId,
                ratingSourcePatchDto);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratingSource, responseEntity.getBody());
        verify(ratingSourceService).patchRatingSource(ratingSourceId,
                ratingSourcePatchDto.getRatingId(),
                ratingSourceMapper.fromRatingSourcePatchDtoToRatingSource(ratingSourcePatchDto));
    }
}