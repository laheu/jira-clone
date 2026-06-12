import type { PriorityRef, StatusRef, UserRef } from './types';

export function StatusBadge({ status }: { status: StatusRef | null }) {
  if (!status) return null;
  return <span className={`status-badge status-${status.category}`}>{status.name}</span>;
}

export function PriorityLabel({ priority }: { priority: PriorityRef | null }) {
  if (!priority?.name) return <span className="muted">–</span>;
  return (
    <span className="priority">
      <span className="priority-dot" style={{ background: priority.color ?? '#999' }} />
      {priority.name}
    </span>
  );
}

export function UserName({ user }: { user: UserRef | null }) {
  if (!user) return <span className="muted">Nicht zugewiesen</span>;
  return <span title={user.email ?? undefined}>{user.displayName}</span>;
}

export function formatDateTime(value: string | null): string {
  if (!value) return '–';
  return new Date(value).toLocaleString('de-DE', {
    dateStyle: 'medium',
    timeStyle: 'short',
  });
}

export function formatDate(value: string | null): string {
  if (!value) return '–';
  return new Date(value).toLocaleDateString('de-DE', { dateStyle: 'medium' });
}
