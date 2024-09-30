package com.software.modsen.ratingmicroservice.services;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import com.software.modsen.ratingmicroservice.exceptions.RatingNotFoundException;
import com.software.modsen.ratingmicroservice.exceptions.RatingSourceNotFoundException;
import com.software.modsen.ratingmicroservice.mappers.RatingSourceMapper;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.software.modsen.ratingmicroservice.exceptions.ErrorMessage.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RatingSourceService {
    private RatingSourceRepository ratingSourceRepository;
    private RatingRepository ratingRepository;

    public List<RatingSource> getAllRatingSources() {
        return ratingSourceRepository.findAll();
    }

    public RatingSource getRatingSourceById(long id) {
        Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceById(id);

        return ratingSourceFromDb.orElseThrow(
                () -> new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE)
        );
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public RatingSource updateRatingSource(long id, Long ratingId, RatingSource updatingRatingSource) {
        Optional<Rating> ratingFromDb = ratingRepository.findRatingById(ratingId);

        if (ratingFromDb.isPresent()) {
            Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceById(id);

            if (ratingSourceFromDb.isPresent()) {

                updatingRatingSource.setId(id);
                updatingRatingSource.setRating(ratingFromDb.get());

                return ratingSourceRepository.save(updatingRatingSource);
            }

            throw new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE);
        }

        throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public RatingSource patchRatingSource(long id, Long ratingId, RatingSource updatingRatingSource) {
        Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceById(id);

        if (ratingSourceFromDb.isPresent()) {
            updatingRatingSource.setId(id);

            Optional<Rating> ratingFromDb;

            ratingFromDb = ratingRepository.findRatingById(Objects.requireNonNullElseGet(ratingId,
                    () -> ratingSourceFromDb.get().getRating().getId()));

            if (ratingFromDb.isPresent()) {
                updatingRatingSource.setRating(ratingFromDb.get());
            } else {
                throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
            }

            if (updatingRatingSource.getSource() == null) {
                updatingRatingSource.setSource(ratingSourceFromDb.get().getSource());
            }

            return ratingSourceRepository.save(updatingRatingSource);
        }

        throw new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE);
    }
}