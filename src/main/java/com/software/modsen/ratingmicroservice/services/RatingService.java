package com.software.modsen.ratingmicroservice.services;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfoDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.exceptions.RatingNotFoundException;
import com.software.modsen.ratingmicroservice.mappers.RatingMapper;
import com.software.modsen.ratingmicroservice.observer.RatingSubject;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.software.modsen.ratingmicroservice.exceptions.ErrorMessage.*;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RideClient rideClient;
    @Autowired
    private RatingSubject ratingSubject;
    private final RatingMapper RATING_MAPPER = RatingMapper.INSTANCE;

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating getRatingById(long id) {
        return ratingRepository.getRatingById(id)
                .orElseThrow(() -> new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE));
    }

    public Rating saveRating(Source ratingSource, RatingDto ratingDto) {
        Rating newRating = RATING_MAPPER.fromRatingDtoToRating(ratingDto);
        ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingDto.getRideId());
        newRating.setRide(rideFromDb.getBody());
        ratingRepository.save(newRating);
        ratingSubject.notifyObservers(new RatingInfoDto(ratingSource, newRating));

        return newRating;
    }

    public Rating updateRating(long id, RatingDto ratingDto) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);
        ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingDto.getRideId());

        return ratingRepository.save(ratingFromDb.map(rating -> {
            rating = RATING_MAPPER.fromRatingDtoToRating(ratingDto);
            rating.setId(id);
            rating.setRide(rideFromDb.getBody());

            return rating;
        }).orElseThrow(() -> new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE)));
    }

    public Rating patchRating(long id, RatingPatchDto ratingPatchDto) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);
        if (ratingFromDb.isPresent()) {
            Rating updatingRating = ratingFromDb.get();
            RATING_MAPPER.updateRatingFromRatingPatchDto(ratingPatchDto, updatingRating);
            if (ratingPatchDto.getRideId() != null) {
                ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingPatchDto.getRideId());
                updatingRating.setRide(rideFromDb.getBody());
            }

            return ratingRepository.save(updatingRating);
        }

        throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
    }

    public void deleteRatingById(long id) {
        Optional<Rating> ratingFromDb = ratingRepository.findById(id);
        ratingFromDb.ifPresentOrElse(
                rating -> ratingRepository.deleteById(id),
                () -> {
                    throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
                }
        );
    }
}