package com.software.modsen.ratingmicroservice.integration;

import com.software.modsen.ratingmicroservice.RatingMicroserviceApplication;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.Source;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RatingMicroserviceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RatingSourceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15"))
            .withDatabaseName("cab-aggregator-db")
            .withUsername("postgres")
            .withPassword("98479847");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    static boolean isAlreadySetUped = false;

    @BeforeEach
    void setUp() {
        if (!isAlreadySetUped) {
            List<Rating> ratings = defaultRatings();
            long rideId = 1;
            for (Rating rating : ratings) {
                jdbcTemplate.update("INSERT INTO rating " +
                                "(ride_id, rating_value, comment)"
                                + " VALUES(?, ?, ?)",
                        rideId++, rating.getRatingValue(), rating.getComment());

                int ratingId = jdbcTemplate.queryForObject("SELECT id FROM rating WHERE ride_id=? AND rating_value=?",
                        new Object[]{rideId - 1, rating.getRatingValue()}, Integer.class);

                Source ratingSource;

                if ((ratingId & 1) == 1) {
                    ratingSource = Source.PASSENGER;
                } else {
                    ratingSource = Source.DRIVER;
                }

                jdbcTemplate.update("INSERT INTO rating_source (rating_id, source) "
                        + "VALUES(?, ?)", ratingId, ratingSource.name());
            }

            isAlreadySetUped = true;
        }
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
    @Order(1)
    @SneakyThrows
    void getAllRatingSourcesTest_ReturnsRatingSources() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/rating-source"))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll(
                () -> assertTrue(responseContent.contains("1")),
                () -> assertTrue(responseContent.contains("2")),
                () -> assertTrue(responseContent.contains("3")),
                () -> assertTrue(responseContent.contains("PASSENGER")),
                () -> assertTrue(responseContent.contains("DRIVER"))
        );
    }

    @Test
    @Order(2)
    @SneakyThrows
    void getRatingSourceByIdTest_ReturnsRatingSources() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/rating-source/1"))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll(
                () -> assertTrue(responseContent.contains("1")),
                () -> assertTrue(responseContent.contains("PASSENGER")),
                () -> assertFalse(responseContent.contains("DRIVER"))
        );
    }

    String ratingSourceDto = "{"
            + "\"rating_id\": 1,"
            + "\"source\": \"DRIVER\""
            + "}";

    @Test
    @Order(3)
    @SneakyThrows
    void updateRatingSourceByIdTest_ReturnsRatingSources() {
        //given
        MvcResult mvcResult = mockMvc.perform(put("/api/rating-source/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingSourceDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll(
                () -> assertTrue(responseContent.contains("1")),
                () -> assertTrue(responseContent.contains("DRIVER")),
                () -> assertFalse(responseContent.contains("PASSENGER"))
        );
    }

    String ratingSourcePatchDto = "{"
            + "\"rating_id\": 1,"
            + "\"source\": \"PASSENGER\""
            + "}";

    @Test
    @Order(4)
    @SneakyThrows
    void patchRatingSourceByIdTest_ReturnsRatingSources() {
        //given
        MvcResult mvcResult = mockMvc.perform(patch("/api/rating-source/2")
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
