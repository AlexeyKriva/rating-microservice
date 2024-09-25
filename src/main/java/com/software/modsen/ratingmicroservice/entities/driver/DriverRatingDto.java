package com.software.modsen.ratingmicroservice.entities.driver;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
@ToString
public class DriverRatingDto {
    @NotNull(message = "Driver id cannot be null.")
    @JsonProperty("driver_id")
    private Long driverId;

    @NotNull(message = "Rating value cannot be null.")
    @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
    @JsonProperty("rating_value")
    private Integer ratingValue;
}