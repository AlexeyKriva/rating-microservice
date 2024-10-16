package com.software.modsen.ratingmicroservice.controllers;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.mappers.RatingMapper;
import com.software.modsen.ratingmicroservice.services.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rating", produces = "application/json")
@AllArgsConstructor
@Tag(name = "Rating controller", description = "Allows to interact with passenger and driver ratings.")
public class RatingController {
    private RatingService ratingService;
    private final RatingMapper RATING_MAPPER = RatingMapper.INSTANCE;

    @GetMapping
    @Operation(
            description = "Allows to get all ratings."
    )
    public ResponseEntity<List<Rating>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Allows to get ratings by id."
    )
    public ResponseEntity<Rating> getRatingById(
            @PathVariable("id")
            @Parameter(description = "Rating id.")
            long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @GetMapping("/passenger/{id}")
    @Operation(
            description = "Allows to get all ratings by passenger id and rating source."
    )
    public ResponseEntity<List<Rating>> getAllRatingsByPassengerIdAndBySource(
            @PathVariable("id")
            @Parameter(description = "Passenger id.")
            long id,
            @RequestParam("ratingSource")
            @Parameter(description = "Rating source.")
            Source ratingSource) {
        return ResponseEntity.ok(ratingService.getAllRatingsByPassengerIdAndBySource(id, ratingSource));
    }

    @GetMapping("/driver/{id}")
    @Operation(
            description = "Allows to get all ratings by driver id and rating source."
    )
    public ResponseEntity<List<Rating>> getAllRatingsByDriverIdAndBySource(
            @PathVariable("id")
            @Parameter(description = "Driver id.")
            long id,
            @RequestParam("ratingSource")
            @Parameter(description = "Rating source.")
            Source ratingSource) {
        return ResponseEntity.ok(ratingService.getAllRatingsByDriverIdAndBySource(id, ratingSource));
    }

    @PostMapping
    @Operation(
            description = "Allows to save new ratings by rating source."
    )
    public ResponseEntity<Rating> saveRating(
            @RequestParam("ratingSource")
            @Parameter(description = "Rating source.")
            Source ratingSource,

            @Valid
            @RequestBody
            @Parameter(description = "Rating entity.")
            RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.saveRating(
                ratingSource,
                ratingDto.getRideId(),
                RATING_MAPPER.fromRatingDtoToRating(ratingDto)));
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Allows to update rating by id."
    )
    public ResponseEntity<Rating> updateRatingById(
            @PathVariable("id")
            @Parameter(description = "Rating id")
            long id,
            @Valid
            @RequestBody
            @Parameter(description = "Rating entity.")
            RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.updateRating(
                id,
                ratingDto.getRideId(),
                RATING_MAPPER.fromRatingDtoToRating(ratingDto)));
    }

    @PatchMapping("/{id}")
    @Operation(
            description = "Allows to update rating by id."
    )
    public ResponseEntity<Rating> patchRatingById(
            @PathVariable("id")
            @Parameter(description = "Rating id.")
            long id,
            @Valid
            @RequestBody
            @Parameter(description = "Rating entity.")
            RatingPatchDto ratingPatchDto) {
        return ResponseEntity.ok(ratingService.patchRating(
                id,
                ratingPatchDto.getRideId(),
                RATING_MAPPER.fromRatingPatchDtoToRating(ratingPatchDto)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Allows to delete rating by id."
    )
    public ResponseEntity<String> deleteRatingById(
            @PathVariable("id")
            @Parameter(description = "Rating id.")
            long id) {
        ratingService.deleteRatingById(id);
        return ResponseEntity.ok("Rating was successfully deleted by id " + id);
    }
}