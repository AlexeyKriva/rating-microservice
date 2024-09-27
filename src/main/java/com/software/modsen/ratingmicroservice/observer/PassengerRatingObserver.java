package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.PassengerRatingClient;
import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.configs.kafka.KafkaTopicConfig;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfoDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class PassengerRatingObserver implements RatingObserver{
    private RideClient rideClient;
    private KafkaTemplate<String, PassengerRatingDto> kafkaTemplate;

    @Override
    public void updateRatingSource(RatingInfoDto ratingInfoDto) {
        if (ratingInfoDto.getRatingSource().equals(Source.DRIVER)) {
            ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingInfoDto.getRating().getRide().getId());

            kafkaTemplate.send(new KafkaTopicConfig().passengerRatingTopic().name(),new PassengerRatingDto(rideFromDb
                    .getBody().getPassenger().getId(), ratingInfoDto.getRating().getRatingValue()));
        }
    }
}
