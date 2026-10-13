package com.kiprono.tickettracker.repository;

import com.kiprono.tickettracker.domain.TicketStatus;

/**
 * One row of the "tickets per status" aggregate.
 */
public record TicketStatusCount(TicketStatus status, long count) {
}
