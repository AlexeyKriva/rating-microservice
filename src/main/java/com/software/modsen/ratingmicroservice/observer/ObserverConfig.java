package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingMessage;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingMessage;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ObserverConfig {
    @Bean
    public RatingSubject ratingSubject(RatingSourceRepository ratingSourceRepository, RideClient rideClient,
                                       KafkaTemplate<String, PassengerRatingMessage> passengerRatingKafkaTemplate,
                                       KafkaTemplate<String, DriverRatingMessage> driverRatingKafkaTemplate) {
        RatingSubject ratingSubject = new RatingSubject();
        ratingSubject.addRatingObserver(new SourceObserver(ratingSourceRepository));
        ratingSubject.addRatingObserver(new PassengerRatingObserver(rideClient, passengerRatingKafkaTemplate));
        ratingSubject.addRatingObserver(new DriverRatingObserver(rideClient, driverRatingKafkaTemplate));

        return ratingSubject;
    }
}