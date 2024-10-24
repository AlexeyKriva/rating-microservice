package com.software.modsen.ratingmicroservice.controllers;

import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import com.software.modsen.ratingmicroservice.mappers.RatingSourceMapper;
import com.software.modsen.ratingmicroservice.services.RatingSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/ratings/sources", produces = "application/json")
@AllArgsConstructor
@Tag(name = "Rating source controller", description = "Allows to interact with rating source.")
public class RatingSourceController {
    private RatingSourceService ratingSourceService;
    private final RatingSourceMapper RATING_SOURCE_MAPPER = RatingSourceMapper.INSTANCE;

    @GetMapping
    @Operation(
            description = "Allows to get all rating sources."
    )
    public ResponseEntity<List<RatingSource>> getAllRatingSources() {
        return ResponseEntity.ok(ratingSourceService.getAllRatingSources());
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Allows to get rating sources by id."
    )
    public ResponseEntity<RatingSource> getRatingSourceById(
            @PathVariable("id")
            @Parameter(description = "Rating source id.")
            long id) {
        return ResponseEntity.ok(ratingSourceService.getRatingSourceById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Allows to update rating sources by id."
    )
    public ResponseEntity<RatingSource> updateRatingSourceById(
            @PathVariable("id")
            @Parameter(description = "Rating source id.")
            long id,
            @Valid
            @RequestBody
            @Parameter(description = "Rating source entity.")
            RatingSourceDto ratingSourceDto) {
        return ResponseEntity.ok(ratingSourceService.updateRatingSource(
                id,
                ratingSourceDto.ratingId(),
                RATING_SOURCE_MAPPER.fromRatingSourceDtoToRatingSource(ratingSourceDto)));
    }

    @PatchMapping("/{id}")
    @Operation(
            description = "Allows to update rating sources by id."
    )
    public ResponseEntity<RatingSource> patchRatingSourceById(
            @PathVariable("id")
            @Parameter(description = "Rating source id.")
            long id,
            @Valid
            @RequestBody
            @Parameter(description = "Rating source entity.")
            RatingSourcePatchDto ratingSourcePatchDto) {
        return ResponseEntity.ok(ratingSourceService.patchRatingSource(
                id,
                ratingSourcePatchDto.ratingId(),
                RATING_SOURCE_MAPPER.fromRatingSourcePatchDtoToRatingSource(ratingSourcePatchDto)));
    }
}