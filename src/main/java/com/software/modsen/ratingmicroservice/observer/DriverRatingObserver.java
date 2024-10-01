package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingMessage;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class DriverRatingObserver implements RatingObserver {
    private RideClient rideClient;
    private KafkaTemplate<String, DriverRatingMessage> driverKafkaTemplate;

    @Override
    public void updateRatingSource(RatingInfo ratingInfo) {
        if (ratingInfo.getRatingSource().equals(Source.PASSENGER)) {
            ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingInfo.getRating().getRide().getId());

            DriverRatingMessage driverRatingMessageValue = new DriverRatingMessage(rideFromDb.getBody().getDriver().getId(),
                    ratingInfo.getRating().getRatingValue());

            driverKafkaTemplate.send("driver-create-rating-topic", driverRatingMessageValue);
        }
    }
}