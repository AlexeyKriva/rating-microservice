package com.software.modsen.ratingmicroservice.services;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourceDto;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSourcePatchDto;
import com.software.modsen.ratingmicroservice.mappers.RatingSourceMapper;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
public class RatingSourceService {
    @Autowired
    private RatingSourceRepository ratingSourceRepository;
    @Autowired
    private RatingRepository ratingRepository;
    private final RatingSourceMapper RATING_SOURCE_MAPPER = RatingSourceMapper.INSTANCE;

    public List<RatingSource> getAllRatingSources() {
        return ratingSourceRepository.findAll();
    }

    public RatingSource getRatingSourceById(long id) {
        Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.getRatingSourceById(id);
        return ratingSourceFromDb.orElseThrow(
                () -> new RuntimeException()
        );
    }

    public RatingSource updateRatingSource(long id, RatingSourceDto ratingSourceDto) {
        Optional<Rating> ratingFromDb = ratingRepository.getRatingById(ratingSourceDto.getRatingId());
        if (ratingFromDb.isPresent()) {
            Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.getRatingSourceById(id);
            if (ratingSourceFromDb.isPresent()) {
                RatingSource updatingRatingSource = RATING_SOURCE_MAPPER.fromRatingSourceDtoToRatingSource(ratingSourceDto);
                updatingRatingSource.setId(id);
                updatingRatingSource.setRating(ratingFromDb.get());

                return ratingSourceRepository.save(updatingRatingSource);
            }

            throw new RuntimeException();
        }

        throw new RuntimeException();
    }

    public RatingSource patchRatingSource(long id, RatingSourcePatchDto ratingSourcePatchDto) {
        Optional<Rating> ratingFromDb = ratingRepository.getRatingById(ratingSourcePatchDto.getRatingId());
        if (ratingFromDb.isPresent()) {
            Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.getRatingSourceById(id);
            if (ratingSourceFromDb.isPresent()) {
                RatingSource updatingRatingSource = ratingSourceFromDb.get();
                RATING_SOURCE_MAPPER.updateRatingSourceFromRatingSourcePatchDto(ratingSourcePatchDto,
                        updatingRatingSource);
                if (ratingSourcePatchDto.getRatingId() != null) {
                    updatingRatingSource.setRating(ratingFromDb.get());
                }

                return ratingSourceRepository.save(updatingRatingSource);
            }

            throw new RuntimeException();
        }

        throw new RuntimeException();
    }

    public void deleteRatingSourceById(long id) {
        Optional<RatingSource> ratingSourceFromDb = ratingSourceRepository.getRatingSourceById(id);
        ratingSourceFromDb.ifPresentOrElse(
                ratingSource -> ratingSourceRepository.deleteById(id),
                () -> new RuntimeException()
        );
    }
}