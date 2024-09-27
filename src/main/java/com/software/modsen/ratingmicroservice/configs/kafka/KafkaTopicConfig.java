package com.software.modsen.ratingmicroservice.configs.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic passengerRatingTopic() {
        return TopicBuilder.name("passenger-rating").build();
    }

    @Bean
    public NewTopic driverRatingTopic() {
        return TopicBuilder.name("driver-rating").build();
    }
}
