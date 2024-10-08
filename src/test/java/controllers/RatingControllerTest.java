package controllers;

import com.software.modsen.ratingmicroservice.controllers.RatingController;
import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.driver.Sex;
import com.software.modsen.ratingmicroservice.entities.driver.car.Car;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarBrand;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarColor;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Currency;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.entities.ride.RideStatus;
import com.software.modsen.ratingmicroservice.mappers.RatingMapper;
import com.software.modsen.ratingmicroservice.services.RatingService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingControllerTest {
    @Mock
    RatingService ratingService;

    @Mock
    RatingMapper ratingMapper;

    @InjectMocks
    RatingController ratingController;

    @BeforeEach
    void setUp() {
        ratingMapper = RatingMapper.INSTANCE;
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

    Rating defaultRating() {
        return new Rating(1, rideWithId(1, 1, 1), 5, "New comment");
    }

    List<Rating> defaultRatings(List<Long> passengerIdes, List<Long> driverIdes) {
        return List.of(new Rating(1, rideWithId(1, passengerIdes.get(0), driverIdes.get(0)),
                        4, "New comment"),
                new Rating(2, rideWithId(2, passengerIdes.get(1), driverIdes.get(1)),
                        5, "New favourite comment"));
    }

    @Test
    void getAllRatingsTest_ReturnsRatings() {
        //given
        List<Rating> ratings = defaultRatings(List.of(1L, 2L), List.of(1L, 2L));
        doReturn(ratings).when(ratingService).getAllRatings();

        //when
        ResponseEntity<List<Rating>> responseEntity = ratingController.getAllRatings();

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratings, responseEntity.getBody());
        verify(ratingService).getAllRatings();
    }

    @Test
    void getRatingByIdTest_ReturnsRating() {
        //given
        long ratingId = 1;
        Rating rating = defaultRating();
        doReturn(rating).when(ratingService).getRatingById(ratingId);

        //when
        ResponseEntity<Rating> responseEntity = ratingController.getRatingById(ratingId);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rating, responseEntity.getBody());
        verify(ratingService).getRatingById(ratingId);
    }

    @Test
    void getAllRatingsByPassengerIdAndBySourceTest_ReturnsRatings() {
        //given
        long passengerId = 1;
        Source ratingSource = Source.DRIVER;
        List<Rating> ratings = defaultRatings(List.of(1L, 1L), List.of(1L, 2L));
        doReturn(ratings).when(ratingService).getAllRatingsByPassengerIdAndBySource(passengerId, ratingSource);

        //when
        ResponseEntity<List<Rating>> responseEntity = ratingController.getAllRatingsByPassengerIdAndBySource(passengerId,
                ratingSource);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratings, responseEntity.getBody());
        verify(ratingService).getAllRatingsByPassengerIdAndBySource(passengerId, ratingSource);
        assertTrue(responseEntity.getBody().stream().allMatch(
                rating -> rating.getRide().getPassenger().getId() == passengerId
        ));
    }

    @Test
    void getAllRatingsByDriverIdAndBySourceTest_ReturnsRatings() {
        //given
        long driverId = 1;
        Source ratingSource = Source.PASSENGER;
        List<Rating> ratings = defaultRatings(List.of(1L, 2L), List.of(1L, 1L));
        doReturn(ratings).when(ratingService).getAllRatingsByDriverIdAndBySource(driverId, ratingSource);

        //when
        ResponseEntity<List<Rating>> responseEntity = ratingController.getAllRatingsByDriverIdAndBySource(driverId,
                ratingSource);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(ratings, responseEntity.getBody());
        verify(ratingService).getAllRatingsByDriverIdAndBySource(driverId, ratingSource);
        assertTrue(responseEntity.getBody().stream().allMatch(
                rating -> rating.getRide().getDriver().getId() == driverId
        ));
    }

    @Test
    void saveRatingTest_ReturnsRating() {
        //given
        RatingDto ratingDto = new RatingDto(1L, 5, "Good ride");
        Source ratingSource = Source.PASSENGER;
        Rating rating = ratingMapper.fromRatingDtoToRating(ratingDto);
        rating.setId(1);
        rating.setRide(rideWithId(1, 1, 1));
        doReturn(rating).when(ratingService).saveRating(ratingSource, ratingDto.getRideId(),
                ratingMapper.fromRatingDtoToRating(ratingDto));

        //when
        ResponseEntity<Rating> responseEntity = ratingController.saveRating(ratingSource, ratingDto);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rating, responseEntity.getBody());
        verify(ratingService).saveRating(ratingSource, ratingDto.getRideId(),
                ratingMapper.fromRatingDtoToRating(ratingDto));
    }

    @Test
    void updateRatingByIdTest_ReturnsRating() {
        //given
        long ratingId = 1;
        RatingDto ratingDto = new RatingDto(1L, 5, "Good ride");
        Rating rating = ratingMapper.fromRatingDtoToRating(ratingDto);
        rating.setId(ratingId);
        rating.setRide(rideWithId(ratingDto.getRideId(), 1, 1));
        doReturn(rating).when(ratingService).updateRating(ratingId, ratingDto.getRideId(),
                ratingMapper.fromRatingDtoToRating(ratingDto));

        //when
        ResponseEntity<Rating> responseEntity = ratingController.updateRatingById(ratingId, ratingDto);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rating, responseEntity.getBody());
        verify(ratingService).updateRating(ratingId, ratingDto.getRideId(),
                ratingMapper.fromRatingDtoToRating(ratingDto));
    }

    @Test
    void patchRatingByIdTest_ReturnsRating() {
        //given
        long ratingId = 1;
        RatingPatchDto ratingPatchDto = new RatingPatchDto(1L, 5, "Good ride");
        Rating rating = ratingMapper.fromRatingPatchDtoToRating(ratingPatchDto);
        rating.setId(ratingId);
        rating.setRide(rideWithId(ratingPatchDto.getRideId(), 1, 1));
        doReturn(rating).when(ratingService).patchRating(ratingId, ratingPatchDto.getRideId(),
                ratingMapper.fromRatingPatchDtoToRating(ratingPatchDto));

        //when
        ResponseEntity<Rating> responseEntity = ratingController.patchRatingById(ratingId, ratingPatchDto);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rating, responseEntity.getBody());
        verify(ratingService).patchRating(ratingId, ratingPatchDto.getRideId(),
                ratingMapper.fromRatingPatchDtoToRating(ratingPatchDto));
    }

    @Test
    void deleteRatingByIdTest_ReturnsString() {
        //given
        long ratingId = 1;
        doNothing().when(ratingService).deleteRatingById(ratingId);
        String result = "Rating was successfully deleted by id " + ratingId;

        //when
        ResponseEntity<String> responseEntity = ratingController.deleteRatingById(ratingId);

        //then
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(result, responseEntity.getBody());
        verify(ratingService).deleteRatingById(ratingId);
    }
}