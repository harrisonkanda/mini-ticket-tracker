package com.kiprono.tickettracker.config;

import com.kiprono.tickettracker.domain.TicketPriority;
import com.kiprono.tickettracker.domain.TicketStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Lets query params use the same lowercase enum values as the JSON API
 * (e.g. {@code ?status=in_progress}).
 */
public final class EnumConverters {

    private EnumConverters() {
    }

    @Component
    public static class TicketStatusConverter implements Converter<String, TicketStatus> {
        @Override
        public TicketStatus convert(String source) {
            return TicketStatus.fromWireValue(source);
        }
    }

    @Component
    public static class TicketPriorityConverter implements Converter<String, TicketPriority> {
        @Override
        public TicketPriority convert(String source) {
            return TicketPriority.fromWireValue(source);
        }
    }
}
