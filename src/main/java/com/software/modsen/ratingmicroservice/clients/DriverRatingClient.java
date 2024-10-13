package com.software.modsen.ratingmicroservice.clients;

import com.software.modsen.ratingmicroservice.entities.driver.DriverRating;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingMessage;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "driver-microservice")
public interface DriverRatingClient {
    @PutMapping
    ResponseEntity<DriverRating> updateDriverRating(@Valid
                                                    @RequestBody
                                                    DriverRatingMessage driverRatingMessage);
}