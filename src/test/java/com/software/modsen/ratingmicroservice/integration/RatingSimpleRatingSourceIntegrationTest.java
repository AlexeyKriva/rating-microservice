package com.software.modsen.ratingmicroservice.integration;

import com.software.modsen.ratingmicroservice.RatingMicroserviceApplication;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.SimpleRatingSource;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import com.software.modsen.ratingmicroservice.services.RatingService;
import com.software.modsen.ratingmicroservice.services.RatingSourceService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RatingMicroserviceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RatingSimpleRatingSourceIntegrationTest extends TestconteinersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingSourceService ratingSourceService;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingSourceRepository ratingSourceRepository;

    @AfterEach
    void setDown() {
        ratingSourceRepository.deleteAll();
        ratingRepository.deleteAll();
    }

    List<Rating> defaultRatings() {
        return List.of(
                Rating.builder()
                        .ratingValue(5)
                        .comment("comment")
                        .build(),
                Rating.builder()
                        .ratingValue(3)
                        .comment("bad")
                        .build(),
                Rating.builder()
                        .ratingValue(4)
                        .comment("no bad")
                        .build()
        );
    }

    @Test
    @SneakyThrows
    void getAllRatingSourcesTest_ReturnsRatingSources() {
        //given
        List<Rating> ratings = defaultRatings();
        long rideId = 1;
        for (Rating rating : ratings) {
            jdbcTemplate.update("INSERT INTO rating " +
                            "(ride_id, rating_value, comment)"
                            + " VALUES(?, ?, ?)",
                    rideId++, rating.getRatingValue(), rating.getComment());

            int ratingId = jdbcTemplate.queryForObject("SELECT id FROM rating WHERE ride_id=? AND rating_value=?",
                    new Object[]{rideId - 1, rating.getRatingValue()}, Integer.class);

            SimpleRatingSource ratingSource;

            if ((ratingId & 1) == 1) {
                ratingSource = SimpleRatingSource.PASSENGER;
            } else {
                ratingSource = SimpleRatingSource.DRIVER;
            }

            jdbcTemplate.update("INSERT INTO rating_source (rating_id, source) "
                    + "VALUES(?, ?)", ratingId, ratingSource.name());
        }

        ratings = ratingService.getAllRatings();


        MvcResult mvcResult = mockMvc.perform(get("/api/rating-source"))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        List<Rating> finalRatings = ratings;
        assertAll(
                () -> assertTrue(responseContent.contains(String.valueOf(finalRatings.get(0).getId()))),
                () -> assertTrue(responseContent.contains(String.valueOf(finalRatings.get(1).getId()))),
                () -> assertTrue(responseContent.contains(String.valueOf(finalRatings.get(2).getId()))),
                () -> assertTrue(responseContent.contains("PASSENGER")),
                () -> assertTrue(responseContent.contains("DRIVER"))
        );
    }

    @Test
    @SneakyThrows
    void getRatingSourceByIdTest_ReturnsRatingSources() {
        //given
        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId, rating.getRatingValue(), rating.getComment());

        rating = ratingService.getAllRatings().get(0);

        jdbcTemplate.update("INSERT INTO rating_source (rating_id, source) "
                + "VALUES(?, ?)", rating.getId(), SimpleRatingSource.PASSENGER.name());

        RatingSource ratingSource = ratingSourceService.getAllRatingSources().get(0);

        MvcResult mvcResult = mockMvc.perform(get("/api/rating-source/" + ratingSource.getId()))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll(
                () -> assertTrue(responseContent.contains(String.valueOf(ratingSource.getId()))),
                () -> assertTrue(responseContent.contains("PASSENGER")),
                () -> assertFalse(responseContent.contains("DRIVER"))
        );
    }

    String ratingSourceDto = "\"source\": \"DRIVER\""
            + "}";

    @Test
    @SneakyThrows
    void updateRatingSourceByIdTest_ReturnsRatingSources() {
        //given
        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId++, rating.getRatingValue(), rating.getComment());

        int ratingId = jdbcTemplate.queryForObject("SELECT id FROM rating WHERE ride_id=? AND rating_value=?",
                new Object[]{rideId - 1, rating.getRatingValue()}, Integer.class);

        SimpleRatingSource ratingSource;

        if ((ratingId & 1) == 1) {
            ratingSource = SimpleRatingSource.PASSENGER;
        } else {
            ratingSource = SimpleRatingSource.DRIVER;
        }

        jdbcTemplate.update("INSERT INTO rating_source (rating_id, source) "
                + "VALUES(?, ?)", ratingId, ratingSource.name());

        RatingSource ratingSourceFromDb = ratingSourceService.getAllRatingSources().get(0);

        ratingSourceDto = "{\n\"ratingId\": " + ratingSourceFromDb.getRating().getId() + ", " + ratingSourceDto;

        MvcResult mvcResult = mockMvc.perform(put("/api/rating-source/" + ratingSourceFromDb.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingSourceDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll(
                () -> assertTrue(responseContent.contains(String.valueOf(ratingSourceFromDb.getId()))),
                () -> assertTrue(responseContent.contains("DRIVER")),
                () -> assertFalse(responseContent.contains("PASSENGER"))
        );
    }

    String ratingSourcePatchDto = "{"
            + "\"ratingId\": 1,"
            + "\"source\": \"PASSENGER\""
            + "}";

    @Test
    @SneakyThrows
    void patchRatingSourceByIdTest_ReturnsRatingSources() {
        //given
        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId, rating.getRatingValue(), rating.getComment());

        rating = ratingService.getAllRatings().get(0);

        jdbcTemplate.update("INSERT INTO rating_source (rating_id, source) "
                + "VALUES(?, ?)", rating.getId(), SimpleRatingSource.DRIVER.name());

        RatingSource ratingSource = ratingSourceService.getAllRatingSources().get(0);

        MvcResult mvcResult = mockMvc.perform(patch("/api/rating-source/" + ratingSource.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingSourcePatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll(
                () -> assertTrue(responseContent.contains("2")),
                () -> assertTrue(responseContent.contains("PASSENGER")),
                () -> assertFalse(responseContent.contains("DRIVER"))
        );
    }
}
