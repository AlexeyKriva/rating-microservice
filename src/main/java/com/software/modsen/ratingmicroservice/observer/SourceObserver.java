package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@AllArgsConstructor
public class SourceObserver implements RatingObserver {
    private RatingSourceRepository ratingSourceRepository;

    @Override
    @Retryable(retryFor = {DataAccessException.class, FeignException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 500))
    public void updateRatingSource(RatingInfo ratingInfo) {
        RatingSource ratingSource = new RatingSource();
        ratingSource.setRating(ratingInfo.getRating());
        ratingSource.setSource(ratingInfo.getRatingSource());

        ratingSourceRepository.save(ratingSource);
    }
}