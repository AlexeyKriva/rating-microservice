package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingMessage;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfo;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.SimpleRatingSource;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@AllArgsConstructor
public class DriverRatingObserver implements RatingObserver {
    private RideClient rideClient;
    private KafkaTemplate<String, DriverRatingMessage> driverKafkaTemplate;

    @Override
    @Retryable(retryFor = {DataAccessException.class, FeignException.class}, maxAttempts = 5,
            backoff = @Backoff(delay = 500))
    public void updateRatingSource(RatingInfo ratingInfo) {
        if (ratingInfo.getRatingSource().equals(SimpleRatingSource.PASSENGER)) {
            ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingInfo.getRating().getRide().getId());

            DriverRatingMessage driverRatingMessageValue = new DriverRatingMessage(rideFromDb.getBody().getDriver().getId(),
                    ratingInfo.getRating().getRatingValue());

            driverKafkaTemplate.send("driver-create-rating-topic", driverRatingMessageValue);
        }
    }
}