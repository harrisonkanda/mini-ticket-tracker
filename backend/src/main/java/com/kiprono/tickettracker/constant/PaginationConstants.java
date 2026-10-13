package com.kiprono.tickettracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Fixed parts of the pagination contract. Tunable values (default and maximum page size)
 * live under {@code ticket-tracker.pagination} in application.yml.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationConstants {

    /** Pages are 1-based in the public API. */
    public static final int FIRST_PAGE = 1;
    public static final int MIN_LIMIT = 1;
}
