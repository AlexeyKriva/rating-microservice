package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.entities.rating.RatingInfoDto;

import java.util.ArrayList;
import java.util.List;

public class RatingSubject {
    private final List<RatingObserver> ratingObservers = new ArrayList<>();

    public void addRatingObserver(RatingObserver ratingObserver) {
        ratingObservers.add(ratingObserver);
    }

    public void removeRatingObserver(RatingObserver ratingObserver) {
        ratingObservers.remove(ratingObserver);
    }

    public void notifyObservers(RatingInfoDto ratingInfoDto) {
        for (RatingObserver ratingObserver: ratingObservers) {
            ratingObserver.updateRatingSource(ratingInfoDto);
        }
    }
}
