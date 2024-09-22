package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.entities.rating.RatingInfoDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@AllArgsConstructor
public class SourceObserver implements RatingObserver {
    private RatingSourceRepository ratingSourceRepository;

    @Override
    public void updateRatingSource(RatingInfoDto ratingInfoDto) {
        RatingSource ratingSource = new RatingSource();
        ratingSource.setRating(ratingInfoDto.getRating());
        ratingSource.setSource(ratingInfoDto.getRatingSource());

        ratingSourceRepository.save(ratingSource);
    }
}
