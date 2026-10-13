package com.kiprono.tickettracker.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kiprono.tickettracker.exception.InvalidEnumValueException;

/**
 * How urgent a ticket is. Sent over the API as {@code low}, {@code medium} or {@code high}.
 */
public enum TicketPriority {

    LOW,
    MEDIUM,
    HIGH;

    /** The value used in JSON, e.g. {@code high}. */
    @JsonValue
    public String wireValue() {
        return name().toLowerCase();
    }

    /** Turns {@code "high"} from a request back into {@link #HIGH}. */
    @JsonCreator
    public static TicketPriority fromWireValue(String value) {
        for (TicketPriority priority : values()) {
            if (priority.wireValue().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new InvalidEnumValueException(
                "Invalid priority '%s'. Allowed values: low, medium, high".formatted(value));
    }
}
