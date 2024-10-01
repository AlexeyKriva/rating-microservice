package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingMessage;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class PassengerRatingObserver implements RatingObserver{
    private RideClient rideClient;
    private KafkaTemplate<String, PassengerRatingMessage> passengerRatingKafkaTemplate;

    @Override
    public void updateRatingSource(RatingInfo ratingInfo) {
        if (ratingInfo.getRatingSource().equals(Source.DRIVER)) {
            ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingInfo.getRating().getRide().getId());

            PassengerRatingMessage passengerRatingMessage = new PassengerRatingMessage(rideFromDb
                    .getBody().getPassenger().getId(), ratingInfo.getRating().getRatingValue());

            passengerRatingKafkaTemplate.send("passenger-create-rating-topic",
                    String.valueOf(rideFromDb.getBody().getPassenger().getId()),
                    passengerRatingMessage);
        }
    }
}