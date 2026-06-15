import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';
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

// Recognises, in order: image refs !file.png!, wiki links [text|url], bracketed
// urls [url], bare http(s) urls, and issue keys like DEMO-7.
const TOKEN =
  /(!([^!|\n]+?)(?:\|[^!\n]*)?!)|\[([^\]|\n]+)\|(https?:\/\/[^\]\s]+)\]|\[(https?:\/\/[^\]\s]+)\]|(https?:\/\/[^\s<>()]+)|(\b[A-Z][A-Z0-9]+-\d+\b)/g;

/**
 * Renders issue/comment text as React nodes. Jira wiki image references like
 * !screenshot.png! become the actual attachment image; URLs and Jira wiki
 * links ([text|url]) become clickable links; issue keys (e.g. DEMO-7) link to
 * the referenced issue, but only when their prefix is a known project so plain
 * text like "HTTP-500" is left untouched.
 */
export function BodyText({
  text,
  attachments,
  projectKeys = [],
  className,
}: {
  text: string;
  attachments: Attachment[];
  projectKeys?: string[];
  className?: string;
}) {
  const knownProjects = new Set(projectKeys.map((k) => k.toUpperCase()));
  const nodes: ReactNode[] = [];
  let last = 0;
  let match: RegExpExecArray | null;
  let key = 0;
  TOKEN.lastIndex = 0;

  const linkClass = 'body-link';

  while ((match = TOKEN.exec(text)) !== null) {
    const [full, imageToken, imageName, wikiText, wikiUrl, bracketUrl, bareUrl, issueKey] = match;

    // strip trailing sentence punctuation from bare urls
    let token: ReactNode = null;
    let consumed = full;

    if (imageToken) {
      const attachment = attachments.find(
        (a) => a.filename.toLowerCase() === imageName.trim().toLowerCase(),
      );
      if (attachment && attachment.mimeType.startsWith('image/')) {
        token = (
          <a key={key++} href={attachmentUrl(attachment.id)} target="_blank" rel="noreferrer">
            <img className="inline-image" src={attachmentUrl(attachment.id)} alt={attachment.filename} />
          </a>
        );
      }
    } else if (wikiUrl) {
      token = (
        <a key={key++} className={linkClass} href={wikiUrl} target="_blank" rel="noreferrer">
          {wikiText}
        </a>
      );
    } else if (bracketUrl || bareUrl) {
      let url = bracketUrl ?? bareUrl;
      let trailing = '';
      if (bareUrl) {
        const m = /[.,;:!?]+$/.exec(url);
        if (m) {
          trailing = m[0];
          url = url.slice(0, -trailing.length);
        }
      }
      token = (
        <a key={key++} className={linkClass} href={url} target="_blank" rel="noreferrer">
          {url}
        </a>
      );
      consumed = full.slice(0, full.length - trailing.length);
    } else if (issueKey) {
      const prefix = issueKey.slice(0, issueKey.lastIndexOf('-'));
      if (knownProjects.has(prefix.toUpperCase())) {
        token = (
          <Link key={key++} className={linkClass} to={`/issues/${issueKey}`}>
            {issueKey}
          </Link>
        );
      }
    }

    if (token === null) {
      continue; // leave the original text in place
    }
    nodes.push(text.slice(last, match.index));
    nodes.push(token);
    last = match.index + consumed.length;
    TOKEN.lastIndex = last;
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
