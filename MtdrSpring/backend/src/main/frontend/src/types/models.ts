
// User Types
export interface User {
  id: number;
  name: string;
  role: string;
  email: string;
  creation_ts: string;
  manager?: Manager;
  assignments?: Assignment[];
  issues?: Issue[];
}

export interface Manager {
  id: number;
  name: string;
  role: string;
  email: string;
  creation_ts: string;
  users?: User[];
  projects?: Project[];
}

export interface Project {
  id: number;
  name: string;
  description: string;
  creation_ts: string;
  assignedTo?: Manager;
  sprints?: Sprint[];
}

export interface Sprint {
  id: number;
  name: string;
  start_date: string;
  end_date: string;
  project?: Project;
  project_id?: number;
  tasks?: Task[];
  issues?: Issue[];
}

export interface Task {
  id: number;
  name: string;
  description: string;
  status: boolean | string;
  estimatedHours?: number;
  realHours?: number;
  storyPoints?: number;
  priority?: 'low' | 'medium' | 'high';
  startDate?: string;
  endDate?: string;
  sprint?: Sprint;
  sprintId?: number;
  assignments?: Assignment[];
}

export interface Assignment {
  id: number;
  task?: Task;
  user?: User;
  start_date: string;
  end_date: string;
}

export interface Issue {
  id: number;
  name: string;
  description: string;
  status: boolean;
  creation_ts: string;
  assignedTo?: User;
  sprint?: Sprint;
}
