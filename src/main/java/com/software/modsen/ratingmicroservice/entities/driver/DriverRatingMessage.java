package com.software.modsen.ratingmicroservice.entities.driver;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Driver rating entity.")
public class DriverRatingMessage {
        @NotNull(message = "Driver id cannot be null.")
        @JsonProperty("driverId")
        private long driverId;

        @NotNull(message = "Rating value cannot be null.")
        @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
        @JsonProperty("ratingValue")
        private int ratingValue;
}