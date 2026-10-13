package com.kiprono.tickettracker.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kiprono.tickettracker.exception.InvalidEnumValueException;

/**
 * Lifecycle state of a support ticket.
 * Stored in the database as the enum name ({@code IN_PROGRESS}) and sent over the API
 * in lowercase ({@code in_progress}), as the brief specifies.
 */
public enum TicketStatus {

    OPEN,
    IN_PROGRESS,
    CLOSED;

    /** The value used in JSON and query params, e.g. {@code in_progress}. */
    @JsonValue
    public String wireValue() {
        return name().toLowerCase();
    }

    /** Turns {@code "in_progress"} from a request back into {@link #IN_PROGRESS}. */
    @JsonCreator
    public static TicketStatus fromWireValue(String value) {
        for (TicketStatus status : values()) {
            if (status.wireValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new InvalidEnumValueException(
                "Invalid status '%s'. Allowed values: open, in_progress, closed".formatted(value));
    }
}
