package com.software.modsen.ratingmicroservice.configs.kafka;

import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingDto;
import com.software.modsen.ratingmicroservice.entities.passenger.Passenger;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, PassengerRatingDto> passengerRatingProducerFactory() {
        Map<String, Object> kafkaProducerProps = new HashMap<>();
        kafkaProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        kafkaProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(kafkaProducerProps);
    }

    @Bean
    public ProducerFactory<String, DriverRatingDto> driverRatingProducerFactory() {
        Map<String, Object> kafkaProducerProps = new HashMap<>();
        kafkaProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        kafkaProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(kafkaProducerProps);
    }

    @Bean
    public KafkaTemplate<String, PassengerRatingDto> passengerRatingDtoKafkaTemplate() {
        return new KafkaTemplate<>(passengerRatingProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, DriverRatingDto> driverRatingDtoKafkaTemplate() {
        return new KafkaTemplate<>(driverRatingProducerFactory());
    }
}
