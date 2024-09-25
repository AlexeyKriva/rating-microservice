package com.software.modsen.ratingmicroservice.repositories;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingSourceRepository extends JpaRepository<RatingSource, Long> {
    Optional<RatingSource> findRatingSourceById(long id);

    Optional<RatingSource> findRatingSourceByRatingIdAndSource(long ratingId, Source ratingSource);
}