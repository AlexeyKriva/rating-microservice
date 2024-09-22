package com.software.modsen.ratingmicroservice.repositories;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingSourceRepository extends JpaRepository<RatingSource, Long> {
}
