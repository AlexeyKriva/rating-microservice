package com.software.modsen.ratingmicroservice.entities.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
public class RatingPatchDto {
    @JsonProperty("ride_id")
    private Long rideId;
    @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
    @JsonProperty("rating_value")
    private Integer ratingValue;
    @JsonProperty("comment")
    private String comment;
}
