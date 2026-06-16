export interface UserRef {
  key: string;
  displayName: string;
  email: string | null;
}

export interface StatusRef {
  id: string;
  name: string;
  category: 'TO_DO' | 'IN_PROGRESS' | 'DONE' | 'UNDEFINED';
}

export interface TypeRef {
  id: string;
  name: string;
}

export interface PriorityRef {
  id: string | null;
  name: string | null;
  color: string | null;
}

export interface Project {
  id: number;
  key: string;
  name: string;
  description: string | null;
  lead: UserRef | null;
  issueCount: number;
}

export interface IssueSummary {
  key: string;
  summary: string;
  type: TypeRef;
  status: StatusRef;
  priority: PriorityRef;
  assignee: UserRef | null;
  created: string;
  updated: string;
  dueDate: string | null;
}

export interface Comment {
  id: number;
  author: UserRef | null;
  body: string;
  created: string;
  updated: string;
}

export interface Transition {
  id: number;
  name: string;
  targetStatus: StatusRef | null;
}

export interface Attachment {
  id: number;
  filename: string;
  mimeType: string;
  size: number;
  author: UserRef | null;
  created: string;
}

export interface CustomField {
  id: number;
  name: string;
  value: string;
  multiline: boolean;
}

export interface IssueDetail extends IssueSummary {
  description: string | null;
  resolution: string | null;
  reporter: UserRef | null;
  labels: string[];
  resolutionDate: string | null;
  comments: Comment[];
  transitions: Transition[];
  parent: IssueSummary | null;
  children: IssueSummary[];
  attachments: Attachment[];
  customFields: CustomField[];
}

export interface Page<T> {
  items: T[];
  page: number;
  size: number;
  total: number;
}

export interface Meta {
  statuses: StatusRef[];
  types: TypeRef[];
  priorities: PriorityRef[];
}

export interface ProjectMeta {
  statuses: StatusRef[];
  types: TypeRef[];
}
