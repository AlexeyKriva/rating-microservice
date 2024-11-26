package com.software.modsen.ratingmicroservice.services;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.SimpleRatingSource;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.exceptions.DatabaseConnectionRefusedException;
import com.software.modsen.ratingmicroservice.exceptions.DriverHasNotRatingsException;
import com.software.modsen.ratingmicroservice.exceptions.PassengerHasNotRatingsException;
import com.software.modsen.ratingmicroservice.exceptions.RatingNotFoundException;
import com.software.modsen.ratingmicroservice.observer.RatingSubject;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.software.modsen.ratingmicroservice.exceptions.ErrorMessage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RatingService {
    private RatingRepository ratingRepository;
    private RatingSourceRepository ratingSourceRepository;
    private RideClient rideClient;
    private RatingSubject ratingSubject;

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public Rating getRatingById(long id) {
        return ratingRepository.findRatingById(id)
                .orElseThrow(() -> new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE));
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<Rating> getAllRatingsByPassengerIdAndBySource(long passengerId, SimpleRatingSource ratingSource) {
        List<Ride> ridesFromDb = rideClient.getAllRidesByPassengerId(passengerId).getBody();
        List<Rating> passengerRatings = getAllRatingsBySource(ratingSource, ridesFromDb);

        if (passengerRatings.isEmpty()) {
            throw new PassengerHasNotRatingsException(PASSENGER_HAS_NOT_RATINGS_MESSAGE);
        }

        return passengerRatings;
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<Rating> getAllRatingsByDriverIdAndBySource(long driverId, SimpleRatingSource ratingSource) {
        List<Ride> ridesFromDb = rideClient.getAllRidesByDriverId(driverId).getBody();
        List<Rating> driverRatings = getAllRatingsBySource(ratingSource, ridesFromDb);

        if (driverRatings.isEmpty()) {
            throw new DriverHasNotRatingsException(DRIVER_HAS_NOT_RATINGS_MESSAGE);
        }

        return driverRatings;
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    private List<Rating> getAllRatingsBySource(SimpleRatingSource ratingSource, List<Ride> ridesFromDb) {
        List<Rating> userRatings = new ArrayList<>();

        for (Ride rideFromDb : ridesFromDb) {
            List<Rating> ratingsFromDb = ratingRepository.findRatingsByRideId(rideFromDb.getId());

            for (Rating ratingFromDb: ratingsFromDb) {
                Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceByRatingIdAndSource(
                        ratingFromDb.getId(), ratingSource);

                if (ratingSourceFromDb.isPresent()) {
                    userRatings.add(ratingFromDb);
                }
            }
        }

        return userRatings;
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Rating saveRating(SimpleRatingSource ratingSource, Long rideId, Rating newRating) {
        ResponseEntity<Ride> rideFromDb = rideClient.getRideById(rideId);

        newRating.setRideId(rideFromDb.getBody().getId());

        newRating = ratingRepository.save(newRating);
        ratingSubject.notifyObservers(new RatingInfo(ratingSource, newRating));

        return newRating;
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Rating updateRating(long id, Long rideId, Rating updatingRating) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);

        if (ratingFromDb.isPresent()) {
            updatingRating.setId(id);

            ResponseEntity<Ride> rideFromDb = rideClient.getRideById(rideId);

            updatingRating.setRideId(rideFromDb.getBody().getId());

            return ratingRepository.save(updatingRating);
        }

        throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Rating patchRating(long id, Long rideId, Rating updatingRating) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);

        if (ratingFromDb.isPresent()) {
            updatingRating.setId(id);

            ResponseEntity<Ride> rideFromDb;

            rideFromDb = rideClient.getRideById(Objects.requireNonNullElseGet(rideId,
                    () -> ratingFromDb.get().getRideId()));

            updatingRating.setRideId(rideFromDb.getBody().getId());

            if (updatingRating.getRatingValue() == null) {
                updatingRating.setRatingValue(ratingFromDb.get().getRatingValue());
            }
            if (updatingRating.getComment() == null) {
                updatingRating.setComment(ratingFromDb.get().getComment());
            }

            return ratingRepository.save(updatingRating);
        }

        throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public void deleteRatingById(long id) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);

        ratingFromDb.ifPresentOrElse(
                rating -> {
                    ratingSourceRepository.deleteByRatingId(ratingFromDb.get().getId());
                    ratingRepository.deleteById(id);
                    },
                () -> {
                    throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
                }
        );
    }

    @Recover
    public Rating fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof FeignException) {
            throw (FeignException) throwable;
        } else if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }

    @Recover
    public List<Rating> recoverToPSQLException(Throwable throwable) {
        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_GET_DATA_MESSAGE);
    }
}