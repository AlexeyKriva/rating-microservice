package com.software.modsen.ratingmicroservice.controllers;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.RatingDto;
import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import com.software.modsen.ratingmicroservice.services.RatingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rating", produces = "application/json")
@AllArgsConstructor
public class RatingController {
    private RatingService ratingService;

    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable("id") long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @GetMapping("/passenger/{id}")
    public ResponseEntity<List<Rating>> getAllRatingsByPassengerIdAndBySource(@PathVariable("id") long id,
                                                                              @RequestParam("ratingSource")
                                                                              Source ratingSource) {
        return ResponseEntity.ok(ratingService.getAllRatingsByPassengerIdAndBySource(id, ratingSource));
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<List<Rating>> getAllRatingsByDriverIdAndBySource(@PathVariable("id") long id,
                                                                              @RequestParam("ratingSource")
                                                                              Source ratingSource) {
        return ResponseEntity.ok(ratingService.getAllRatingsByDriverIdAndBySource(id, ratingSource));
    }

    @PostMapping
    public ResponseEntity<Rating> saveRating(@RequestParam("ratingSource") Source ratingSource,
                                             @Valid @RequestBody RatingDto ratingDto) {
        System.out.println(ratingDto);
        return ResponseEntity.ok(ratingService.saveRating(ratingSource, ratingDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRatingById(@PathVariable("id") long id,
                                                   @Valid @RequestBody RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.updateRating(id, ratingDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Rating> patchRatingById(@PathVariable("id") long id,
                                                  @Valid @RequestBody RatingPatchDto ratingPatchDto) {
        return ResponseEntity.ok(ratingService.patchRating(id, ratingPatchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRatingById(@PathVariable("id") long id) {
        ratingService.deleteRatingById(id);
        return ResponseEntity.ok("Rating was successfully deleted by id " + id);
    }
}