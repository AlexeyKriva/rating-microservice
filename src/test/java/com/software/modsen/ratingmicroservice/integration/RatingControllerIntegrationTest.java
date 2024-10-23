package com.software.modsen.ratingmicroservice.integration;

import com.software.modsen.ratingmicroservice.RatingMicroserviceApplication;
import com.software.modsen.ratingmicroservice.clients.RideClient;
import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.driver.Sex;
import com.software.modsen.ratingmicroservice.entities.driver.car.Car;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarBrand;
import com.software.modsen.ratingmicroservice.entities.driver.car.CarColor;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.SimpleRatingSource;
import com.software.modsen.ratingmicroservice.entities.ride.Currency;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import com.software.modsen.ratingmicroservice.entities.ride.RideStatus;
import com.software.modsen.ratingmicroservice.repositories.RatingRepository;
import com.software.modsen.ratingmicroservice.repositories.RatingSourceRepository;
import com.software.modsen.ratingmicroservice.services.RatingService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RatingMicroserviceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RatingControllerIntegrationTest extends TestconteinersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private RideClient rideClient;

    @Autowired
    private RatingService ratingService;

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
    void getAllRatingsTest_ReturnsRatings() {
        //given
        List<Rating> ratings = defaultRatings();
        long rideId = 1;
        for (Rating rating : ratings) {
            jdbcTemplate.update("INSERT INTO rating " +
                            "(ride_id, rating_value, comment)"
                            + " VALUES(?, ?, ?)",
                    rideId++, rating.getRatingValue(), rating.getComment());
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/rating"))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Ivan")),
                () -> assertTrue(responseContent.contains("Leonid")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("Vova")),
                () -> assertTrue(responseContent.contains("Andrei")),
                () -> assertTrue(responseContent.contains("Sergei")),
                () -> assertTrue(responseContent.contains("5")),
                () -> assertTrue(responseContent.contains("3")),
                () -> assertTrue(responseContent.contains("4")),
                () -> assertTrue(responseContent.contains("comment")),
                () -> assertTrue(responseContent.contains("bad")),
                () -> assertTrue(responseContent.contains("no bad"))
        );
    }

    @Test
    @SneakyThrows
    void getRatingByIdTest_ReturnsRating() {
        //given
        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId, rating.getRatingValue(), rating.getComment());

        rating = ratingService.getAllRatings().get(0);


        MvcResult mvcResult = mockMvc.perform(get("/api/rating/" + rating.getId()))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Ivan")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("5")),
                () -> assertTrue(responseContent.contains("comment"))
        );
    }

    @Test
    @SneakyThrows
    void getAllRatingsByPassengerIdAndBySourceTest_ReturnsRatings() {
        //given
        List<Ride> mockRides = List.of(Ride.builder()
                .id(3L)
                .passenger(Passenger.builder()
                        .id(3L)
                        .name("Andrei")
                        .email("andrei@gmail.com")
                        .phoneNumber("+37529178933")
                        .isDeleted(false)
                        .build())
                .driver(Driver.builder()
                        .id(3L)
                        .name("Sergei")
                        .email("sergei@gmail.com")
                        .phoneNumber("+375441333337")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(3L)
                                .color(CarColor.BLUE)
                                .brand(CarBrand.FERRARI)
                                .carNumber("3433CD-7")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build())
                .fromAddress("Nezavisimosty 5")
                .toAddress("Nezavisimosty 6")
                .rideStatus(RideStatus.ACCEPTED)
                .orderDateTime(LocalDateTime.of(2021, 10, 11, 12, 0, 0))
                .price(100F)
                .currency(Currency.BYN)
                .build());
        when(rideClient.getAllRidesByPassengerId(3L))
                .thenReturn(new ResponseEntity<>(mockRides, HttpStatus.OK));

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

        MvcResult mvcResult = mockMvc.perform(get("/api/rating/passenger/3?ratingSource=DRIVER"))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Andrei")),
                () -> assertTrue(responseContent.contains("Sergei")),
                () -> assertTrue(responseContent.contains("4")),
                () -> assertTrue(responseContent.contains("no bad"))
        );
    }

    @Test
    @SneakyThrows
    void getAllRatingsByDriverIdAndBySourceTest_ReturnsRatings() {
        //given
        List<Ride> mockRides = List.of(Ride.builder()
                .id(2L)
                .passenger(Passenger.builder()
                        .id(2L)
                        .name("Leonid")
                        .email("leonid@gmail.com")
                        .phoneNumber("375291145878")
                        .isDeleted(false)
                        .build())
                .driver(Driver.builder()
                        .id(2L)
                        .name("Vova")
                        .email("vova@gmail.com")
                        .phoneNumber("+375441553333")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(2L)
                                .color(CarColor.BLACK)
                                .brand(CarBrand.BMW)
                                .carNumber("3498CD-7")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build())
                .fromAddress("Nezavisimosty 3")
                .toAddress("Nezavisimosty 4")
                .rideStatus(RideStatus.COMPLETED)
                .orderDateTime(LocalDateTime.of(2022, 10, 11, 12, 0, 0))
                .price(100F)
                .currency(Currency.BYN)
                .build());
        when(rideClient.getAllRidesByDriverId(2L))
                .thenReturn(new ResponseEntity<>(mockRides, HttpStatus.OK));

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
                ratingSource = SimpleRatingSource.DRIVER;
            } else {
                ratingSource = SimpleRatingSource.PASSENGER;
            }

            jdbcTemplate.update("INSERT INTO rating_source (rating_id, source) "
                    + "VALUES(?, ?)", ratingId, ratingSource.name());
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/rating/driver/2?ratingSource=PASSENGER"))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Leonid")),
                () -> assertTrue(responseContent.contains("Vova")),
                () -> assertTrue(responseContent.contains("3")),
                () -> assertTrue(responseContent.contains("bad"))
        );
    }

    private String ratingDto =
            "{"
                    + "\"rideId\": 1,"
                    + "\"ratingValue\": 2,"
                    + "\"comment\": \"not clean passenger\""
                    + "}";

    @Test
    @SneakyThrows
    void saveRatingTest_ReturnsRating() {
        //given
        Ride mockRide = Ride.builder()
                .id(1L)
                .passenger(Passenger.builder()
                        .id(1L)
                        .name("Ivan")
                        .email("ivan@gmail.com")
                        .phoneNumber("+375293578799")
                        .isDeleted(false)
                        .build())
                .driver(Driver.builder()
                        .id(1L)
                        .name("Kirill")
                        .email("kirill@gmail.com")
                        .phoneNumber("+375298877123")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(3L)
                                .color(CarColor.WHITE)
                                .brand(CarBrand.FORD)
                                .carNumber("1257AB-1")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build())
                .fromAddress("Nezavisimosty 1")
                .toAddress("Nezavisimosty 2")
                .rideStatus(RideStatus.CREATED)
                .orderDateTime(LocalDateTime.of(2023, 10, 11, 12, 0, 0))
                .price(100F)
                .currency(Currency.BYN)
                .build();
        when(rideClient.getRideById(1L))
                .thenReturn(new ResponseEntity<>(mockRide, HttpStatus.OK));

        MvcResult mvcResult = mockMvc.perform(post("/api/rating?ratingSource=DRIVER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Ivan")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("Nezavisimosty 1")),
                () -> assertTrue(responseContent.contains("Nezavisimosty 2")),
                () -> assertTrue(responseContent.contains("2023,10,11,12,0")),
                () -> assertTrue(responseContent.contains("2")),
                () -> assertTrue(responseContent.contains("not clean passenger"))
        );
    }

    private String ratingUpdateDto =
            "{"
                    + "\"rideId\": 1,"
                    + "\"ratingValue\": 1,"
                    + "\"comment\": \"very bad driver\""
                    + "}";

    @Test
    @SneakyThrows
    void updateRatingByIdTest_ReturnsRating() {
        //given
        Ride mockRide = Ride.builder()
                .id(1L)
                .passenger(Passenger.builder()
                        .id(1L)
                        .name("Ivan")
                        .email("ivan@gmail.com")
                        .phoneNumber("+375293578799")
                        .isDeleted(false)
                        .build())
                .driver(Driver.builder()
                        .id(1L)
                        .name("Kirill")
                        .email("kirill@gmail.com")
                        .phoneNumber("+375298877123")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(3L)
                                .color(CarColor.WHITE)
                                .brand(CarBrand.FORD)
                                .carNumber("1257AB-1")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build())
                .fromAddress("Nezavisimosty 1")
                .toAddress("Nezavisimosty 2")
                .rideStatus(RideStatus.CREATED)
                .orderDateTime(LocalDateTime.of(2023, 10, 11, 12, 0, 0))
                .price(100F)
                .currency(Currency.BYN)
                .build();
        when(rideClient.getRideById(1L))
                .thenReturn(new ResponseEntity<>(mockRide, HttpStatus.OK));

        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId, rating.getRatingValue(), rating.getComment());

        rating = ratingService.getAllRatings().get(0);

        MvcResult mvcResult = mockMvc.perform(put("/api/rating/" + rating.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingUpdateDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Ivan")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("Nezavisimosty 1")),
                () -> assertTrue(responseContent.contains("Nezavisimosty 2")),
                () -> assertTrue(responseContent.contains("2023,10,11,12,0")),
                () -> assertTrue(responseContent.contains("1")),
                () -> assertTrue(responseContent.contains("very bad driver"))
        );
    }

    private String ratingPatchDto =
            "{"
                    + "\"rideId\": 2,"
                    + "\"ratingValue\": 5,"
                    + "\"comment\": \"super passenger\""
                    + "}";

    @Test
    @SneakyThrows
    void patchRatingByIdTest_ReturnsRating() {
        //given
        Ride mockRide = Ride.builder()
                .id(2L)
                .passenger(Passenger.builder()
                        .id(2L)
                        .name("Leonid")
                        .email("leonid@gmail.com")
                        .phoneNumber("375291145878")
                        .isDeleted(false)
                        .build())
                .driver(Driver.builder()
                        .id(2L)
                        .name("Vova")
                        .email("vova@gmail.com")
                        .phoneNumber("+375441553333")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(2L)
                                .color(CarColor.BLACK)
                                .brand(CarBrand.BMW)
                                .carNumber("3498CD-7")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build())
                .fromAddress("Nezavisimosty 3")
                .toAddress("Nezavisimosty 4")
                .rideStatus(RideStatus.COMPLETED)
                .orderDateTime(LocalDateTime.of(2022, 10, 11, 12, 0, 0))
                .price(100F)
                .currency(Currency.BYN)
                .build();
        when(rideClient.getRideById(2L))
                .thenReturn(new ResponseEntity<>(mockRide, HttpStatus.OK));

        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId, rating.getRatingValue(), rating.getComment());

        rating = ratingService.getAllRatings().get(0);

        MvcResult mvcResult = mockMvc.perform(patch("/api/rating/" + rating.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ratingPatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Leonid")),
                () -> assertTrue(responseContent.contains("Vova")),
                () -> assertTrue(responseContent.contains("Nezavisimosty 3")),
                () -> assertTrue(responseContent.contains("Nezavisimosty 4")),
                () -> assertTrue(responseContent.contains("2022,10,11,12,0")),
                () -> assertTrue(responseContent.contains("5")),
                () -> assertTrue(responseContent.contains("super passenger"))
        );
    }

    @Test
    @SneakyThrows
    void deleteRatingByIdTest_ReturnsString() {
        //given
        Rating rating = defaultRatings().get(0);
        long rideId = 1;
        jdbcTemplate.update("INSERT INTO rating " +
                        "(ride_id, rating_value, comment)"
                        + " VALUES(?, ?, ?)",
                rideId, rating.getRatingValue(), rating.getComment());

        rating = ratingService.getAllRatings().get(0);

        MvcResult mvcResult = mockMvc.perform(delete("/api/rating/" + rating.getId()))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals("Rating was successfully deleted by id " + rating.getId(), responseContent);
    }
}