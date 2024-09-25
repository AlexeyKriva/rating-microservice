package com.software.modsen.ratingmicroservice.observer;

import com.software.modsen.ratingmicroservice.clients.DriverRatingClient;
import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingInfoDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class DriverRatingObserver implements RatingObserver {
    private RideClient rideClient;
    private DriverRatingClient driverRatingClient;

    @Override
    public void updateRatingSource(RatingInfoDto ratingInfoDto) {
        if (ratingInfoDto.getRatingSource().equals(Source.PASSENGER)) {
            ResponseEntity<Ride> rideFromDb = rideClient.getRideById(ratingInfoDto.getRating().getRide().getId());

            driverRatingClient.updateDriverRating(new DriverRatingDto(rideFromDb.getBody().getDriver().getId(),
                    ratingInfoDto.getRating().getRatingValue()));
        }
    }
}
