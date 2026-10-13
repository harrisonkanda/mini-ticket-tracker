export type TicketStatus = 'open' | 'in_progress' | 'closed';
export type TicketPriority = 'low' | 'medium' | 'high';

/** "No filter" option for the status dropdown. */
export type StatusFilter = TicketStatus | 'all';

export interface Ticket {
  id: number;
  title: string;
  description: string | null;
  status: TicketStatus;
  priority: TicketPriority;
  /** ISO-8601 timestamp (UTC). */
  createdAt: string;
}

export interface CreateTicketPayload {
  title: string;
  description?: string;
  priority?: TicketPriority;
}

export interface UpdateTicketPayload {
  status?: TicketStatus;
  priority?: TicketPriority;
}

export interface TicketStats {
  byStatus: Record<TicketStatus, number>;
  total: number;
}
