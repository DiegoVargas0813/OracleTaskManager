import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { ChevronRight, ChevronDown } from 'lucide-react';
import { Task } from '@/types/models';

interface User {
  id: number;
  name: string;
  assignments?: number[];
}

interface Sprint {
  id: number;
  name: string;
}

interface Props {
  users?: User[];
  sprintTasksResults: any[];
  sprints?: Sprint[];
}

const ReportePorDesarrollador: React.FC<Props> = ({ users, sprintTasksResults, sprints }) => {
  const [expanded, setExpanded] = useState<{ [userId: number]: boolean }>({});

  if (!users || !sprints) return null;

  // Todas las tareas por usuario (de todos los sprints)
  const userTasks: { [userId: number]: Task[] } = {};
  users.forEach(user => {
    userTasks[user.id] = [];
    sprints.forEach((sprint, idx) => {
      const result = sprintTasksResults[idx];
      if (result && Array.isArray(result.data)) {
        const tasks = result.data.filter((t: Task) => Array.isArray(user.assignments) && user.assignments.includes(t.id));
        userTasks[user.id].push(...tasks);
      }
    });
  });

  // Expandir/colapsar todos
  const expandAll = () => {
    const all: { [userId: number]: boolean } = {};
    users.forEach(u => { all[u.id] = true; });
    setExpanded(all);
  };
  const collapseAll = () => setExpanded({});

  return (
    <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md">
      <CardHeader>
        <CardTitle className="text-lg font-medium flex items-center justify-between">
          Reporte de Tareas terminadas (último sprint)
          <span>
            <button className="text-xs px-2 py-1 bg-slate-100 rounded mr-2" onClick={expandAll}>Expandir todo</button>
            <button className="text-xs px-2 py-1 bg-slate-100 rounded" onClick={collapseAll}>Colapsar todo</button>
          </span>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="divide-y">
          {users.map(user => (
            <div key={user.id}>
              <div
                className="flex items-center cursor-pointer py-2 select-none"
                onClick={() => setExpanded(prev => ({ ...prev, [user.id]: !prev[user.id] }))}
              >
                {expanded[user.id] ? (
                  <ChevronDown className="w-4 h-4 mr-2 transition-transform" />
                ) : (
                  <ChevronRight className="w-4 h-4 mr-2 transition-transform" />
                )}
                <span className="font-semibold text-slate-700">{user.name}</span>
                <span className="ml-2 text-xs text-slate-500">({userTasks[user.id].length} tareas)</span>
              </div>
              {expanded[user.id] && (
                <div className="pl-8 pb-2">
                  {userTasks[user.id].length === 0 ? (
                    <div className="text-slate-400 text-xs">Sin tareas asignadas</div>
                  ) : (
                    <ul className="list-disc text-xs text-slate-700">
                      {userTasks[user.id].map(task => (
                        <li key={task.id} className="mb-1">
                          <span className="font-medium">{task.name}</span> {' '}
                          <span className="text-slate-500">({task.realHours || 0}h)</span>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};

export default ReportePorDesarrollador;
