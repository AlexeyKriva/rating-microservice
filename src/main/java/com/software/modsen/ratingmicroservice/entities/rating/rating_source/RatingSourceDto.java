package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Rating source entity.")
public class RatingSourceDto {
    @NotNull(message = "Rating id cannot be null.")
    @JsonProperty("rating_id")
    private Long ratingId;

    @NotNull(message = "Source cannot be null.")
    @JsonProperty("source")
    private Source source;
}