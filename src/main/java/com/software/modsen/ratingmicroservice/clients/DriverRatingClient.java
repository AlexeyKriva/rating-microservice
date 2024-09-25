package com.software.modsen.ratingmicroservice.clients;

import com.software.modsen.ratingmicroservice.entities.driver.DriverRating;
import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "driver-microservice", url = "http://localhost:8082/api/driver/rating")
public interface DriverRatingClient {
    @PutMapping
    ResponseEntity<DriverRating> updateDriverRating(@Valid
                                                    @RequestBody
                                                    DriverRatingDto driverRatingDto);
}