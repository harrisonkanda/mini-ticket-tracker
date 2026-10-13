package com.kiprono.tickettracker.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kiprono.tickettracker.util.WireEnums;

/**
 * Relative urgency of a support ticket.
 * Exposed over the API as {@code low}, {@code medium} or {@code high}.
 */
public enum TicketPriority {

    LOW,
    MEDIUM,
    HIGH;

    @JsonValue
    public String wireValue() {
        return WireEnums.toWireValue(this);
    }

    @JsonCreator
    public static TicketPriority fromWireValue(String value) {
        return WireEnums.parse(TicketPriority.class, value, "priority");
    }
}
