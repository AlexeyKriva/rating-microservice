package com.software.modsen.ratingmicroservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableFeignClients(basePackages = "com.software.modsen.ratingmicroservice.clients")
@EnableDiscoveryClient
@EnableRetry
@EnableTransactionManagement
@OpenAPIDefinition(
        info = @Info(
                title = "Rating API",
                description = "Rating microservice for Modsen internship",
                contact = @Contact(
                        name = "Alexey Kryvetski",
                        email = "alexey.kriva03.com@gmail.com"
                )
        )
)
public class RatingMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingMicroserviceApplication.class, args);
    }

}