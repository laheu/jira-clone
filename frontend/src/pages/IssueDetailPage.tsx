import { Fragment, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ApiError, attachmentUrl, getIssue } from '../api';
import {
  BodyText,
  PriorityLabel,
  StatusBadge,
  UserName,
  formatBytes,
  formatDate,
  formatDateTime,
} from '../components';
import type { IssueDetail } from '../types';

export default function IssueDetailPage() {
  const { issueKey = '' } = useParams();
  const [issue, setIssue] = useState<IssueDetail | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setIssue(null);
    getIssue(issueKey)
      .then(setIssue)
      .catch((err) =>
        setError(
          err instanceof ApiError && err.status === 404
            ? `Vorgang ${issueKey} wurde nicht gefunden.`
            : 'Vorgang konnte nicht geladen werden.',
        ),
      );
  }, [issueKey]);

  if (error) return <div className="error-box">{error}</div>;
  if (!issue) return <div className="page-loading">Lade Vorgang…</div>;

  const projectKey = issue.key.split('-')[0];

  return (
    <>
      <nav className="breadcrumb">
        <Link to="/">Projekte</Link> / <Link to={`/projects/${projectKey}`}>{projectKey}</Link> /{' '}
        <span>{issue.key}</span>
      </nav>

      {issue.parent && (
        <div className="parent-line">
          {issue.parent.type.name}:{' '}
          <Link to={`/issues/${issue.parent.key}`} className="issue-key">
            {issue.parent.key}
          </Link>{' '}
          {issue.parent.summary}
        </div>
      )}

      <div className="issue-header">
        <h1>
          <span className="issue-key">{issue.key}</span> {issue.summary}
        </h1>
        <StatusBadge status={issue.status} />
      </div>

      <div className="issue-layout">
        <div className="issue-main">
          <section>
            <h2>Beschreibung</h2>
            {issue.description ? (
              <BodyText text={issue.description} attachments={issue.attachments} />
            ) : (
              <p className="muted">Keine Beschreibung vorhanden.</p>
            )}
          </section>

          {issue.children.length > 0 && (
            <section>
              <h2>Untergeordnete Vorgänge ({issue.children.length})</h2>
              <ul className="child-list">
                {issue.children.map((child) => (
                  <li key={child.key} className="child-row">
                    <Link className="issue-key" to={`/issues/${child.key}`}>
                      {child.key}
                    </Link>
                    <span className="child-type muted">{child.type.name}</span>
                    <Link className="issue-summary child-summary" to={`/issues/${child.key}`}>
                      {child.summary}
                    </Link>
                    <StatusBadge status={child.status} />
                  </li>
                ))}
              </ul>
            </section>
          )}

          {issue.attachments.length > 0 && (
            <section>
              <h2>Anhänge ({issue.attachments.length})</h2>
              <div className="attachment-grid">
                {issue.attachments.map((attachment) => (
                  <a
                    key={attachment.id}
                    className="attachment-card"
                    href={attachmentUrl(attachment.id)}
                    target="_blank"
                    rel="noreferrer"
                  >
                    {attachment.mimeType.startsWith('image/') ? (
                      <img
                        className="attachment-thumb"
                        src={attachmentUrl(attachment.id)}
                        alt={attachment.filename}
                      />
                    ) : (
                      <div className="attachment-thumb attachment-file">📄</div>
                    )}
                    <div className="attachment-name">{attachment.filename}</div>
                    <div className="muted small">
                      {formatBytes(attachment.size)} · {formatDateTime(attachment.created)}
                    </div>
                  </a>
                ))}
              </div>
            </section>
          )}

          {issue.transitions.length > 0 && (
            <section>
              <h2>Mögliche Übergänge</h2>
              <p className="muted small">
                Nur Anzeige – Statusänderungen sind in dieser Read-only-Version nicht möglich.
              </p>
              <div className="transition-list">
                {issue.transitions.map((t) => (
                  <span key={t.id} className="transition-chip" title="Nur Anzeige">
                    {t.name}
                    {t.targetStatus && (
                      <>
                        {' '}
                        → <StatusBadge status={t.targetStatus} />
                      </>
                    )}
                  </span>
                ))}
              </div>
            </section>
          )}

          <section>
            <h2>Kommentare ({issue.comments.length})</h2>
            {issue.comments.length === 0 ? (
              <p className="muted">Keine Kommentare vorhanden.</p>
            ) : (
              <ul className="comment-list">
                {issue.comments.map((comment) => (
                  <li key={comment.id} className="comment">
                    <div className="comment-head">
                      <strong>
                        <UserName user={comment.author} />
                      </strong>
                      <span className="muted">{formatDateTime(comment.created)}</span>
                    </div>
                    <BodyText text={comment.body} attachments={issue.attachments} className="comment-body" />
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>

        <aside className="issue-sidebar">
          <dl>
            <dt>Typ</dt>
            <dd>{issue.type.name}</dd>
            <dt>Priorität</dt>
            <dd>
              <PriorityLabel priority={issue.priority} />
            </dd>
            <dt>Bearbeiter</dt>
            <dd>
              <UserName user={issue.assignee} />
            </dd>
            <dt>Autor</dt>
            <dd>
              <UserName user={issue.reporter} />
            </dd>
            <dt>Erstellt</dt>
            <dd>{formatDateTime(issue.created)}</dd>
            <dt>Aktualisiert</dt>
            <dd>{formatDateTime(issue.updated)}</dd>
            <dt>Fällig am</dt>
            <dd>{formatDate(issue.dueDate)}</dd>
            <dt>Lösung</dt>
            <dd>{issue.resolution ?? 'Offen'}</dd>
            {issue.labels.length > 0 && (
              <>
                <dt>Labels</dt>
                <dd className="label-list">
                  {issue.labels.map((label) => (
                    <span key={label} className="label-chip">
                      {label}
                    </span>
                  ))}
                </dd>
              </>
            )}
            {issue.customFields.map((field) => (
              <Fragment key={field.id}>
                <dt>{field.name}</dt>
                <dd>{field.value}</dd>
              </Fragment>
            ))}
          </dl>
        </aside>
      </div>
    </>
  );
}
