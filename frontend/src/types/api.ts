/** Paged list envelope returned by the API. `page` is 1-based. */
export interface PageResponse<T> {
  content: T[];
  page: number;
  limit: number;
  totalElements: number;
  totalPages: number;
}

/** Error body the API returns for every non-2xx response. */
export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[];
}
