package com.kiprono.tickettracker.mapper;

import com.kiprono.tickettracker.domain.Ticket;
import com.kiprono.tickettracker.dto.response.PageResponse;
import com.kiprono.tickettracker.dto.response.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Entity to DTO mapping. Hand-written on purpose: with one entity, MapStruct would be
 * more build setup than it saves.
 */
@Component
public class TicketMapper {

    public TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt()
        );
    }

    public PageResponse<TicketResponse> toPageResponse(Page<Ticket> page) {
        return new PageResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
