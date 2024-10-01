package com.software.modsen.ratingmicroservice.configs.kafka;

import com.software.modsen.ratingmicroservice.entities.driver.DriverRatingMessage;
import com.software.modsen.ratingmicroservice.entities.passenger.PassengerRatingMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerialize;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private String enableIdempotence;

    public Map<String, Object> producerFactory() {
        Map<String, Object> kafkaProducerProps = new HashMap<>();
        kafkaProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        kafkaProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerialize);
        kafkaProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        kafkaProducerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

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