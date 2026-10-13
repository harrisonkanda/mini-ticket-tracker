package com.kiprono.tickettracker.config;

import com.kiprono.tickettracker.constant.ApiPaths;
import com.kiprono.tickettracker.domain.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web layer setup: CORS for the browser client, and reading {@code ?status=in_progress}
 * from the query string.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TicketTrackerProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final TicketTrackerProperties properties;

    /**
     * Spring would only accept {@code ?status=IN_PROGRESS} by default. This makes query params
     * use the same lowercase values as the JSON body.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, TicketStatus.class, TicketStatus::fromWireValue);
    }

    /** Allowed origins and methods come from application.yml, so they can differ per environment. */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        TicketTrackerProperties.Cors cors = properties.cors();
        if (cors.allowedOrigins().isEmpty()) {
            return;
        }
        registry.addMapping(ApiPaths.API_V1 + "/**")
                .allowedOrigins(cors.allowedOrigins().toArray(String[]::new))
                .allowedMethods(cors.allowedMethods().toArray(String[]::new))
                .allowedHeaders("*");
    }
}
