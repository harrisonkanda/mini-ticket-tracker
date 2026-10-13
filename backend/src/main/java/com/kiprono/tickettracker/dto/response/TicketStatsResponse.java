package com.kiprono.tickettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Ticket counts grouped by status")
public record TicketStatsResponse(

        @Schema(
                description = "Count per status key (open, in_progress, closed)",
                example = "{\"open\":3,\"in_progress\":1,\"closed\":2}"
        )
        Map<String, Long> byStatus,

        @Schema(description = "Total tickets across all statuses", example = "6")
        long total
) {
}
