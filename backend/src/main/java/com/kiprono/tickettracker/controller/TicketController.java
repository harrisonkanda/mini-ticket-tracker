package com.kiprono.tickettracker.controller;

import com.kiprono.tickettracker.constant.ApiPaths;
import com.kiprono.tickettracker.domain.TicketStatus;
import com.kiprono.tickettracker.dto.request.CreateTicketRequest;
import com.kiprono.tickettracker.dto.request.UpdateTicketRequest;
import com.kiprono.tickettracker.dto.response.ErrorResponse;
import com.kiprono.tickettracker.dto.response.PageResponse;
import com.kiprono.tickettracker.dto.response.TicketResponse;
import com.kiprono.tickettracker.dto.response.TicketStatsResponse;
import com.kiprono.tickettracker.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP adapter for tickets: request binding, validation and OpenAPI docs.
 * Business rules live in {@link TicketService}.
 */
@RestController
@RequestMapping(ApiPaths.TICKETS)
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Create, list, update, and aggregate support tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a ticket", description = "New tickets always start as open. Priority defaults to medium when omitted.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket created",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List tickets",
            description = "Returns one page of tickets, newest first, optionally filtered by status. "
                    + "Pages are 1-based; limit falls back to the server default and is capped server-side."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of tickets"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or status",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public PageResponse<TicketResponse> list(
            @Parameter(description = "Only return tickets in this status; omit for all")
            @RequestParam(required = false) TicketStatus status,
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size (default 20, max 100 unless reconfigured)", example = "20")
            @RequestParam(required = false) Integer limit
    ) {
        return ticketService.list(status, page, limit);
    }

    @PatchMapping(
            path = ApiPaths.TICKET_BY_ID,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update ticket status or priority", description = "Partial update; supply status and/or priority.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket updated",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
            @ApiResponse(responseCode = "400", description = "Empty body or unknown status/priority",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TicketResponse update(
            @Parameter(description = "Ticket id", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest request
    ) {
        return ticketService.update(id, request);
    }

    @GetMapping(path = ApiPaths.TICKET_STATS, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ticket stats", description = "Returns ticket counts grouped by status. Statuses with no tickets are returned as 0.")
    @ApiResponse(responseCode = "200", description = "Counts by status",
            content = @Content(schema = @Schema(implementation = TicketStatsResponse.class)))
    public TicketStatsResponse stats() {
        return ticketService.stats();
    }
}
