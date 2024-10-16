package com.software.modsen.ratingmicroservice.entities.rating.rating_source;

import com.software.modsen.ratingmicroservice.entities.rating.Rating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rating_source")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Rating source entity.")
public class RatingSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source;
}