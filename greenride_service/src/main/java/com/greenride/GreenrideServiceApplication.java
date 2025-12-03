package com.greenride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync      // Enables @Async for your SMS Service
@EnableScheduling // Enables @Scheduled for your Ride Reminders
public class GreenrideServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenrideServiceApplication.class, args);
    }
}