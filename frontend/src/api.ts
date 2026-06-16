import type { IssueDetail, IssueSummary, Meta, Page, Project, ProjectMeta } from './types';

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
  ) {
    super(message);
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json' },
    ...init,
  });
  if (!res.ok) {
    let message = `HTTP ${res.status}`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
    } catch {
      /* no JSON body */
    }
    throw new ApiError(res.status, message);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export interface UserInfo {
  username: string;
}

export const login = (username: string, password: string) =>
  request<UserInfo>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });

export const logout = () => request<void>('/api/auth/logout', { method: 'POST' });

export const me = () => request<UserInfo>('/api/auth/me');

export const getProjects = () => request<Project[]>('/api/projects');

export const getProject = (key: string) =>
  request<Project>(`/api/projects/${encodeURIComponent(key)}`);

export interface IssueQuery {
  status?: string;
  type?: string;
  q?: string;
  sort?: string;
  order?: 'asc' | 'desc';
  page?: number;
  size?: number;
}

export const getIssues = (projectKey: string, query: IssueQuery) => {
  const params = new URLSearchParams();
  if (query.status) params.set('status', query.status);
  if (query.type) params.set('type', query.type);
  if (query.q) params.set('q', query.q);
  params.set('sort', query.sort ?? 'updated');
  params.set('order', query.order ?? 'desc');
  params.set('page', String(query.page ?? 0));
  params.set('size', String(query.size ?? 25));
  return request<Page<IssueSummary>>(
    `/api/projects/${encodeURIComponent(projectKey)}/issues?${params}`,
  );
};

export const getIssue = (issueKey: string) =>
  request<IssueDetail>(`/api/issues/${encodeURIComponent(issueKey)}`);

export const getMeta = () => request<Meta>('/api/meta');

export const getProjectMeta = (projectKey: string) =>
  request<ProjectMeta>(`/api/projects/${encodeURIComponent(projectKey)}/meta`);

export const attachmentUrl = (id: number) => `/api/attachments/${id}`;
