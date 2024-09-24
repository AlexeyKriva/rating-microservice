package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.DriverRatingClient;
import com.software.modsen.ratingmicroservice.clients.PassengerRatingClient;
import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {
    @Bean
    public RatingSubject ratingSubject(RatingSourceRepository ratingSourceRepository, RideClient rideClient,
                                       PassengerRatingClient passengerClient, DriverRatingClient driverRatingClient) {
        RatingSubject ratingSubject = new RatingSubject();
        ratingSubject.addRatingObserver(new SourceObserver(ratingSourceRepository));
        ratingSubject.addRatingObserver(new PassengerRatingObserver(rideClient, passengerClient));
        ratingSubject.addRatingObserver(new DriverRatingObserver(rideClient, driverRatingClient));

        return ratingSubject;
    }
}
