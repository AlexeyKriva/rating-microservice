package com.software.modsen.ratingmicroservice.entities.ride;

import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ride entity.")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    private String passengerId;

    private Long driverId;

    @Column(name = "from_address", nullable = false)
    @Schema(example = "Nezavisimosty 3")
    private String fromAddress;

    @Column(name = "to_address", nullable = false)
    @Schema(example = "Nezavisimosty 177")
    private String toAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "ride_status", nullable = false)
    private RideStatus rideStatus;

    @Column(name = "order_date_time", nullable = false)
    @Schema(example = "2024-03-29T12:00:00")
    private LocalDateTime orderDateTime;

    @Column(name = "price", nullable = false)
    private Float price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;
}