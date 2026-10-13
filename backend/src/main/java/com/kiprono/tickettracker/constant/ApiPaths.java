package com.kiprono.tickettracker.constant;

/**
 * Central place for HTTP path segments so controllers and docs stay aligned.
 */
public final class ApiPaths {

    public static final String API_V1 = "/api/v1";
    public static final String TICKETS = API_V1 + "/tickets";
    public static final String TICKET_BY_ID = "/{id}";
    public static final String TICKET_STATS = "/stats";

    private ApiPaths() {
    }
}
