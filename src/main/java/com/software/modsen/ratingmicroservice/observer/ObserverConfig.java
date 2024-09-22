package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {
    @Bean
    public RatingSubject ratingSubject(RatingSourceRepository ratingSourceRepository) {
        RatingSubject ratingSubject = new RatingSubject();
        ratingSubject.addRatingObserver(new SourceObserver(ratingSourceRepository));

        return ratingSubject;
    }
}
