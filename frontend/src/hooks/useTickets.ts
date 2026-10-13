import { useCallback, useEffect, useState } from 'react';
import { listTickets } from '../api/ticketsApi';
import { FIRST_PAGE, PAGE_SIZE } from '../constants/ticket';
import type { PageResponse } from '../types/api';
import type { StatusFilter, Ticket } from '../types/ticket';
import { toErrorMessage } from '../utils/errors';

export interface UseTicketsResult {
  pageData: PageResponse<Ticket> | null;
  loading: boolean;
  error: string | null;
  statusFilter: StatusFilter;
  changePage: (page: number) => void;
  changeStatusFilter: (status: StatusFilter) => void;
  /** Loads the current page again, e.g. after a ticket was created or updated. */
  refresh: () => Promise<void>;
}

/**
 * Loads one page of tickets from the API and reloads it whenever the page number
 * or the status filter changes.
 */
export function useTickets(): UseTicketsResult {
  const [page, setPage] = useState(FIRST_PAGE);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('all');
  const [pageData, setPageData] = useState<PageResponse<Ticket> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadTickets = useCallback(async () => {
    try {
      const data = await listTickets({
        page,
        limit: PAGE_SIZE,
        status: statusFilter === 'all' ? undefined : statusFilter,
      });

      // If the last ticket on this page was just moved out of the filter (e.g. closed while
      // filtering on "open"), the page no longer exists. Go back to the new last page.
      const pageNoLongerExists = data.totalPages > 0 && page > data.totalPages;
      if (pageNoLongerExists) {
        setPage(data.totalPages);
        return;
      }

      setPageData(data);
      setError(null);
    } catch (err) {
      setError(toErrorMessage(err, 'Unable to load tickets. Is the API running?'));
    }
    setLoading(false);
  }, [page, statusFilter]);

  // Runs on first render, and again every time page or statusFilter changes.
  useEffect(() => {
    // oxlint-disable-next-line react/set-state-in-effect -- state is only set after the fetch resolves
    void loadTickets();
  }, [loadTickets]);

  function changePage(newPage: number) {
    setLoading(true);
    setPage(newPage);
  }

  function changeStatusFilter(newStatus: StatusFilter) {
    setLoading(true);
    setStatusFilter(newStatus);
    setPage(FIRST_PAGE);
  }

  return {
    pageData,
    loading,
    error,
    statusFilter,
    changePage,
    changeStatusFilter,
    refresh: loadTickets,
  };
}
