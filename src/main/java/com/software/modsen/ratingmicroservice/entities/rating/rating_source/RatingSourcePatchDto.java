package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rating source entity.")
public record RatingSourcePatchDto(
        @JsonProperty("ratingId")
        Long ratingId,

        @JsonProperty("source")
        SimpleRatingSource source
) {
}
