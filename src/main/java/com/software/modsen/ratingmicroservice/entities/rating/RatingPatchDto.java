package com.software.modsen.ratingmicroservice.entities.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;

@Schema(description = "Rating entity.")
public record RatingPatchDto(
        @JsonProperty("rideId")
        Long rideId,

        @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
        @JsonProperty("ratingValue")
        Integer ratingValue,

        @JsonProperty("comment")
        String comment
) {
}
