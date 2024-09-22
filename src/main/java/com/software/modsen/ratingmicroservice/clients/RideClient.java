package com.software.modsen.ratingmicroservice.clients;

import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ride-microservice", url = "http://localhost:8083/api/ride")
public interface RideClient {
    @GetMapping("/{id}")
    ResponseEntity<Ride> getRideById(@PathVariable("id") long id);
}