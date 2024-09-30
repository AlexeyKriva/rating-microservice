package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SourceObserver implements RatingObserver {
    private RatingSourceRepository ratingSourceRepository;

    @Override
    public void updateRatingSource(RatingInfo ratingInfo) {
        RatingSource ratingSource = new RatingSource();
        ratingSource.setRating(ratingInfo.getRating());
        ratingSource.setSource(ratingInfo.getRatingSource());

        ratingSourceRepository.save(ratingSource);
    }
}