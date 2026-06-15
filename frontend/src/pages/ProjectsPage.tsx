import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getProjects } from '../api';
import { UserName } from '../components';
import type { Project } from '../types';

export default function ProjectsPage() {
  const [projects, setProjects] = useState<Project[] | null>(null);
  const [search, setSearch] = useState('');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getProjects()
      .then(setProjects)
      .catch(() => setError('Projekte konnten nicht geladen werden.'));
  }, []);

  if (error) return <div className="error-box">{error}</div>;
  if (!projects) return <div className="page-loading">Lade Projekte…</div>;

  const term = search.trim().toLowerCase();
  const filtered = term
    ? projects.filter((p) =>
        [p.key, p.name, p.description ?? '', p.lead?.displayName ?? '']
          .join(' ')
          .toLowerCase()
          .includes(term),
      )
    : projects;

  return (
    <>
      <h1>Projekte</h1>
      <div className="filter-bar">
        <input
          className="filter-search"
          placeholder="Projekte durchsuchen (Schlüssel, Name, Beschreibung)…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          autoFocus
        />
      </div>
      {filtered.length === 0 ? (
        <div className="empty-state">Keine Projekte gefunden.</div>
      ) : (
        <div className="project-grid">
          {filtered.map((project) => (
          <Link key={project.key} to={`/projects/${project.key}`} className="project-card">
            <div className="project-card-head">
              <span className="project-avatar">{project.key.slice(0, 3)}</span>
              <div>
                <div className="project-name">{project.name}</div>
                <div className="muted">{project.key}</div>
              </div>
            </div>
            {project.description && <p className="project-desc">{project.description}</p>}
              <div className="project-card-foot">
                <span>
                  Leitung: <UserName user={project.lead} />
                </span>
                <span className="muted">{project.issueCount} Vorgänge</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </>
  );
}
