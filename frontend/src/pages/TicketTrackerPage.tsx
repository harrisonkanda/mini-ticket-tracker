import { createTicket, updateTicket } from '../api/ticketsApi';
import { CreateTicketForm } from '../components/tickets/CreateTicketForm';
import { TicketList } from '../components/tickets/TicketList';
import { TicketStatsBar } from '../components/tickets/TicketStatsBar';
import { useTicketStats } from '../hooks/useTicketStats';
import { useTickets } from '../hooks/useTickets';
import type { CreateTicketPayload, Ticket, TicketStatus } from '../types/ticket';

/**
 * The single screen of the app. Owns the data (list + stats) and the two mutations;
 * the components below it are presentational and report intent through callbacks.
 */
export function TicketTrackerPage() {
  const tickets = useTickets();
  const stats = useTicketStats();

  // After any write, re-read from the server rather than patching local state:
  // the list is filtered and paginated server-side, and the counts must stay in step with it.
  async function refreshAll() {
    await Promise.all([tickets.refresh(), stats.refresh()]);
  }

  async function handleCreate(payload: CreateTicketPayload) {
    await createTicket(payload);
    await refreshAll();
  }

  async function handleStatusChange(ticket: Ticket, status: TicketStatus) {
    await updateTicket(ticket.id, { status });
    await refreshAll();
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <div>
          <p className="eyebrow">Mini Ticket Tracker</p>
          <h1>Support queue</h1>
        </div>
        <TicketStatsBar stats={stats.stats} error={stats.error} />
      </header>

      <main className="layout">
        <CreateTicketForm onCreate={handleCreate} />
        <TicketList
          pageData={tickets.pageData}
          loading={tickets.loading}
          error={tickets.error}
          statusFilter={tickets.statusFilter}
          onPageChange={tickets.changePage}
          onStatusFilterChange={tickets.changeStatusFilter}
          onStatusChange={handleStatusChange}
        />
      </main>
    </div>
  );
}
