import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ApiError, getIssue } from '../api';
import { PriorityLabel, StatusBadge, UserName, formatDate, formatDateTime } from '../components';
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
              <p className="issue-description">{issue.description}</p>
            ) : (
              <p className="muted">Keine Beschreibung vorhanden.</p>
            )}
          </section>

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
                    <p>{comment.body}</p>
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
          </dl>
        </aside>
      </div>
    </>
  );
}
