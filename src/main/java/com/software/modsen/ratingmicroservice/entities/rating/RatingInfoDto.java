package com.software.modsen.ratingmicroservice.entities.rating;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class RatingInfoDto {
    private Source ratingSource;
    private Rating rating;
}