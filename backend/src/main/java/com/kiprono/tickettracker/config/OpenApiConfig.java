package com.kiprono.tickettracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Top-level OpenAPI metadata. Endpoint-level docs live on the controller and DTOs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketTrackerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Ticket Tracker API")
                        .description("REST API for creating, listing, updating and counting support tickets.")
                        .version("v1")
                        .contact(new Contact().name("Harrison Kiprono")));
    }
}
