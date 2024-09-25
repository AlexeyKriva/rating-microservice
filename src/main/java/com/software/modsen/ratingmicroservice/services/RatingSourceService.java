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
import java.util.Optional;

@Service
@AllArgsConstructor
public class RatingSourceService {
    private RatingSourceRepository ratingSourceRepository;
    private RatingRepository ratingRepository;
    private final RatingSourceMapper RATING_SOURCE_MAPPER = RatingSourceMapper.INSTANCE;

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
    public RatingSource updateRatingSource(long id, RatingSourceDto ratingSourceDto) {
        Optional<Rating> ratingFromDb = ratingRepository.findRatingById(ratingSourceDto.getRatingId());

        if (ratingFromDb.isPresent()) {
            Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceById(id);

            if (ratingSourceFromDb.isPresent()) {
                RatingSource updatingRatingSource =
                        RATING_SOURCE_MAPPER.fromRatingSourceDtoToRatingSource(ratingSourceDto);

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
    public RatingSource patchRatingSource(long id, RatingSourcePatchDto ratingSourcePatchDto) {
        Optional<Rating> ratingFromDb = ratingRepository.findRatingById(ratingSourcePatchDto.getRatingId());

        if (ratingFromDb.isPresent()) {
            Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceById(id);

            if (ratingSourceFromDb.isPresent()) {
                RatingSource updatingRatingSource = ratingSourceFromDb.get();

                RATING_SOURCE_MAPPER.updateRatingSourceFromRatingSourcePatchDto(ratingSourcePatchDto,
                        updatingRatingSource);

                if (ratingSourcePatchDto.getRatingId() != null) {
                    updatingRatingSource.setRating(ratingFromDb.get());
                }

                return ratingSourceRepository.save(updatingRatingSource);
            }

            throw new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE);
        }

        throw new RatingNotFoundException(RATING_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public void deleteRatingSourceById(long id) {
        Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.findRatingSourceById(id);

        ratingSourceFromDb.ifPresentOrElse(
                ratingSource -> ratingSourceRepository.deleteById(id),
                () -> { throw new RatingSourceNotFoundException(RATING_SOURCE_NOT_FOUND_MESSAGE);}
        );
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForSaveAndPut(DataAccessException exception,
                                                                          RatingSourceDto ratingSourceDto) {
        return new ResponseEntity<>(CANNOT_SAVE_RATING_SOURCE_MESSAGE + ratingSourceDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForPatch(DataAccessException exception,
                                                                     RatingSourcePatchDto ratingSourcePatchDto) {
        return new ResponseEntity<>(CANNOT_PATCH_RATING_SOURCE_MESSAGE + ratingSourcePatchDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForDelete(DataAccessException exception,
                                                                      long id) {
        return new ResponseEntity<>(CANNOT_DELETE_RATING_SOURCE_MESSAGE + id,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}