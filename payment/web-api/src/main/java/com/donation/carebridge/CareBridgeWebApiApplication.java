package com.donation.carebridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CareBridgeWebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareBridgeWebApiApplication.class, args);
    }
}