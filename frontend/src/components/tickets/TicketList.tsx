import type { PageResponse } from '../../types/api';
import type { StatusFilter, Ticket, TicketStatus } from '../../types/ticket';
import { Pagination } from '../common/Pagination';
import { SelectField } from '../common/SelectField';
import { TicketRow } from './TicketRow';
import { STATUS_FILTER_OPTIONS } from './ticketOptions';

interface TicketListProps {
  pageData: PageResponse<Ticket> | null;
  loading: boolean;
  error: string | null;
  statusFilter: StatusFilter;
  onPageChange: (page: number) => void;
  onStatusFilterChange: (status: StatusFilter) => void;
  onStatusChange: (ticket: Ticket, status: TicketStatus) => Promise<void>;
}

export function TicketList({
  pageData,
  loading,
  error,
  statusFilter,
  onPageChange,
  onStatusFilterChange,
  onStatusChange,
}: TicketListProps) {
  const tickets = pageData?.content ?? [];
  const isEmpty = !loading && !error && pageData !== null && tickets.length === 0;

  return (
    <section className="panel" aria-busy={loading}>
      <div className="panel-header row">
        <div>
          <h2>Tickets</h2>
          <p>Newest first.</p>
        </div>
        <SelectField
          className="inline-filter"
          label="Status"
          value={statusFilter}
          options={STATUS_FILTER_OPTIONS}
          onChange={onStatusFilterChange}
        />
      </div>

      {error && (
        <p className="feedback error" role="alert">
          {error}
        </p>
      )}
      {loading && pageData === null && <p className="muted">Loading tickets…</p>}
      {isEmpty && (
        <p className="muted">
          {statusFilter === 'all' ? 'No tickets yet. Create the first one.' : 'No tickets with this status.'}
        </p>
      )}

      {tickets.length > 0 && (
        // Keep the previous page on screen (dimmed) while the next one loads, so the table
        // does not collapse and jump on every page or filter change.
        <div className={`table-wrap${loading ? ' is-loading' : ''}`}>
          <table>
            <thead>
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Title</th>
                <th scope="col">Priority</th>
                <th scope="col">Status</th>
                <th scope="col">Change status</th>
                <th scope="col">Created</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <TicketRow key={ticket.id} ticket={ticket} onStatusChange={onStatusChange} />
              ))}
            </tbody>
          </table>
        </div>
      )}

      {pageData && (
        <Pagination
          page={pageData.page}
          totalPages={pageData.totalPages}
          totalElements={pageData.totalElements}
          disabled={loading}
          onPageChange={onPageChange}
        />
      )}
    </section>
  );
}
