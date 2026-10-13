package com.kiprono.tickettracker.constant;

import com.kiprono.tickettracker.domain.TicketPriority;
import com.kiprono.tickettracker.domain.TicketStatus;

/**
 * Ticket rules shared by the entity mapping, request validation and the service.
 */
public final class TicketConstants {

    /** Keep in sync with the column width in V1__create_tickets_table.sql. */
    public static final int TITLE_MAX_LENGTH = 200;
    public static final int DESCRIPTION_MAX_LENGTH = 5000;

    public static final TicketStatus INITIAL_STATUS = TicketStatus.OPEN;
    public static final TicketPriority DEFAULT_PRIORITY = TicketPriority.MEDIUM;

    /** Entity property the ticket list is ordered by (newest first). */
    public static final String SORT_PROPERTY = "createdAt";
    public static final String SORT_TIE_BREAKER = "id";

    private TicketConstants() {
    }
}
