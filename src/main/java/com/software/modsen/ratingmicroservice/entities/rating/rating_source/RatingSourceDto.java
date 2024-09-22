package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RatingSourceDto {
    @NotNull(message = "Rating id cannot be null.")
    @JsonProperty("rating_id")
    private Long ratingId;
    @NotNull(message = "Source cannot be null.")
    @JsonProperty("source")
    private Source source;
}
