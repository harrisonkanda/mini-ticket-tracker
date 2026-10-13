package com.kiprono.tickettracker.service;

import com.kiprono.tickettracker.config.TicketTrackerProperties;
import com.kiprono.tickettracker.constant.TicketConstants;
import com.kiprono.tickettracker.domain.Ticket;
import com.kiprono.tickettracker.domain.TicketPriority;
import com.kiprono.tickettracker.domain.TicketStatus;
import com.kiprono.tickettracker.dto.request.CreateTicketRequest;
import com.kiprono.tickettracker.dto.request.UpdateTicketRequest;
import com.kiprono.tickettracker.dto.response.PageResponse;
import com.kiprono.tickettracker.dto.response.TicketResponse;
import com.kiprono.tickettracker.dto.response.TicketStatsResponse;
import com.kiprono.tickettracker.exception.BadRequestException;
import com.kiprono.tickettracker.exception.ResourceNotFoundException;
import com.kiprono.tickettracker.mapper.TicketMapper;
import com.kiprono.tickettracker.repository.TicketRepository;
import com.kiprono.tickettracker.repository.TicketStatusCount;
import com.kiprono.tickettracker.util.PaginationUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ticket use cases. Owns the transaction boundaries and the business defaults;
 * the controller stays a thin HTTP adapter.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    // id breaks ties between tickets created in the same instant, keeping page boundaries stable.
    private static final Sort NEWEST_FIRST =
            Sort.by(Sort.Direction.DESC, TicketConstants.SORT_PROPERTY, TicketConstants.SORT_TIE_BREAKER);

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final TicketTrackerProperties properties;

    /**
     * Creates a ticket. New tickets always start as {@code open}; the client cannot choose
     * the initial status. Priority falls back to {@code medium} when omitted.
     */
    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        TicketPriority priority = request.priority() != null
                ? request.priority()
                : TicketConstants.DEFAULT_PRIORITY;

        Ticket ticket = new Ticket(
                request.title().trim(),
                blankToNull(request.description()),
                TicketConstants.INITIAL_STATUS,
                priority
        );

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    /**
     * Lists tickets newest first, optionally filtered by status.
     *
     * @param status filter, or {@code null} for all tickets
     * @param page   1-based page number, or {@code null} for the first page
     * @param limit  page size, or {@code null} for the configured default
     */
    @Transactional(readOnly = true)
    public PageResponse<TicketResponse> list(TicketStatus status, Integer page, Integer limit) {
        Pageable pageable = PaginationUtils.toPageable(page, limit, properties.pagination(), NEWEST_FIRST);
        Page<Ticket> tickets = status == null
                ? ticketRepository.findAll(pageable)
                : ticketRepository.findByStatus(status, pageable);

        return ticketMapper.toPageResponse(tickets);
    }

    /**
     * Applies a partial update of status and/or priority. Any status may move to any other:
     * support staff reopen tickets all the time, so no transition rules are enforced.
     */
    @Transactional
    public TicketResponse update(Long id, UpdateTicketRequest request) {
        if (request.status() == null && request.priority() == null) {
            throw new BadRequestException("At least one of status or priority must be provided");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found with id %d".formatted(id)));

        if (request.status() != null) {
            ticket.setStatus(request.status());
        }
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }

        // No explicit save: the entity is managed, so the change is flushed on commit.
        return ticketMapper.toResponse(ticket);
    }

    /**
     * Counts tickets per status. Every status is present in the result, with zero when
     * it has no tickets, so clients never have to handle a missing key.
     */
    @Transactional(readOnly = true)
    public TicketStatsResponse stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (TicketStatus status : TicketStatus.values()) {
            byStatus.put(status.wireValue(), 0L);
        }

        long total = 0L;
        for (TicketStatusCount row : ticketRepository.countGroupedByStatus()) {
            byStatus.put(row.status().wireValue(), row.count());
            total += row.count();
        }

        return new TicketStatsResponse(byStatus, total);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
