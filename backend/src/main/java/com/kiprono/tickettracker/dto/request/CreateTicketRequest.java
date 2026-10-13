package com.kiprono.tickettracker.dto.request;

import com.kiprono.tickettracker.constant.TicketConstants;
import com.kiprono.tickettracker.domain.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating a new support ticket")
public record CreateTicketRequest(

        @NotBlank(message = "Title is required")
        @Size(max = TicketConstants.TITLE_MAX_LENGTH, message = "Title must be at most {max} characters")
        @Schema(description = "Short summary of the issue", example = "Unable to reset password")
        String title,

        @Size(max = TicketConstants.DESCRIPTION_MAX_LENGTH, message = "Description must be at most {max} characters")
        @Schema(description = "Optional details", example = "Reset link returns 404 on staging",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,

        @Schema(description = "Priority; defaults to medium when omitted", example = "high",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        TicketPriority priority
) {
}
