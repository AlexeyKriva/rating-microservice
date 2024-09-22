package com.software.modsen.ratingmicroservice.entities.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
public class RatingPatchDto {
    @JsonProperty("passenger_id")
    private Long passengerId;
    @JsonProperty("driver_id")
    private Long driverId;
    @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
    @JsonProperty("rating_value")
    private int ratingValue;
    @JsonProperty("comment")
    private String comment;
}