package com.kiprono.tickettracker.exception;

/**
 * Thrown when a client sends a status or priority that does not exist.
 * The message lists the allowed values and is safe to show to the client.
 */
public class InvalidEnumValueException extends IllegalArgumentException {

    public InvalidEnumValueException(String message) {
        super(message);
    }
}
