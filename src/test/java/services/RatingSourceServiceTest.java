package services;

import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.driver.Sex;
import com.software.modsen.ratingmicroservice.entities.driver.car.Car;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarBrand;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarColor;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Currency;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.entities.ride.RideStatus;
import com.software.modsen.ratingmicroservice.exceptions.RatingNotFoundException;
import com.software.modsen.ratingmicroservice.exceptions.RatingSourceNotFoundException;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import com.software.modsen.ratingmicroservice.services.RatingSourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.software.modsen.ratingmicroservice.exceptions.ErrorMessage.RATING_NOT_FOUND_MESSAGE;
import static com.software.modsen.ratingmicroservice.exceptions.ErrorMessage.RATING_SOURCE_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingSourceServiceTest {
    @Mock
    RatingSourceRepository ratingSourceRepository;

    @Mock
    RatingRepository ratingRepository;

    @InjectMocks
    RatingSourceService ratingSourceService;

    RatingSource defaultRating(long ratingSourceId, long ratingId, long rideId, long passengerId, long driverId,
                               Source ratingSource) {
        return new RatingSource(
                ratingSourceId,
                new Rating(
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
                        "comment"),
                ratingSource);
    }

    @Test
    void getAllRatingSourcesTest_ReturnsRatingSources() {
        //given
        List<RatingSource> ratingSources = List.of(defaultRating(1L, 1L, 1L, 1L,
                1L, Source.DRIVER), defaultRating(2L, 1L, 1L, 1L,
                1L, Source.PASSENGER));
        doReturn(ratingSources).when(ratingSourceRepository).findAll();

        //when
        List<RatingSource> ratingSourcesData = ratingSourceService.getAllRatingSources();

        //then
        assertNotNull(ratingSourcesData);
        assertEquals(ratingSources, ratingSourcesData);
        verify(ratingSourceRepository).findAll();
    }

    @Test
    void getRatingSourceByIdTest_WithoutException_ReturnsRatingSource() {
        //given
        long ratingSourceId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, 1L, 1L, 1L,
                1L, Source.DRIVER);
        doReturn(Optional.of(ratingSource)).when(ratingSourceRepository).findRatingSourceById(ratingSourceId);

        //when
        RatingSource ratingSourceData = ratingSourceService.getRatingSourceById(ratingSourceId);

        //then
        assertNotNull(ratingSourceData);
        assertEquals(ratingSource, ratingSourceData);
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
    }

    @Test
    void getRatingSourceByIdTest_WithRatingSourceNotFoundException_ReturnsException() {
        //given
        long ratingSourceId = 1;
        doThrow(new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE))
                .when(ratingSourceRepository).findRatingSourceById(ratingSourceId);

        //when
        RatingSourceNotFoundException exception = assertThrows(RatingSourceNotFoundException.class,
                () -> ratingSourceService.getRatingSourceById(ratingSourceId));

        //then
        assertEquals(RATING_SOURCE_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
    }

    @Test
    void updateRatingSourceTest_WithoutException_ReturnsRatingSource() {
        //given
        long ratingSourceId = 1;
        long ratingId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, ratingId, 1, 1, 1,
                Source.PASSENGER);
        Rating rating = ratingSource.getRating();
        doReturn(Optional.of(rating)).when(ratingRepository).findRatingById(ratingId);
        doReturn(Optional.of(ratingSource)).when(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        doReturn(ratingSource).when(ratingSourceRepository).save(ratingSource);
        ratingSource.setId(0);
        ratingSource.setRating(null);

        //when
        RatingSource ratingSourceData = ratingSourceService.updateRatingSource(ratingSourceId, ratingId, ratingSource);

        //then
        assertNotNull(ratingSourceData);
        assertEquals(ratingSource, ratingSourceData);
        verify(ratingRepository).findRatingById(ratingId);
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        verify(ratingSourceRepository).save(ratingSource);
    }

    @Test
    void updateRatingSourceTest_WithRatingNotFoundException_ReturnsException() {
        //given
        long ratingSourceId = 1;
        long ratingId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, ratingId, 1, 1, 1,
                Source.PASSENGER);
        doThrow(new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE)).when(ratingRepository).findRatingById(ratingId);
        ratingSource.setId(0);
        ratingSource.setRating(null);

        //when
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class,
                () -> ratingSourceService.updateRatingSource(ratingSourceId, ratingId, ratingSource));

        //then
        assertEquals(RATING_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingRepository).findRatingById(ratingId);
        verify(ratingSourceRepository, times(0)).findRatingSourceById(ratingSourceId);
        verify(ratingSourceRepository, times(0)).save(ratingSource);
    }

    @Test
    void updateRatingSourceTest_WithRatingSourceNotFoundException_ReturnsException() {
        //given
        long ratingSourceId = 1;
        long ratingId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, ratingId, 1, 1, 1,
                Source.PASSENGER);
        Rating rating = ratingSource.getRating();
        doReturn(Optional.of(rating)).when(ratingRepository).findRatingById(ratingId);
        doThrow(new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE))
                .when(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        ratingSource.setId(0);
        ratingSource.setRating(null);

        //when
        RatingSourceNotFoundException exception = assertThrows(RatingSourceNotFoundException.class,
                () -> ratingSourceService.updateRatingSource(ratingSourceId, ratingId, ratingSource));

        //then
        assertEquals(RATING_SOURCE_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingRepository).findRatingById(ratingId);
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        verify(ratingSourceRepository, times(0)).save(ratingSource);
    }

    @Test
    void patchRatingSourceTest_WithoutException_ReturnsRatingSource() {
        //given
        long ratingSourceId = 1;
        long ratingId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, ratingId, 1, 1, 1,
                Source.PASSENGER);
        doReturn(Optional.of(ratingSource)).when(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        Rating rating = ratingSource.getRating();
        doReturn(Optional.of(rating)).when(ratingRepository).findRatingById(ratingId);
        doReturn(ratingSource).when(ratingSourceRepository).save(ratingSource);
        ratingSource.setId(0);
        ratingSource.setRating(null);

        //when
        RatingSource ratingSourceData = ratingSourceService.patchRatingSource(ratingSourceId, ratingId, ratingSource);

        //then
        assertNotNull(ratingSourceData);
        assertEquals(ratingSource, ratingSourceData);
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        verify(ratingRepository).findRatingById(ratingId);
        verify(ratingSourceRepository).save(ratingSource);
    }

    @Test
    void patchRatingSourceTest_WithRatingNotFoundException_ReturnsException() {
        //given
        long ratingSourceId = 1;
        long ratingId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, ratingId, 1, 1, 1,
                Source.PASSENGER);
        doThrow(new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE))
                .when(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        ratingSource.setId(0);
        ratingSource.setRating(null);

        //when
        RatingSourceNotFoundException exception = assertThrows(RatingSourceNotFoundException.class,
                () -> ratingSourceService.patchRatingSource(ratingSourceId, ratingId, ratingSource));

        //then
        assertEquals(RATING_SOURCE_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        verify(ratingRepository, never()).findRatingById(ratingId);
        verify(ratingSourceRepository, never()).save(ratingSource);
    }

    @Test
    void patchRatingSourceTest_WithRatingSourceNotFoundException_ReturnsException() {
        //given
        long ratingSourceId = 1;
        long ratingId = 1;
        RatingSource ratingSource = defaultRating(ratingSourceId, ratingId, 1, 1, 1,
                Source.PASSENGER);
        doReturn(Optional.of(ratingSource)).when(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        doThrow(new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE))
                .when(ratingRepository).findRatingById(ratingId);
        ratingSource.setId(0);
        ratingSource.setRating(null);

        //when
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class,
                () -> ratingSourceService.patchRatingSource(ratingSourceId, ratingId, ratingSource));

        //then
        assertEquals(RATING_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(ratingSourceRepository).findRatingSourceById(ratingSourceId);
        verify(ratingRepository).findRatingById(ratingId);
        verify(ratingSourceRepository, never()).save(ratingSource);
    }
}