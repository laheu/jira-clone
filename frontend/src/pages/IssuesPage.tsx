import { FormEvent, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getIssues, getProject, getProjectMeta } from '../api';
import { PriorityLabel, StatusBadge, UserName, formatDateTime } from '../components';
import type { IssueSummary, Page, Project, ProjectMeta } from '../types';

const PAGE_SIZE = 25;

export default function IssuesPage() {
  const { projectKey = '' } = useParams();
  const [project, setProject] = useState<Project | null>(null);
  const [meta, setMeta] = useState<ProjectMeta | null>(null);
  const [issues, setIssues] = useState<Page<IssueSummary> | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [status, setStatus] = useState('');
  const [type, setType] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState('updated');
  const [order, setOrder] = useState<'asc' | 'desc'>('desc');

  const toggleSort = (field: string) => {
    if (sort === field) {
      setOrder(order === 'asc' ? 'desc' : 'asc');
    } else {
      setSort(field);
      setOrder('asc');
    }
    setPage(0);
  };

  const SortHeader = ({ field, label }: { field: string; label: string }) => (
    <th>
      <button className="th-sort" onClick={() => toggleSort(field)}>
        {label}
        {sort === field && <span className="sort-arrow">{order === 'asc' ? '▲' : '▼'}</span>}
      </button>
    </th>
  );

  useEffect(() => {
    getProject(projectKey).then(setProject).catch(() => setProject(null));
    // only statuses/types that actually occur in this project
    getProjectMeta(projectKey).then(setMeta).catch(() => setMeta(null));
  }, [projectKey]);

  useEffect(() => {
    setIssues(null);
    getIssues(projectKey, { status, type, q: query, sort, order, page, size: PAGE_SIZE })
      .then(setIssues)
      .catch(() => setError('Vorgänge konnten nicht geladen werden.'));
  }, [projectKey, status, type, query, sort, order, page]);

  const submitSearch = (e: FormEvent) => {
    e.preventDefault();
    setPage(0);
    setQuery(searchInput.trim());
  };

  const totalPages = issues ? Math.max(1, Math.ceil(issues.total / issues.size)) : 1;

  if (error) return <div className="error-box">{error}</div>;

  return (
    <>
      <nav className="breadcrumb">
        <Link to="/">Projekte</Link> / <span>{project?.name ?? projectKey}</span>
      </nav>
      <h1>{project ? `${project.name} (${project.key})` : projectKey}</h1>

      <form className="filter-bar" onSubmit={submitSearch}>
        <input
          className="filter-search"
          placeholder="Zusammenfassung durchsuchen…"
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
        />
        <select
          value={status}
          onChange={(e) => {
            setStatus(e.target.value);
            setPage(0);
          }}
        >
          <option value="">Alle Status</option>
          {meta?.statuses.map((s) => (
            <option key={s.id} value={s.id}>
              {s.name}
            </option>
          ))}
        </select>
        <select
          value={type}
          onChange={(e) => {
            setType(e.target.value);
            setPage(0);
          }}
        >
          <option value="">Alle Typen</option>
          {meta?.types.map((t) => (
            <option key={t.id} value={t.id}>
              {t.name}
            </option>
          ))}
        </select>
        <button className="btn" type="submit">
          Suchen
        </button>
      </form>

      {!issues ? (
        <div className="page-loading">Lade Vorgänge…</div>
      ) : issues.items.length === 0 ? (
        <div className="empty-state">Keine Vorgänge gefunden.</div>
      ) : (
        <table className="issue-table">
          <thead>
            <tr>
              <SortHeader field="key" label="Schlüssel" />
              <SortHeader field="type" label="Typ" />
              <SortHeader field="summary" label="Zusammenfassung" />
              <SortHeader field="status" label="Status" />
              <SortHeader field="priority" label="Priorität" />
              <SortHeader field="assignee" label="Bearbeiter" />
              <SortHeader field="updated" label="Aktualisiert" />
            </tr>
          </thead>
          <tbody>
            {issues.items.map((issue) => (
              <tr key={issue.key}>
                <td>
                  <Link className="issue-key" to={`/issues/${issue.key}`}>
                    {issue.key}
                  </Link>
                </td>
                <td>{issue.type.name}</td>
                <td>
                  <Link className="issue-summary" to={`/issues/${issue.key}`}>
                    {issue.summary}
                  </Link>
                </td>
                <td>
                  <StatusBadge status={issue.status} />
                </td>
                <td>
                  <PriorityLabel priority={issue.priority} />
                </td>
                <td>
                  <UserName user={issue.assignee} />
                </td>
                <td className="muted">{formatDateTime(issue.updated)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {issues && issues.total > PAGE_SIZE && (
        <div className="pagination">
          <button className="btn" disabled={page === 0} onClick={() => setPage(page - 1)}>
            Zurück
          </button>
          <span>
            Seite {page + 1} von {totalPages} · {issues.total} Vorgänge
          </span>
          <button
            className="btn"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage(page + 1)}
          >
            Weiter
          </button>
        </div>
      )}
    </>
  );
}
