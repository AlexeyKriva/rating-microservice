package com.software.modsen.ratingmicroservice.entities.rating;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.SimpleRatingSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Schema(description = "Rating info entity.")
public class RatingInfo {
    private SimpleRatingSource ratingSource;
    private Rating rating;
}