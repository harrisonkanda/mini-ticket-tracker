import { useCallback, useEffect, useRef, useState } from 'react';
import { listTickets } from '../api/ticketsApi';
import { FIRST_PAGE, PAGE_SIZE } from '../constants/ticket';
import type { PageResponse } from '../types/api';
import type { StatusFilter, Ticket } from '../types/ticket';
import { toErrorMessage } from '../utils/errors';

interface TicketQuery {
  page: number;
  status: StatusFilter;
}

export interface UseTicketsResult {
  pageData: PageResponse<Ticket> | null;
  loading: boolean;
  error: string | null;
  page: number;
  statusFilter: StatusFilter;
  setPage: (page: number) => void;
  setStatusFilter: (status: StatusFilter) => void;
  /** Re-fetches the current page, e.g. after a ticket was created or updated. */
  refresh: () => Promise<void>;
}

/**
 * Server-side paginated ticket list, with the status filter and current page as state.
 */
export function useTickets(): UseTicketsResult {
  const [query, setQuery] = useState<TicketQuery>({ page: FIRST_PAGE, status: 'all' });
  const [pageData, setPageData] = useState<PageResponse<Ticket> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Responses can arrive out of order when the user changes filter or page quickly;
  // only the most recent request is allowed to update state.
  const latestRequestId = useRef(0);

  const load = useCallback(async ({ page, status }: TicketQuery) => {
    const requestId = ++latestRequestId.current;
    const isStale = () => requestId !== latestRequestId.current;

    try {
      const data = await listTickets({
        page,
        limit: PAGE_SIZE,
        status: status === 'all' ? undefined : status,
      });
      if (isStale()) {
        return;
      }
      // Moving the last ticket off a page (e.g. closing it while filtered on "open")
      // leaves that page empty, so step back to the new last page.
      if (data.totalPages > 0 && page > data.totalPages) {
        setQuery({ page: data.totalPages, status });
        return;
      }
      setPageData(data);
      setError(null);
      setLoading(false);
    } catch (err) {
      if (isStale()) {
        return;
      }
      setError(toErrorMessage(err, 'Unable to load tickets. Is the API running?'));
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // oxlint-disable-next-line react/set-state-in-effect -- state is only set after the fetch resolves
    void load(query);
  }, [load, query]);

  const setPage = useCallback((page: number) => {
    setLoading(true);
    setQuery((current) => ({ ...current, page }));
  }, []);

  const setStatusFilter = useCallback((status: StatusFilter) => {
    setLoading(true);
    setQuery({ page: FIRST_PAGE, status });
  }, []);

  const refresh = useCallback(() => load(query), [load, query]);

  return {
    pageData,
    loading,
    error,
    page: query.page,
    statusFilter: query.status,
    setPage,
    setStatusFilter,
    refresh,
  };
}
