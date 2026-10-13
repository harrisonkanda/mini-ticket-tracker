package com.kiprono.tickettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list response (1-based page index)")
public record PageResponse<T>(

        @Schema(description = "Page items")
        List<T> content,

        @Schema(description = "Current page (1-based)", example = "1")
        int page,

        @Schema(description = "Page size", example = "20")
        int limit,

        @Schema(description = "Total matching rows", example = "42")
        long totalElements,

        @Schema(description = "Total pages", example = "3")
        int totalPages
) {
}
