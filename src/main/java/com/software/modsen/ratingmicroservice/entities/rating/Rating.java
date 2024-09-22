package com.software.modsen.ratingmicroservice.entities.rating;

import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.rating.rating_source.RatingSource;
import com.software.modsen.ratingmicroservice.entities.ride.Ride;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ride_id")
    private Ride ride;
    @Column(name = "rating_value", nullable = false)
    private Integer ratingValue;
    @Column(name = "comment")
    private String comment;
}