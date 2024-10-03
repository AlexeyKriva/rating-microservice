package com.software.modsen.ratingmicroservice.configs.kafka;

import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingMessage;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingMessage;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaProducerConfig {
    private Environment environment;

    public Map<String, Object> producerFactory() {
        Map<String, Object> kafkaProducerProps = new HashMap<>();
        kafkaProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        kafkaProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //kafkaProducerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

        return kafkaProducerProps;
    }

    @Bean
    public ProducerFactory<String, PassengerRatingMessage> passengerRatingProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, PassengerRatingMessage> passengerRatingKafkaTemplate() {
        return new KafkaTemplate<>(passengerRatingProducerFactory());
    }

    @Bean
    public ProducerFactory<String, DriverRatingMessage> driverRatingProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, DriverRatingMessage> driverRatingKafkaTemplate() {
        return new KafkaTemplate<>(driverRatingProducerFactory());
    }
}