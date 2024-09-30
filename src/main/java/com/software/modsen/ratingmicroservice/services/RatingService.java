package com.software.modsen.ratingmicroservice.services;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.exceptions.DriverHasNotRatingsException;
import com.software.modsen.ratingmicroservice.exceptions.PassengerHasNotRatingsException;
import com.software.modsen.ratingmicroservice.exceptions.RatingNotFoundException;
import com.software.modsen.ratingmicroservice.observer.RatingSubject;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
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

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating getRatingById(long id) {
        return ratingRepository.findRatingById(id)
                .orElseThrow(() -> new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE));
    }

    public List<Rating> getAllRatingsByPassengerIdAndBySource(long passengerId, Source ratingSource) {
        List<Ride> ridesFromDb = rideClient.getAllRidesByPassengerId(passengerId).getBody();
        List<Rating> passengerRatings = getAllRatingsBySource(ratingSource, ridesFromDb);

        if (passengerRatings.isEmpty()) {
            throw new PassengerHasNotRatingsException(PASSENGER_HAS_NOT_RATINGS_MESSAGE);
        }

        return passengerRatings;
    }

    public List<Rating> getAllRatingsByDriverIdAndBySource(long driverId, Source ratingSource) {
        List<Ride> ridesFromDb = rideClient.getAllRidesByDriverId(driverId).getBody();
        List<Rating> driverRatings = getAllRatingsBySource(ratingSource, ridesFromDb);

        if (driverRatings.isEmpty()) {
            throw new DriverHasNotRatingsException(DRIVER_HAS_NOT_RATINGS_MESSAGE);
        }

        return driverRatings;
    }

    private List<Rating> getAllRatingsBySource(Source ratingSource, List<Ride> ridesFromDb) {
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

    @Retryable(retryFor = {DataAccessException.class, FeignException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 500))
    @Transactional
    public Rating saveRating(Source ratingSource, Long rideId, Rating newRating) {
        ResponseEntity<Ride> rideFromDb = rideClient.getRideById(rideId);

        newRating.setRide(rideFromDb.getBody());

        ratingRepository.save(newRating);
        ratingSubject.notifyObservers(new RatingInfo(ratingSource, newRating));

        return newRating;
    }

    @Retryable(retryFor = {DataAccessException.class, FeignException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 500))
    @Transactional
    public Rating updateRating(long id, Long rideId, Rating updatingRating) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);

        ResponseEntity<Ride> rideFromDb = rideClient.getRideById(rideId);

        if (ratingFromDb.isPresent()) {
            updatingRating.setId(id);
            updatingRating.setRide(rideFromDb.getBody());

            return ratingRepository.save(updatingRating);
        }

        throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class, FeignException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 500))
    @Transactional
    public Rating patchRating(long id, Long rideId, Rating updatingRating) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);

        if (ratingFromDb.isPresent()) {
            updatingRating.setId(id);

            ResponseEntity<Ride> rideFromDb;

            rideFromDb = rideClient.getRideById(Objects.requireNonNullElseGet(rideId,
                    () -> ratingFromDb.get().getRide().getId()));

            updatingRating.setRide(rideFromDb.getBody());

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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
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
}