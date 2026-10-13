package com.kiprono.tickettracker.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Typed view of the {@code ticket-tracker.*} block in application.yml.
 * Validated at startup so a bad value fails fast instead of surfacing on the first request.
 */
@Validated
@ConfigurationProperties(prefix = "ticket-tracker")
public record TicketTrackerProperties(
        @Valid @NotNull Pagination pagination,
        @Valid @NotNull Cors cors
) {

    /**
     * @param defaultLimit page size used when the client does not send {@code limit}
     * @param maxLimit     upper bound on {@code limit}, so a client cannot pull the whole table
     */
    public record Pagination(
            @Min(1) int defaultLimit,
            @Min(1) int maxLimit
    ) {
    }

    public record Cors(
            @NotNull List<String> allowedOrigins,
            @NotEmpty List<String> allowedMethods
    ) {
    }
}
