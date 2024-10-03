package com.software.modsen.ratingmicroservice.clients;

import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRating;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingMessage;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "passenger-microservice", url = "${feign.client.passenger.url}")
public interface PassengerRatingClient {
    @PutMapping
    ResponseEntity<PassengerRating> updatePassengerRating(@Valid
                                                          @RequestBody
                                                          PassengerRatingMessage passengerRatingDto);
}