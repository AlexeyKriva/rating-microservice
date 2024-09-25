package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RatingSourcePatchDto {
    @JsonProperty("rating_id")
    private Long ratingId;

    @JsonProperty("source")
    private Source source;
}