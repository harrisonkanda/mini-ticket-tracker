import { useState } from 'react';
import type { Ticket, TicketStatus } from '../../types/ticket';
import { formatDateTime } from '../../utils/date';
import { toErrorMessage } from '../../utils/errors';
import { SelectField } from '../common/SelectField';
import { PriorityBadge } from './PriorityBadge';
import { StatusBadge } from './StatusBadge';
import { STATUS_OPTIONS } from './ticketOptions';

interface TicketRowProps {
  ticket: Ticket;
  onStatusChange: (ticket: Ticket, status: TicketStatus) => Promise<void>;
}

export function TicketRow({ ticket, onStatusChange }: TicketRowProps) {
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleStatusChange(status: TicketStatus) {
    if (status === ticket.status) {
      return;
    }
    setSaving(true);
    setError(null);
    try {
      await onStatusChange(ticket, status);
    } catch (err) {
      setError(toErrorMessage(err, 'Update failed, please retry'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <tr>
      <td className="mono">#{ticket.id}</td>
      <td>
        <div className="ticket-title">{ticket.title}</div>
        {ticket.description && <div className="ticket-desc">{ticket.description}</div>}
      </td>
      <td>
        <PriorityBadge priority={ticket.priority} />
      </td>
      <td>
        <StatusBadge status={ticket.status} />
      </td>
      <td>
        <SelectField
          ariaLabel={`Change status of ticket ${ticket.id}`}
          value={ticket.status}
          options={STATUS_OPTIONS}
          disabled={saving}
          onChange={(status) => void handleStatusChange(status)}
        />
        {error && (
          <p className="feedback error" role="alert">
            {error}
          </p>
        )}
      </td>
      <td className="mono nowrap">{formatDateTime(ticket.createdAt)}</td>
    </tr>
  );
}
