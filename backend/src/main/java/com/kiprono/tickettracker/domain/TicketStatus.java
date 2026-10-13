package com.kiprono.tickettracker.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kiprono.tickettracker.util.WireEnums;

/**
 * Lifecycle state of a support ticket.
 * Stored as the enum name ({@code IN_PROGRESS}); exposed over the API in the
 * lowercase form from the brief ({@code in_progress}).
 */
public enum TicketStatus {

    OPEN,
    IN_PROGRESS,
    CLOSED;

    @JsonValue
    public String wireValue() {
        return WireEnums.toWireValue(this);
    }

    @JsonCreator
    public static TicketStatus fromWireValue(String value) {
        return WireEnums.parse(TicketStatus.class, value, "status");
    }
}
