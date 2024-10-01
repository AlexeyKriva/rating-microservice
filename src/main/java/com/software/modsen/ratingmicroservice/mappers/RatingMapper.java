package com.software.modsen.ratingmicroservice.mappers;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    Rating fromRatingDtoToRating(RatingDto ratingDto);

    Rating fromRatingPatchDtoToRating(RatingPatchDto ratingPatchDto);
}