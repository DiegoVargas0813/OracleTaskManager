
import { BarChart } from 'lucide-react';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { useProjects } from '@/hooks/useProjects';
import { useSprints } from '@/hooks/useSprints';
import { useSprintTasks, fetchSprintTasks } from '@/hooks/useSprintTasks';
import { useQueries, useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ResponsiveContainer, BarChart as RechartsBarChart, Bar, XAxis, YAxis, Tooltip, Legend } from 'recharts';
import React, { useMemo, useState } from 'react';
import { useUsers } from '@/hooks/useUsers';
import { useUserSprintTasks } from '@/hooks/useUserSprintTasks';
import ReportePorDesarrollador from './ReportePorDesarrollador';
import { toast } from 'sonner';
import { Task } from '@/types/models';

const Reports = () => {
  const { data: currentUser, isLoading: userLoading } = useCurrentUser();
  const managerId = currentUser?.id || 1;
  const { data: projects, isLoading: projectsLoading } = useProjects(managerId);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);

  // Get sprints for the selected project
  const { data: sprints, isLoading: sprintsLoading } = useSprints(selectedProjectId || (projects?.[0]?.id ?? undefined));

  // Fetch all users
  const { data: users, isLoading: usersLoading } = useUsers();

  // Fetch tasks for each sprint using useQueries (existing chart)
  const sprintTasksResults = useQueries({
    queries: (sprints || []).map(sprint => ({
      queryKey: ['sprint-tasks', sprint.id],
      queryFn: () => fetchSprintTasks(sprint.id),
      staleTime: 10000,
    }))
  });

  // Prepare data for the first chart (total hours per sprint)
  const sprintHoursData = useMemo(() => {
    return (sprints || []).map((sprint, idx) => {
      const result = sprintTasksResults[idx];
      let estimated = 0;
      let real = 0;
      if (result?.isLoading) {
        estimated = 0;
        real = 0;
      } else if (result?.isError || !result?.data) {
        estimated = 0;
        real = 0;
      } else if (Array.isArray(result.data)) {
        estimated = result.data.reduce((sum, t) => sum + (t.estimatedHours || 0), 0);
        real = result.data.reduce((sum, t) => sum + (t.realHours || 0), 0);
      }
      return {
        sprint: sprint.name || `Sprint ${sprint.id}`,
        estimated,
        real
      };
    });
  }, [sprints, sprintTasksResults]);

  // --- Assignment type guards ---
  function isAssignmentArrayOfNumbers(assignments: any[]): assignments is number[] {
    return assignments.length === 0 || typeof assignments[0] === 'number';
  }
  function isAssignmentArrayOfObjects(assignments: any[]): assignments is { user: number }[] {
    return assignments.length > 0 && typeof assignments[0] === 'object' && 'user' in assignments[0];
  }
  function isAssignmentArrayOfUsers(assignments: any[]): assignments is { id: number }[] {
    return assignments.length > 0 && typeof assignments[0] === 'object' && 'id' in assignments[0];
  }

  // Prepare data for the second chart (hours worked by each developer per sprint)
  const sprintUserHoursData = useMemo(() => {
    if (!users || !sprints) return [];
    return sprints.map((sprint, sprintIdx) => {
      const result = sprintTasksResults[sprintIdx];
      const row = { sprint: sprint.name || `Sprint ${sprint.id}` };
      users.forEach(user => {
        let hours = 0;
        if (result && Array.isArray(result.data)) {
          hours = result.data
            .filter(task =>
              Array.isArray(user.assignments) &&
              user.assignments.includes(task.id) &&
              typeof task.status === 'string' &&
              task.status.trim().toLowerCase() === 'complete'
            )
            .reduce((sum, t) => sum + (t.realHours || 0), 0);
        }
        row[user.name.split(' ')[0] || user.name] = hours;
      });
      return row;
    });
  }, [users, sprints, sprintTasksResults]);

  // Prepare data for the completed tasks chart (Graphic 3)
  const completedTasksData = useMemo(() => {
    if (!users || !sprints) return [];
    return sprints.map((sprint, sprintIdx) => {
      const result = sprintTasksResults[sprintIdx];
      const row = { sprint: sprint.name || `Sprint ${sprint.id}` };
      users.forEach(user => {
        let completed = 0;
        if (result && Array.isArray(result.data)) {
          completed = result.data.filter(
            t => Array.isArray(user.assignments) && user.assignments.includes(t.id) && typeof t.status === 'string' && t.status.trim().toLowerCase() === 'complete'
          ).length;
        }
        row[user.name.split(' ')[0] || user.name] = completed;
      });
      return row;
    });
  }, [users, sprints, sprintTasksResults]);

  return (
    <div className="flex h-screen bg-background">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6 bg-slate-50">
          <div className="max-w-6xl mx-auto">
            <div className="mb-8">
              <h1 className="text-3xl font-bold tracking-tight text-gray-900">
                {userLoading ? 'Cargando...' : `Reportes Generales`}
              </h1>
              <p className="text-muted-foreground mt-1">
               
              </p>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Columna 1: Gráficas */}
              <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md mb-8">
                <CardHeader>
                  <CardTitle className="text-lg font-medium flex items-center">
                    <BarChart className="h-5 w-5 mr-2 text-muted-foreground" /> Horas Totales trabajadas por sprint
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-[350px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                      <RechartsBarChart
                        data={sprintHoursData}
                        margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                      >
                        <XAxis dataKey="sprint" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                      <Bar dataKey="estimated" fill="#3B82F6" name="Horas Estimadas" />
                        <Bar dataKey="real" fill="#10B981" name="Horas Reales" />
                      </RechartsBarChart>
                    </ResponsiveContainer>
                  </div>
                </CardContent>
              </Card>
              <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md mb-8">
                <CardHeader>
                  <CardTitle className="text-lg font-medium flex items-center">
                    <BarChart className="h-5 w-5 mr-2 text-muted-foreground" /> Horas trabajadas por desarrollador y sprint
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-[350px] w-full">
                    {usersLoading || sprintsLoading ? (
                      <div className="flex items-center justify-center h-full text-muted-foreground">Cargando datos...</div>
                    ) : users && users.length > 0 && sprints && sprints.length > 0 ? (
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsBarChart
                          data={sprintUserHoursData}
                          margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                          barGap={8}
                        >
                          <XAxis dataKey="sprint" />
                          <YAxis />
                          <Tooltip />
                          <Legend />
                          {users.map((user, idx) => (
                            <Bar
                              key={user.id}
                              dataKey={user.name.split(' ')[0] || user.name}
                              fill={['#6366F1', '#F59E42', '#10B981', '#EF4444', '#FBBF24', '#3B82F6', '#E879F9', '#F472B6'][idx % 8]}
                              name={user.name}
                              isAnimationActive={false}
                            />
                          ))}
                        </RechartsBarChart>
                      </ResponsiveContainer>
                    ) : (
                      <div className="flex items-center justify-center h-full text-muted-foreground">No hay datos suficientes para mostrar el gráfico.</div>
                    )}
                  </div>
                </CardContent>
              </Card>
              <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md mb-8">
                <CardHeader>
                  <CardTitle className="text-lg font-medium flex items-center">
                    <BarChart className="h-5 w-5 mr-2 text-muted-foreground" /> Tareas completadas por desarrollador y sprint
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-[350px] w-full">
                    {usersLoading || sprintsLoading ? (
                      <div className="flex items-center justify-center h-full text-muted-foreground">Cargando datos...</div>
                    ) : users && users.length > 0 && sprints && sprints.length > 0 ? (
                      <ResponsiveContainer width="100%" height="100%">
                        <RechartsBarChart
                          data={completedTasksData}
                          margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                          barGap={8}
                        >
                          <XAxis dataKey="sprint" />
                          <YAxis allowDecimals={false} />
                          <Tooltip />
                          <Legend />
                          {users.map((user, idx) => (
                            <Bar
                              key={user.id}
                              dataKey={user.name.split(' ')[0] || user.name}
                              fill={['#6366F1', '#F59E42', '#10B981', '#EF4444', '#FBBF24', '#3B82F6', '#E879F9', '#F472B6'][idx % 8]}
                              name={user.name}
                              isAnimationActive={false}
                            />
                          ))}
                        </RechartsBarChart>
                      </ResponsiveContainer>
                    ) : (
                      <div className="flex items-center justify-center h-full text-muted-foreground">No hay datos suficientes para mostrar el gráfico.</div>
                    )}
                  </div>
                </CardContent>
              </Card>
            </div>
            {/* Columna 2: Reporte por desarrollador */}
            <div className="mb-8">
              <ReportePorDesarrollador users={users} sprintTasksResults={sprintTasksResults} sprints={sprints} />
            </div>
            <div className="mb-8">
              
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

// --- Inline component for the report ---

const ReportedeTareasTerminadas = () => {
  // 1. Fetch all sprints for projectId=1
  const {
    data: sprints,
    isLoading: sprintsLoading,
    error: sprintsError
  } = useQuery<any[]>(
    {
      queryKey: ['sprints', 1],
      queryFn: async () => {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/sprints?projectId=1`);
        if (!res.ok) throw new Error('No se pudo obtener los sprints');
        return await res.json();
      }
    }
  );

  // 2. Find latest sprint by endDate or createdAt
  const latestSprint = React.useMemo(() => {
    if (!Array.isArray(sprints) || sprints.length === 0) return null;
    return [...sprints].sort((a, b) => {
      // Prefer endDate, fallback to createdAt
      const aDate = new Date(a.endDate || a.createdAt || 0);
      const bDate = new Date(b.endDate || b.createdAt || 0);
      return bDate.getTime() - aDate.getTime();
    })[0];
  }, [sprints]);

  // 3. Fetch tasks for latest sprint
  const {
    data: sprintTasks,
    isLoading: sprintTasksLoading,
    error: sprintTasksError
  } = useQuery<Task[]>(
    {
      queryKey: ['tasks-latest-sprint', latestSprint?.id],
      queryFn: async () => {
        if (!latestSprint?.id) return [];
        const res = await fetch(`${import.meta.env.VITE_API_URL}/tasks/sprint/${latestSprint.id}`);
        if (!res.ok) throw new Error('No se pudo obtener las tareas del sprint más reciente');
        return await res.json();
      },
      enabled: !!latestSprint?.id
    }
  );

  // 4. Fetch users
  const { data: users, isLoading: usersLoading, error: usersError } = useUsers();

  // 5. Filter completed and sort by developer name
  const completedTasks = React.useMemo(() => {
    if (!Array.isArray(sprintTasks)) return [];
    return sprintTasks
      .filter(t => typeof t.status === 'string' && t.status.trim().toLowerCase() === 'complete')
      .sort((a, b) => {
        // Sort by developer name (if found)
        const aUser = users?.find(u => Array.isArray(u.assignments) && u.assignments.includes(a.id));
        const bUser = users?.find(u => Array.isArray(u.assignments) && u.assignments.includes(b.id));
        const aName = aUser?.name || '';
        const bName = bUser?.name || '';
        return aName.localeCompare(bName);
      });
  }, [sprintTasks, users]);

  return (
    <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md">
      <CardHeader>
        <CardTitle className="text-lg font-medium">Reporte de Tareas terminadas (último sprint)</CardTitle>
      </CardHeader>
      <CardContent>
        {sprintsLoading || sprintTasksLoading || usersLoading ? (
          <div className="text-muted-foreground">Cargando reporte...</div>
        ) : sprintsError || sprintTasksError || usersError ? (
          <div className="text-red-500">Error obteniendo datos del reporte</div>
        ) : (
          <table className="min-w-full border text-sm bg-white">
            <thead>
              <tr className="bg-gray-100">
                <th className="p-2 border">Tarea</th>
                <th className="p-2 border">Desarrollador</th>
                <th className="p-2 border">Estado</th>
              </tr>
            </thead>
            <tbody>
              {completedTasks.map(task => {
                const user = users?.find(u => Array.isArray(u.assignments) && u.assignments.includes(task.id));
                return (
                  <tr key={task.id}>
                    <td className="p-2 border">{task.name}</td>
                    <td className="p-2 border">{user ? user.name : 'Sin asignar'}</td>
                    <td className="p-2 border">{task.status}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
        {completedTasks.length === 0 && !(sprintsLoading || sprintTasksLoading || usersLoading) && (
          <div className="text-gray-500 mt-2">No hay tareas completadas en el sprint más reciente.</div>
        )}
      </CardContent>
    </Card>
  );
};

export default Reports;
