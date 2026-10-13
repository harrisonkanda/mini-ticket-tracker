const FALLBACK_API_BASE_URL = 'http://localhost:8080/api/v1';

/** Base URL of the backend API, without a trailing slash. Set via VITE_API_BASE_URL. */
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/+$/, '') || FALLBACK_API_BASE_URL;

export const API_ROUTES = {
  tickets: '/tickets',
  ticket: (id: number) => `/tickets/${id}`,
  ticketStats: '/tickets/stats',
} as const;
