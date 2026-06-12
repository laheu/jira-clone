import type { ReactNode } from 'react';
import { attachmentUrl } from './api';
import type { Attachment, PriorityRef, StatusRef, UserRef } from './types';

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

/**
 * Renders issue/comment text and replaces Jira wiki image references like
 * !screenshot.png! or !screenshot.png|width=300! with the actual attachment image.
 */
export function BodyText({
  text,
  attachments,
  className,
}: {
  text: string;
  attachments: Attachment[];
  className?: string;
}) {
  const nodes: ReactNode[] = [];
  const imageRef = /!([^!|\n]+?)(\|[^!\n]*)?!/g;
  let last = 0;
  let match;
  let key = 0;
  while ((match = imageRef.exec(text)) !== null) {
    const filename = match[1].trim().toLowerCase();
    const attachment = attachments.find((a) => a.filename.toLowerCase() === filename);
    if (!attachment || !attachment.mimeType.startsWith('image/')) {
      continue; // no matching image attachment: keep the original text
    }
    nodes.push(text.slice(last, match.index));
    nodes.push(
      <a key={key++} href={attachmentUrl(attachment.id)} target="_blank" rel="noreferrer">
        <img className="inline-image" src={attachmentUrl(attachment.id)} alt={attachment.filename} />
      </a>,
    );
    last = match.index + match[0].length;
  }
  nodes.push(text.slice(last));
  return <p className={className ?? 'issue-description'}>{nodes}</p>;
}

export function formatBytes(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} kB`;
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
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
