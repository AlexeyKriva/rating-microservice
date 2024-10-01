package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Rating source entity.")
public class RatingSourcePatchDto {
    @JsonProperty("rating_id")
    private Long ratingId;

    @JsonProperty("source")
    private Source source;
}