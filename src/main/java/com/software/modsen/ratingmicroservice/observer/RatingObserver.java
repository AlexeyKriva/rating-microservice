package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;

public interface RatingObserver {
    void updateRatingSource(RatingInfo ratingInfo);
}