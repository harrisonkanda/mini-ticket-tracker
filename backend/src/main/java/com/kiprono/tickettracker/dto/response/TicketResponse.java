package com.kiprono.tickettracker.dto.response;

import com.kiprono.tickettracker.domain.TicketPriority;
import com.kiprono.tickettracker.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Ticket as returned by the API")
public record TicketResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Unable to reset password")
        String title,

        @Schema(example = "Reset link returns 404 on staging")
        String description,

        @Schema(example = "open")
        TicketStatus status,

        @Schema(example = "medium")
        TicketPriority priority,

        @Schema(example = "2026-10-13T06:00:00Z")
        Instant createdAt
) {
}
