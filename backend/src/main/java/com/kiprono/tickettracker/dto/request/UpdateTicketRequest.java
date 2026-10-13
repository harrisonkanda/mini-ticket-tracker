package com.kiprono.tickettracker.dto.request;

import com.kiprono.tickettracker.domain.TicketPriority;
import com.kiprono.tickettracker.domain.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Partial update for status and/or priority. At least one field must be provided.")
public record UpdateTicketRequest(

        @Schema(description = "New status", example = "in_progress")
        TicketStatus status,

        @Schema(description = "New priority", example = "high")
        TicketPriority priority
) {
}
