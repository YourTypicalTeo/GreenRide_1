package com.greenride.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi; // <--- New Import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GreenRide API Service")
                        .version("1.0")
                        .description("API documentation for the Distributed Systems Semester Project. " +
                                "Handles Rides, Bookings, and Authentication.")
                        .contact(new Contact()
                                .name("GreenRide Team")
                                .email("support@greenride.com")))
                // Add Security (JWT) to Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * Groups all REST API endpoints into a specific "API" category in Swagger.
     * This hides the internal Web UI controllers from the API documentation.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("greenride-api")
                .packagesToScan("com.greenride.controller.api") // Scans only your API controllers
                .pathsToMatch("/api/**") // Matches only API paths
                .build();
    }
}