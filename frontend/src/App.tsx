import { useEffect, useState } from 'react';
import { Link, Navigate, Outlet, Route, Routes, useNavigate } from 'react-router-dom';
import { logout, me } from './api';
import LoginPage from './pages/LoginPage';
import ProjectsPage from './pages/ProjectsPage';
import IssuesPage from './pages/IssuesPage';
import IssueDetailPage from './pages/IssueDetailPage';

interface LayoutProps {
  user: string;
  onLogout: () => void;
}

function Layout({ user, onLogout }: LayoutProps) {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await logout();
    } finally {
      onLogout();
      navigate('/login');
    }
  };

  return (
    <div className="app">
      <header className="topbar">
        <Link to="/" className="brand">
          <span className="brand-logo">JC</span> Jira Clone
        </Link>
        <nav>
          <Link to="/">Projekte</Link>
        </nav>
        <div className="topbar-right">
          <span className="topbar-user">{user}</span>
          <button className="btn btn-subtle" onClick={handleLogout}>
            Abmelden
          </button>
        </div>
      </header>
      <main className="content">
        <Outlet />
      </main>
      <footer className="footer">Read-only-Ansicht der Jira-Datenbank · keine Änderungen möglich</footer>
    </div>
  );
}

export default function App() {
  const [user, setUser] = useState<string | null>(null);
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    me()
      .then((u) => setUser(u.username))
      .catch(() => setUser(null))
      .finally(() => setChecking(false));
  }, []);

  if (checking) {
    return <div className="page-loading">Lade…</div>;
  }

  return (
    <Routes>
      <Route path="/login" element={<LoginPage onLogin={setUser} />} />
      {user ? (
        <Route element={<Layout user={user} onLogout={() => setUser(null)} />}>
          <Route path="/" element={<ProjectsPage />} />
          <Route path="/projects/:projectKey" element={<IssuesPage />} />
          <Route path="/issues/:issueKey" element={<IssueDetailPage />} />
        </Route>
      ) : (
        <Route path="*" element={<Navigate to="/login" replace />} />
      )}
    </Routes>
  );
}
