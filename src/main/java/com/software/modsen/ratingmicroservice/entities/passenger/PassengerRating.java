package com.software.modsen.ratingmicroservice.entities.passenger;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passenger_rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @Column(name = "rating_value", nullable = false)
    private Float ratingValue;

    @Column(name = "number_of_ratings", nullable = false)
    private Integer numberOfRatings;
}