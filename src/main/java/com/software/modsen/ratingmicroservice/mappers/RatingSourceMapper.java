package com.software.modsen.ratingmicroservice.mappers;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RatingSourceMapper {
    RatingSourceMapper INSTANCE = Mappers.getMapper(RatingSourceMapper.class);

    RatingSource fromRatingSourceDtoToRatingSource(RatingSourceDto ratingSourceDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRatingSourceFromRatingSourcePatchDto(RatingSourcePatchDto ratingSourcePatchDto,
                                                    @MappingTarget RatingSource ratingSource);
}
