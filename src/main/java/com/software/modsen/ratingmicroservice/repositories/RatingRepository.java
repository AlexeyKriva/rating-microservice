package com.software.modsen.ratingmicroservice.repositories;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> getRatingById(long id);
}
