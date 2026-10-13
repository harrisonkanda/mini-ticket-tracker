import type { TicketPriority, TicketStatus } from '../types/ticket';

export const TICKET_STATUSES: readonly TicketStatus[] = ['open', 'in_progress', 'closed'];

export const TICKET_PRIORITIES: readonly TicketPriority[] = ['low', 'medium', 'high'];

export const STATUS_LABELS: Record<TicketStatus, string> = {
  open: 'Open',
  in_progress: 'In progress',
  closed: 'Closed',
};

export const PRIORITY_LABELS: Record<TicketPriority, string> = {
  low: 'Low',
  medium: 'Medium',
  high: 'High',
};

export const DEFAULT_PRIORITY: TicketPriority = 'medium';

/** Mirror the server-side validation so the form stops the user before the API does. */
export const TITLE_MAX_LENGTH = 200;
export const DESCRIPTION_MAX_LENGTH = 5000;

export const FIRST_PAGE = 1;
export const PAGE_SIZE = 10;
