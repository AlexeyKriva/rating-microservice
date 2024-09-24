package com.software.modsen.ratingmicroservice.clients;

import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRating;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "passenger-microservice", url = "http://localhost:8081/api/passenger/rating")
public interface PassengerRatingClient {
    @PutMapping
    ResponseEntity<PassengerRating> updatePassengerRating(@Valid
                                                          @RequestBody
                                                          PassengerRatingDto passengerRatingDto);
}
