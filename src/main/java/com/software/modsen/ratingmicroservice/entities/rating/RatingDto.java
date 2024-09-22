package com.software.modsen.ratingmicroservice.entities.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
public class RatingDto {
    @NotNull(message = "Passenger id cannot be null.")
    @JsonProperty("passenger_id")
    private Long passengerId;
    @NotNull(message = "Driver id cannot be null.")
    @JsonProperty("driver_id")
    private Long driverId;
    @NotNull(message = "Rating value cannot be null.")
    @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
    @JsonProperty("rating_value")
    private int ratingValue;
    @JsonProperty("comment")
    private String comment;
}