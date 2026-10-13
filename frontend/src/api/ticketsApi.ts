import { API_ROUTES } from '../constants/config';
import type { PageResponse } from '../types/api';
import type {
  CreateTicketPayload,
  Ticket,
  TicketStats,
  TicketStatus,
  UpdateTicketPayload,
} from '../types/ticket';
import { request } from './http';

export interface ListTicketsParams {
  status?: TicketStatus;
  page?: number;
  limit?: number;
}

export function createTicket(payload: CreateTicketPayload): Promise<Ticket> {
  return request<Ticket>(API_ROUTES.tickets, {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function listTickets(params: ListTicketsParams = {}): Promise<PageResponse<Ticket>> {
  const query = new URLSearchParams();
  if (params.status) {
    query.set('status', params.status);
  }
  if (params.page != null) {
    query.set('page', String(params.page));
  }
  if (params.limit != null) {
    query.set('limit', String(params.limit));
  }
  const queryString = query.toString();
  return request<PageResponse<Ticket>>(
    queryString ? `${API_ROUTES.tickets}?${queryString}` : API_ROUTES.tickets,
  );
}

export function updateTicket(id: number, payload: UpdateTicketPayload): Promise<Ticket> {
  return request<Ticket>(API_ROUTES.ticket(id), {
    method: 'PATCH',
    body: JSON.stringify(payload),
  });
}

export function fetchTicketStats(): Promise<TicketStats> {
  return request<TicketStats>(API_ROUTES.ticketStats);
}
