package com.software.modsen.ratingmicroservice.entities.rating;

import com.software.modsen.ratingmicroservice.entities.driver.Driver;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passnger_id")
    private Passenger passenger;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    @Column(name = "rating_value", nullable = false)
    private int ratingValue;
    @Column(name = "comment")
    private String comment;
}