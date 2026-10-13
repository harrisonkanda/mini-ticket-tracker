package com.kiprono.tickettracker.config;

import com.kiprono.tickettracker.constant.ApiPaths;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS for the browser client. Origins and methods come from YAML so each environment
 * can point at its own UI host without a code change.
 */
@Configuration
@EnableConfigurationProperties(TicketTrackerProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final TicketTrackerProperties.Cors cors;

    public WebConfig(TicketTrackerProperties properties) {
        this.cors = properties.cors();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (cors.allowedOrigins().isEmpty()) {
            return;
        }
        registry.addMapping(ApiPaths.API_V1 + "/**")
                .allowedOrigins(cors.allowedOrigins().toArray(String[]::new))
                .allowedMethods(cors.allowedMethods().toArray(String[]::new))
                .allowedHeaders("*");
    }
}
