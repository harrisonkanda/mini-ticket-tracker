package com.kiprono.tickettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Standard error body returned by the API")
public record ErrorResponse(

        @Schema(example = "2026-10-13T06:00:00Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "HTTP reason phrase", example = "Bad Request")
        String error,

        @Schema(description = "Human-readable summary", example = "Validation failed")
        String message,

        @Schema(description = "Request path", example = "/api/v1/tickets")
        String path,

        @Schema(description = "Per-field problems, when there are any", example = "[\"title: Title is required\"]")
        List<String> details
) {
}
