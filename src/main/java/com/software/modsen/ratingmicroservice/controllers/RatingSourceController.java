package com.software.modsen.ratingmicroservice.controllers;

import com.software.modsen.ratingmicroservice.entities.rating.RatingPatchDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import com.software.modsen.ratingmicroservice.services.RatingService;
import com.software.modsen.ratingmicroservice.services.RatingSourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rating-source", produces = "application/json")
public class RatingSourceController {
    @Autowired
    private RatingSourceService ratingSourceService;

    @GetMapping
    public ResponseEntity<List<RatingSource>> getAllRatingSources() {
        return ResponseEntity.ok(ratingSourceService.getAllRatingSources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingSource> getRatingSourceById(@PathVariable("id") long id) {
        return ResponseEntity.ok(ratingSourceService.getRatingSourceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingSource> updateRatingSourceById(@PathVariable("id") long id,
                                                               @Valid
                                                               @RequestBody RatingSourceDto ratingSourceDto) {
        return ResponseEntity.ok(ratingSourceService.updateRatingSource(id, ratingSourceDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RatingSource> patchRatingSourceById(@PathVariable("id") long id,
                                                              @Valid
                                                              @RequestBody RatingSourcePatchDto ratingSourcePatchDto) {
        return ResponseEntity.ok(ratingSourceService.patchRatingSource(id, ratingSourcePatchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRatingSourceById(@PathVariable("id") long id) {
        ratingSourceService.deleteRatingSourceById(id);
        return ResponseEntity.ok("Rating source was successfully deleted by id " + id);
    }
}