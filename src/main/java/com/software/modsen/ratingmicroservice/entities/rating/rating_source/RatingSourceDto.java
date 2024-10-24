package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Rating source entity.")
public record RatingSourceDto(
        @NotNull(message = "Rating id cannot be null.")
        @JsonProperty("ratingId")
        Long ratingId,

        @NotNull(message = "Source cannot be null.")
        @JsonProperty("source")
        SimpleRatingSource source
) {

}