package com.software.modsen.ratingmicroservice.controllers;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import com.software.modsen.ratingmicroservice.mappers.RatingSourceMapper;
import com.software.modsen.ratingmicroservice.services.RatingSourceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rating-source", produces = "application/json")
@AllArgsConstructor
public class RatingSourceController {
    private RatingSourceService ratingSourceService;
    private final RatingSourceMapper RATING_SOURCE_MAPPER = RatingSourceMapper.INSTANCE;

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
        return ResponseEntity.ok(ratingSourceService.updateRatingSource(
                id,
                ratingSourceDto.getRatingId(),
                RATING_SOURCE_MAPPER.fromRatingSourceDtoToRatingSource(ratingSourceDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RatingSource> patchRatingSourceById(@PathVariable("id") long id,
                                                              @Valid
                                                              @RequestBody RatingSourcePatchDto ratingSourcePatchDto) {
        return ResponseEntity.ok(ratingSourceService.patchRatingSource(
                id,
                ratingSourcePatchDto.getRatingId(),
                RATING_SOURCE_MAPPER.fromRatingSourcePatchDtoToRatingSource(ratingSourcePatchDto)));
    }
}