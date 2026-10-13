import { ApiError } from '../api/http';

/**
 * Message to show the user for a failed request. Prefers the field-level details the API
 * sends for validation errors, then its summary message, then the caller's fallback
 * (network down, CORS, etc.).
 */
export function toErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof ApiError) {
    const details = error.body?.details;
    return details && details.length > 0 ? details.join('; ') : error.message;
  }
  return fallback;
}
