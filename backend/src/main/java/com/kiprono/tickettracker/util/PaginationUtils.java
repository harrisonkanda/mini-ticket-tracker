package com.kiprono.tickettracker.util;

import com.kiprono.tickettracker.config.TicketTrackerProperties;
import com.kiprono.tickettracker.constant.PaginationConstants;
import com.kiprono.tickettracker.exception.BadRequestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Converts the public 1-based {@code page}/{@code limit} contract into Spring Data's
 * 0-based {@link Pageable}, applying the configured default and maximum page size.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationUtils {

    /**
     * @param page  1-based page number, or {@code null} for the first page
     * @param limit page size, or {@code null} for the configured default
     * @throws BadRequestException when either value is out of range. Out-of-range values are
     *                             rejected rather than silently clamped so client bugs surface early.
     */
    public static Pageable toPageable(
            Integer page,
            Integer limit,
            TicketTrackerProperties.Pagination settings,
            Sort sort
    ) {
        int resolvedPage = page != null ? page : PaginationConstants.FIRST_PAGE;
        int resolvedLimit = limit != null ? limit : settings.defaultLimit();

        if (resolvedPage < PaginationConstants.FIRST_PAGE) {
            throw new BadRequestException(
                    "page must be >= %d".formatted(PaginationConstants.FIRST_PAGE));
        }
        if (resolvedLimit < PaginationConstants.MIN_LIMIT || resolvedLimit > settings.maxLimit()) {
            throw new BadRequestException(
                    "limit must be between %d and %d"
                            .formatted(PaginationConstants.MIN_LIMIT, settings.maxLimit()));
        }

        return PageRequest.of(resolvedPage - 1, resolvedLimit, sort);
    }
}
