package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.DriverRatingClient;
import com.software.modsen.ratingmicroservice.clients.PassengerRatingClient;
import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingDto;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingDto;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ObserverConfig {
    @Bean
    public RatingSubject ratingSubject(RatingSourceRepository ratingSourceRepository, RideClient rideClient,
                                       KafkaTemplate<String, PassengerRatingDto> passengerKafkaTemplate,
                                       KafkaTemplate<String, DriverRatingDto> driverKafkaTemplate) {
        RatingSubject ratingSubject = new RatingSubject();
        ratingSubject.addRatingObserver(new SourceObserver(ratingSourceRepository));
        ratingSubject.addRatingObserver(new PassengerRatingObserver(rideClient, passengerKafkaTemplate));
        ratingSubject.addRatingObserver(new DriverRatingObserver(rideClient, driverKafkaTemplate));

        return ratingSubject;
    }
}