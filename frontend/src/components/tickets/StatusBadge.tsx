import { STATUS_LABELS } from '../../constants/ticket';
import type { TicketStatus } from '../../types/ticket';
import { Badge } from '../common/Badge';

interface StatusBadgeProps {
  status: TicketStatus;
}

export function StatusBadge({ status }: StatusBadgeProps) {
  return <Badge tone={status}>{STATUS_LABELS[status]}</Badge>;
}
