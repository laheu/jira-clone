import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ApiError, login } from '../api';

export default function LoginPage({ onLogin }: { onLogin: (user: string) => void }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const navigate = useNavigate();

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      const user = await login(username, password);
      onLogin(user.username);
      navigate('/');
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 401
          ? 'Benutzername oder Passwort falsch.'
          : 'Anmeldung fehlgeschlagen. Bitte später erneut versuchen.',
      );
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={submit}>
        <h1>
          <span className="brand-logo">JC</span> Jira Clone
        </h1>
        <p className="muted">Read-only-Zugriff auf die Jira-Datenbank</p>
        <label>
          Benutzername
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            autoFocus
            required
          />
        </label>
        <label>
          Passwort
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>
        {error && <div className="error-box">{error}</div>}
        <button className="btn btn-primary" type="submit" disabled={busy}>
          {busy ? 'Anmelden…' : 'Anmelden'}
        </button>
      </form>
    </div>
  );
}
