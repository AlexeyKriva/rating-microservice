package com.software.modsen.ratingmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableFeignClients(basePackages = "com.software.modsen.ratingmicroservice.clients")
@EnableDiscoveryClient
public class RatingMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingMicroserviceApplication.class, args);
    }

}
