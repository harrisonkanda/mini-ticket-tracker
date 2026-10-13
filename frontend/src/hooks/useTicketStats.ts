import { useCallback, useEffect, useState } from 'react';
import { fetchTicketStats } from '../api/ticketsApi';
import type { TicketStats } from '../types/ticket';
import { toErrorMessage } from '../utils/errors';

export interface UseTicketStatsResult {
  stats: TicketStats | null;
  error: string | null;
  refresh: () => Promise<void>;
}

/** Ticket counts per status for the header summary. */
export function useTicketStats(): UseTicketStatsResult {
  const [stats, setStats] = useState<TicketStats | null>(null);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      setStats(await fetchTicketStats());
      setError(null);
    } catch (err) {
      setError(toErrorMessage(err, 'Unable to load stats'));
    }
  }, []);

  useEffect(() => {
    // oxlint-disable-next-line react/set-state-in-effect -- state is only set after the fetch resolves
    void refresh();
  }, [refresh]);

  return { stats, error, refresh };
}
