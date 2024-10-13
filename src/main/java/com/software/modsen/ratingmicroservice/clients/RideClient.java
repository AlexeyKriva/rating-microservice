package com.software.modsen.ratingmicroservice.clients;

import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ride-microservice")
public interface RideClient {
    @GetMapping("/{id}")
    ResponseEntity<Ride> getRideById(@PathVariable("id") long id);

    @GetMapping("/passenger/{id}")
    ResponseEntity<List<Ride>> getAllRidesByPassengerId(@PathVariable("id") long passengerId);

    @GetMapping("/driver/{id}")
    ResponseEntity<List<Ride>> getAllRidesByDriverId(@PathVariable("id") long driverId);
}