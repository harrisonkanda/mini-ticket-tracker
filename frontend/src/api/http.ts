import { API_BASE_URL } from '../constants/config';
import type { ApiErrorBody } from '../types/api';

export class ApiError extends Error {
  readonly status: number;
  readonly body: ApiErrorBody | null;

  constructor(message: string, status: number, body: ApiErrorBody | null = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

async function toApiError(response: Response): Promise<ApiError> {
  try {
    const body = (await response.json()) as ApiErrorBody;
    return new ApiError(body.message || response.statusText, response.status, body);
  } catch {
    // Not JSON (e.g. a proxy error page) - fall back to the status line.
    return new ApiError(response.statusText || 'Request failed', response.status);
  }
}

/** Thin fetch wrapper: prefixes the API base URL, sends/accepts JSON, throws ApiError on non-2xx. */
export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      Accept: 'application/json',
      ...(init.body ? { 'Content-Type': 'application/json' } : {}),
      ...init.headers,
    },
  });

  if (!response.ok) {
    throw await toApiError(response);
  }

  return (await response.json()) as T;
}
