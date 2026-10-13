import { STATUS_LABELS, TICKET_STATUSES } from '../../constants/ticket';
import type { TicketStats } from '../../types/ticket';

interface TicketStatsBarProps {
  stats: TicketStats | null;
  error: string | null;
}

export function TicketStatsBar({ stats, error }: TicketStatsBarProps) {
  if (error) {
    return <p className="muted">Stats unavailable: {error}</p>;
  }

  if (!stats) {
    return <p className="muted">Loading stats…</p>;
  }

  return (
    <dl className="stats-row" aria-label="Ticket counts by status">
      <StatChip label="Total" value={stats.total} />
      {TICKET_STATUSES.map((status) => (
        <StatChip key={status} label={STATUS_LABELS[status]} value={stats.byStatus[status] ?? 0} />
      ))}
    </dl>
  );
}

function StatChip({ label, value }: { label: string; value: number }) {
  return (
    <div className="stat-chip">
      <dt className="stat-label">{label}</dt>
      <dd className="stat-value">{value}</dd>
    </div>
  );
}
