import { PRIORITY_LABELS } from '../../constants/ticket';
import type { TicketPriority } from '../../types/ticket';
import { Badge } from '../common/Badge';

interface PriorityBadgeProps {
  priority: TicketPriority;
}

export function PriorityBadge({ priority }: PriorityBadgeProps) {
  return <Badge tone={priority}>{PRIORITY_LABELS[priority]}</Badge>;
}
