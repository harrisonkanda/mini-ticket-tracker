import type { ReactNode } from 'react';

interface BadgeProps {
  /** Selects the colour scheme; maps to a `badge-<tone>` class in global.css. */
  tone: string;
  children: ReactNode;
}

export function Badge({ tone, children }: BadgeProps) {
  return <span className={`badge badge-${tone}`}>{children}</span>;
}
