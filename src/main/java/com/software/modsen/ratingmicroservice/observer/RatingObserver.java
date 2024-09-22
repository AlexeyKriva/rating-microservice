package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfoDto;

public interface RatingObserver {
    void updateRatingSource(RatingInfoDto ratingInfoDto);
}
