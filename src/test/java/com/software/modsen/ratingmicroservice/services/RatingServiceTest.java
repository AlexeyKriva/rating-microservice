package com.software.modsen.ratingmicroservice.services;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.driver.Sex;
import com.software.modsen.ratingmicroservice.entities.driver.car.Car;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarBrand;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarColor;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.SimpleRatingSource;
import com.software.modsen.ratingmicroservice.entities.ride.Currency;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.entities.ride.RideStatus;
import com.software.modsen.ratingmicroservice.exceptions.DriverHasNotRatingsException;
import com.software.modsen.ratingmicroservice.exceptions.PassengerHasNotRatingsException;
import com.software.modsen.ratingmicroservice.exceptions.RatingNotFoundException;
import com.software.modsen.ratingmicroservice.observer.RatingSubject;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.software.modsen.ratingmicroservice.exceptions.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    RatingRepository ratingRepository;

    @Mock
    RatingSourceRepository ratingSourceRepository;

    @Mock
    RideClient rideClient;

    @Mock
    RatingSubject ratingSubject;

    @InjectMocks
    RatingService ratingService;

    List<Rating> ratingsWithId(List<Long> ratingsIdes, List<Long> rideIdes,
                               List<Long> passengerIdes, List<Long> driverIdes) {
        return List.of(new Rating(
                        ratingsIdes.get(0),
                        new Ride(
                                rideIdes.get(0),
                                new Passenger(
                                        passengerIdes.get(0),
                                        "name" + passengerIdes.get(0),
                                        "name" + passengerIdes.get(0) + "@gmail.com",
                                        "+37529123457" + passengerIdes.get(0),
                                        false
                                ),
                                new Driver(
                                        driverIdes.get(0),
                                        "name" + driverIdes.get(0),
                                        "name" + driverIdes.get(0) + "@gmail.com",
                                        "+37529123457" + driverIdes.get(0),
                                        Sex.MALE,
                                        new Car(
                                                driverIdes.get(0),
                                                CarColor.GREEN,
                                                CarBrand.AUDI,
                                                "123" + driverIdes.get(0) + "AB-7",
                                                false
                                        ),
                                        false
                                ),
                                "Nezavisimosty " + passengerIdes.get(0),
                                "Nezavisimosty " + (passengerIdes.get(0) + 1),
                                RideStatus.ACCEPTED,
                                LocalDateTime.of(2024, 10, 3, 12, 0, 0),
                                100F,
                                Currency.BYN
                        ),
                        5,
                        "comment"),
                new Rating(
                        ratingsIdes.get(1),
                        new Ride(
                                rideIdes.get(1),
                                new Passenger(
                                        passengerIdes.get(1),
                                        "name" + passengerIdes.get(1),
                                        "name" + passengerIdes.get(1) + "@gmail.com",
                                        "+37529123457" + passengerIdes.get(1),
                                        false
                                ),
                                new Driver(
                                        driverIdes.get(1),
                                        "name" + driverIdes.get(1),
                                        "name" + driverIdes.get(1) + "@gmail.com",
                                        "+37529123457" + driverIdes.get(1),
                                        Sex.MALE,
                                        new Car(
                                                driverIdes.get(1),
                                                CarColor.GREEN,
                                                CarBrand.AUDI,
                                                "123" + driverIdes.get(1) + "AB-7",
                                                false
                                        ),
                                        false
                                ),
                                "Nezavisimosty " + passengerIdes.get(1),
                                "Nezavisimosty " + (passengerIdes.get(1) + 1),
                                RideStatus.ACCEPTED,
                                LocalDateTime.of(2024, 10, 3, 12, 0, 0),
                                100F,
                                Currency.BYN
                        ),
                        5,
                        "comment")
        );
    }

    Rating defaultRating(long ratingId, long rideId, long passengerId, long driverId) {
        return new Rating(
                ratingId,
                new Ride(
                        rideId,
                        new Passenger(
                                passengerId,
                                "name" + passengerId,
                                "name" + passengerId + "@gmail.com",
                                "+37529123457" + passengerId,
                                false
                        ),
                        new Driver(
                                driverId,
                                "name" + driverId,
                                "name" + driverId + "@gmail.com",
                                "+37529123457" + driverId,
                                Sex.MALE,
                                new Car(
                                        driverId,
                                        CarColor.GREEN,
                                        CarBrand.AUDI,
                                        "123" + driverId + "AB-7",
                                        false
                                ),
                                false
                        ),
                        "Nezavisimosty " + passengerId,
                        "Nezavisimosty " + (passengerId + 1),
                        RideStatus.ACCEPTED,
                        LocalDateTime.of(2024, 10, 3, 12, 0, 0),
                        100F,
                        Currency.BYN),
                5,
                "comment");
    }

    Passenger passengerWithIdAndIsDeleted(long id, boolean isDeleted) {
        return new Passenger(id, "name" + id, "name" + id + "@gmail.com",
                "+37529123457" + id, isDeleted);
    }

    Driver driverWithIdAndIsDeleted(long id, boolean isDeleted) {
        return new Driver(id, "name" + id, "name" + id + "@gmail.com",
                "+37529123457" + id, Sex.MALE, new Car(id, CarColor.GREEN, CarBrand.AUDI, "123" + id + "AB-1",
                isDeleted), isDeleted);
    }

    List<Ride> defaultRides(List<Long> passengerIdes, List<Long> drivesIdes) {
        return List.of(
                new Ride(
                        1,
                        passengerWithIdAndIsDeleted(passengerIdes.get(0), false),
                        driverWithIdAndIsDeleted(drivesIdes.get(0), false),
                        "Nezavisimosty 1",
                        "Nezavisimosty 2",
                        RideStatus.CREATED,
                        LocalDateTime.of(2024, 10, 1, 12, 0, 0),
                        100f,
                        Currency.BYN),
                new Ride(
                        2,
                        passengerWithIdAndIsDeleted(passengerIdes.get(1), false),
                        driverWithIdAndIsDeleted(drivesIdes.get(1), false),
                        "Nezavisimosty 1" ,
                        "Nezavisimosty 2",
                        RideStatus.CREATED,
                        LocalDateTime.of(2024, 10, 2, 12, 0, 0),
                        100f,
                        Currency.BYN)
        );
    }

    @Test
    void getAllRatingsTest_ReturnsRatings() {
        //given
        List<Rating> ratings = ratingsWithId(List.of(1L, 2L), List.of(1L, 2L),
                List.of(1L, 2L), List.of(1L, 2L));
        doReturn(ratings).when(ratingRepository).findAll();

        //when
        List<Rating> ratingsData = ratingService.getAllRatings();

        //then
        assertNotNull(ratingsData);
        assertEquals(ratings, ratingsData);
        verify(ratingRepository).findAll();
    }

    @Test
    void getRatingByIdTest_WithoutException_ReturnsRating() {
        //given
        long ratingId = 1;
        Optional<Rating> rating = Optional.of(defaultRating(1, 1, 1, 1));
        doReturn(rating).when(ratingRepository).findRatingById(ratingId);

        //when
        Rating ratingData = ratingService.getRatingById(ratingId);

        //then
        assertNotNull(ratingData);
        assertEquals(rating.get(), ratingData);
        verify(ratingRepository).findRatingById(ratingId);
    }

    @Test
    void getRatingByIdTest_WithRatingNotFoundException_ReturnsException() {
        //given
        long ratingId = 1;
        doThrow(new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE)).when(ratingRepository).findRatingById(ratingId);

        //when
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class,
                () -> ratingService.getRatingById(ratingId));

        //then
        assertEquals(RATING_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    void getAllRatingsByPassengerIdAndBySourceTest_WithoutExceptions_ReturnsRatings() {
        //given
        long passengerId = 1;
        SimpleRatingSource ratingSource = SimpleRatingSource.DRIVER;
        List<Rating> ratings = ratingsWithId(List.of(1L, 2L), List.of(1L, 2L), List.of(1L, 1L), List.of(1L, 2L));
        ResponseEntity<List<Ride>> rides = new ResponseEntity<>(defaultRides(List.of(1L, 1L), List.of(1L, 2L)),
                HttpStatus.OK);
        doReturn(rides).when(rideClient).getAllRidesByPassengerId(passengerId);
        long ratingId = 1;
        for (Ride ride : rides.getBody()) {
            List<Rating> ratingsByRideId = List.of(defaultRating(ratingId++, ride.getId(), ride.getPassenger().getId(),
                    ride.getDriver().getId()));
            doReturn(ratingsByRideId).when(ratingRepository).findRatingsByRideId(ride.getId());
            for (Rating rating : ratingsByRideId) {
                Optional<RatingSource> ratingSourceInstance = Optional.of(
                        new RatingSource(rating.getId(), rating, ratingSource)
                );
                doReturn(ratingSourceInstance).when(ratingSourceRepository).findRatingSourceByRatingIdAndSource(
                        rating.getId(), ratingSource);
            }
        }

        //when
        List<Rating> ratingsData = ratingService.getAllRatingsByPassengerIdAndBySource(passengerId, ratingSource);

        //then
        assertNotNull(ratingsData);
        assertEquals(ratings, ratingsData);
        verify(rideClient).getAllRidesByPassengerId(passengerId);
        verify(ratingRepository, times(2)).findRatingsByRideId(anyLong());
        verify(ratingSourceRepository, times(2)).findRatingSourceByRatingIdAndSource(anyLong(),
                eq(ratingSource));
    }

    @Test
    void getAllRatingsByPassengerIdAndBySourceTest_WithPassengerHasNotRatingsExceptions_ReturnsException() {
        //given
        long passengerId = 1;
        SimpleRatingSource ratingSource = SimpleRatingSource.DRIVER;
        ResponseEntity<List<Ride>> rides = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        doReturn(rides).when(rideClient).getAllRidesByPassengerId(passengerId);

        //when
        PassengerHasNotRatingsException exception = assertThrows(PassengerHasNotRatingsException.class,
                () -> ratingService.getAllRatingsByPassengerIdAndBySource(passengerId, ratingSource));

        //then
        assertEquals(PASSENGER_HAS_NOT_RATINGS_MESSAGE, exception.getMessage());
        verify(rideClient).getAllRidesByPassengerId(passengerId);
        verify(ratingRepository, times(0)).findRatingsByRideId(anyLong());
        verify(ratingSourceRepository, times(0)).findRatingSourceByRatingIdAndSource(anyLong(),
                eq(ratingSource));
    }

    @Test
    void getAllRatingsByDriverIdAndBySourceTest_WithoutExceptions_ReturnsRatings() {
        //given
        long driverId = 1;
        SimpleRatingSource ratingSource = SimpleRatingSource.PASSENGER;
        List<Rating> ratings = ratingsWithId(List.of(1L, 2L), List.of(1L, 2L), List.of(1L, 2L), List.of(1L, 1L));
        ResponseEntity<List<Ride>> rides = new ResponseEntity<>(defaultRides(List.of(1L, 2L), List.of(1L, 1L)),
                HttpStatus.OK);
        doReturn(rides).when(rideClient).getAllRidesByDriverId(driverId);
        long ratingId = 1;
        for (Ride ride : rides.getBody()) {
            List<Rating> ratingsByRideId = List.of(defaultRating(ratingId++, ride.getId(), ride.getPassenger().getId(),
                    ride.getDriver().getId()));
            doReturn(ratingsByRideId).when(ratingRepository).findRatingsByRideId(ride.getId());
            for (Rating rating : ratingsByRideId) {
                Optional<RatingSource> ratingSourceInstance = Optional.of(
                        new RatingSource(rating.getId(), rating, ratingSource)
                );
                doReturn(ratingSourceInstance).when(ratingSourceRepository).findRatingSourceByRatingIdAndSource(
                        rating.getId(), ratingSource);
            }
        }

        //when
        List<Rating> ratingsData = ratingService.getAllRatingsByDriverIdAndBySource(driverId, ratingSource);

        //then
        assertNotNull(ratingsData);
        assertEquals(ratings, ratingsData);
        verify(rideClient).getAllRidesByDriverId(driverId);
        verify(ratingRepository, times(2)).findRatingsByRideId(anyLong());
        verify(ratingSourceRepository, times(2)).findRatingSourceByRatingIdAndSource(anyLong(),
                eq(ratingSource));
    }

    @Test
    void getAllRatingsByDriverIdAndBySourceTest_WithPassengerHasNotRatingsExceptions_ReturnsException() {
        //given
        long driverId = 1;
        SimpleRatingSource ratingSource = SimpleRatingSource.PASSENGER;
        ResponseEntity<List<Ride>> rides = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        doReturn(rides).when(rideClient).getAllRidesByDriverId(driverId);

        //when
        DriverHasNotRatingsException exception = assertThrows(DriverHasNotRatingsException.class,
                () -> ratingService.getAllRatingsByDriverIdAndBySource(driverId, ratingSource));

        //then
        assertEquals(DRIVER_HAS_NOT_RATINGS_MESSAGE, exception.getMessage());
        verify(rideClient).getAllRidesByDriverId(driverId);
        verify(ratingRepository, times(0)).findRatingsByRideId(anyLong());
        verify(ratingSourceRepository, times(0)).findRatingSourceByRatingIdAndSource(anyLong(),
                eq(ratingSource));
    }

    @Test
    void saveRatingTest_WithoutExceptions_ReturnsRating() {
        //given
        SimpleRatingSource ratingSource = SimpleRatingSource.DRIVER;
        long rideId = 1;
        Rating rating = defaultRating(1L, rideId, 1L, 1L);
        ResponseEntity<Ride> rideEntity = new ResponseEntity<>(
                rating.getRide(), HttpStatus.OK
        );
        doReturn(rideEntity).when(rideClient).getRideById(rideId);
        doReturn(rating).when(ratingRepository).save(rating);
        doNothing().when(ratingSubject).notifyObservers(any(RatingInfo.class));
        rating.setId(0);
        rating.setRide(null);

        //when
        Rating ratingData = ratingService.saveRating(ratingSource, rideId, rating);

        //then
        assertNotNull(ratingData);
        assertEquals(rating, ratingData);
        verify(rideClient).getRideById(rideId);
        verify(ratingRepository).save(rating);
        verify(ratingSubject).notifyObservers(any(RatingInfo.class));
    }

    @Test
    void updateRatingTest_WithoutException_ReturnsRating() {
        //given
        long ratingId = 1;
        long rideId = 1;
        Rating rating = defaultRating(ratingId, rideId, 1L, 1L);
        doReturn(Optional.of(rating)).when(ratingRepository).findById(ratingId);
        ResponseEntity<Ride> rideEntity = new ResponseEntity<>(
                rating.getRide(), HttpStatus.OK
        );
        doReturn(rideEntity).when(rideClient).getRideById(rideId);
        doReturn(rating).when(ratingRepository).save(rating);
        rating.setId(0);
        rating.setRide(null);

        //when
        Rating ratingData = ratingService.updateRating(ratingId, rideId, rating);


        //then
        assertNotNull(ratingData);
        assertEquals(rating, ratingData);
        verify(ratingRepository).findById(ratingId);
        verify(rideClient).getRideById(rideId);
        verify(ratingRepository).save(rating);
    }

    @Test
    void updateRatingTest_WithoutRatingNotFoundException_ReturnsException() {
        //given
        long ratingId = 1;
        long rideId = 1;
        Rating rating = defaultRating(ratingId, rideId, 1L, 1L);
        doThrow(new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE)).when(ratingRepository).findById(ratingId);
        rating.setId(0);
        rating.setRide(null);

        //when
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class,
                () -> ratingService.updateRating(ratingId, rideId, rating));

        //then
        assertEquals(RATING_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingRepository).findById(ratingId);
        verify(rideClient, times(0)).getRideById(rideId);
        verify(ratingRepository, times(0)).save(rating);
    }

    @Test
    void patchRatingTest_WithoutException_ReturnsRating() {
        //given
        long ratingId = 1;
        long rideId = 1;
        Rating rating = defaultRating(ratingId, rideId, 1L, 1L);
        doReturn(Optional.of(rating)).when(ratingRepository).findById(ratingId);
        ResponseEntity<Ride> rideEntity = new ResponseEntity<>(
                rating.getRide(), HttpStatus.OK
        );
        doReturn(rideEntity).when(rideClient).getRideById(rideId);
        doReturn(rating).when(ratingRepository).save(rating);
        rating.setId(0);
        rating.setRide(null);

        //when
        Rating ratingData = ratingService.patchRating(ratingId, rideId, rating);

        //then
        assertNotNull(ratingData);
        assertEquals(rating, ratingData);
        verify(ratingRepository).findById(ratingId);
        verify(rideClient).getRideById(rideId);
        verify(ratingRepository).save(rating);
    }

    @Test
    void patchRatingTest_WithoutRatingNotFoundException_ReturnsException() {
        //given
        long ratingId = 1;
        long rideId = 1;
        Rating rating = defaultRating(ratingId, rideId, 1L, 1L);
        doThrow(new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE)).when(ratingRepository).findById(ratingId);
        rating.setId(0);
        rating.setRide(null);

        //when
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class,
                () -> ratingService.patchRating(ratingId, rideId, rating));

        //then
        assertEquals(RATING_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingRepository).findById(ratingId);
        verify(rideClient, times(0)).getRideById(rideId);
        verify(ratingRepository, times(0)).save(rating);
    }

    @Test
    void deleteRatingByIdTest_WithoutException_ReturnVoid() {
        //given
        long ratingId = 1;
        Rating rating = defaultRating(ratingId, 1, 1L, 1L);
        doReturn(Optional.of(rating)).when(ratingRepository).findById(ratingId);
        doNothing().when(ratingSourceRepository).deleteByRatingId(rating.getId());
        doNothing().when(ratingRepository).deleteById(ratingId);

        //when
        ratingService.deleteRatingById(ratingId);

        //then
        verify(ratingRepository).findById(ratingId);
        verify(ratingSourceRepository).deleteByRatingId(rating.getId());
        verify(ratingRepository).deleteById(ratingId);
    }

    @Test
    void deleteRatingByIdTest_WithoutRatingNotFoundException_ReturnVoid() {
        //given
        long ratingId = 1;
        Rating rating = defaultRating(ratingId, 1, 1L, 1L);
        doThrow(new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE)).when(ratingRepository).findById(ratingId);

        //when
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class,
                () -> ratingService.deleteRatingById(ratingId));

        //then
        assertEquals(RATING_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingRepository).findById(ratingId);
        verify(ratingSourceRepository, times(0)).deleteByRatingId(rating.getId());
        verify(ratingRepository, times(0)).deleteById(ratingId);
    }
}