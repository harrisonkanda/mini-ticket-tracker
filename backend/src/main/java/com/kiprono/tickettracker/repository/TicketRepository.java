package com.kiprono.tickettracker.repository;

import com.kiprono.tickettracker.domain.Ticket;
import com.kiprono.tickettracker.domain.TicketStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    /**
     * Counts tickets per status in a single GROUP BY.
     * Statuses with no tickets are absent from the result; the service fills those in with zero.
     */
    @Query("""
            SELECT new com.kiprono.tickettracker.repository.TicketStatusCount(t.status, COUNT(t))
            FROM Ticket t
            GROUP BY t.status
            """)
    List<TicketStatusCount> countGroupedByStatus();
}
