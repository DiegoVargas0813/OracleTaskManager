import { BarChart, FileText, Loader2, Download } from 'lucide-react';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { useProjects } from '@/hooks/useProjects';
import { useSprints } from '@/hooks/useSprints';
import { useSprintTasks, fetchSprintTasks } from '@/hooks/useSprintTasks';
import { useQueries, useQuery } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ResponsiveContainer, BarChart as RechartsBarChart, Bar, XAxis, YAxis, Tooltip, Legend } from 'recharts';
import React, { useMemo, useState, useRef } from 'react';
import { useUsers } from '@/hooks/useUsers';
import { useUserSprintTasks } from '@/hooks/useUserSprintTasks';
import ReportePorDesarrollador from './ReportePorDesarrollador';
import { toast } from 'sonner';
import { Task } from '@/types/models';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

const Reports = () => {
  const { data: currentUser, isLoading: userLoading } = useCurrentUser();
  const managerId = currentUser?.id || 1;
  const { data: projects, isLoading: projectsLoading } = useProjects(managerId);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(null);
  const [selectedSprintId, setSelectedSprintId] = useState<number | null>(null);
  const [isGeneratingReport, setIsGeneratingReport] = useState(false);
  const [aiReport, setAiReport] = useState<string>('');

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

  const formatSprintLabel = (sprint: any) => {
    const startDate = new Date(sprint.startDate);
    const formattedDate = startDate.toLocaleDateString('es-ES', {
      month: 'short',
      day: 'numeric'
    });
    return `Sprint ${sprint.id} `;
  };

  const generateAIReport = async () => {
    if (!selectedSprintId) {
      toast.error("Por favor selecciona un sprint");
      return;
    }

    setIsGeneratingReport(true);
    try {
      // Obtener los datos del sprint seleccionado
      const sprintTasks = await fetchSprintTasks(selectedSprintId);
      const sprint = sprints?.find(s => s.id === selectedSprintId);
      
      if (!sprint || !sprintTasks) {
        throw new Error('No se encontraron datos del sprint');
      }

      // Preparar el prompt para OpenAI
      const prompt = `Genera un reporte detallado del sprint ${sprint.id} con la siguiente información:
        - Fecha de inicio: ${sprint.start_date}
        - Fecha de fin: ${sprint.end_date}
        - Total de tareas: ${sprintTasks.length}
        - Tareas completadas: ${sprintTasks.filter(t => t.status === 'completed').length}
        - Tareas pendientes: ${sprintTasks.filter(t => t.status === 'pending').length}
        - Tareas en progreso: ${sprintTasks.filter(t => t.status === 'in_progress').length}
        
        Por favor, proporciona un análisis detallado del progreso del sprint, incluyendo:
        1. Resumen general del sprint
        2. Análisis de las tareas completadas
        3. Identificación de posibles bloqueos o retrasos
        4. Recomendaciones para el siguiente sprint
        5. Métricas clave de rendimiento`;

      const response = await fetch('https://api.openai.com/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${import.meta.env.VITE_OPENAI_API_KEY}`
        },
        body: JSON.stringify({
          model: "gpt-3.5-turbo",
          messages: [
            {
              role: "system",
              content: "Eres un asistente experto en análisis de sprints y gestión de proyectos ágiles. Tu tarea es generar reportes detallados y útiles basados en los datos proporcionados."
            },
            {
              role: "user",
              content: prompt
            }
          ],
          temperature: 0.7,
          max_tokens: 1000
        })
      });

      if (!response.ok) {
        throw new Error('Error al generar el reporte con OpenAI');
      }

      const data = await response.json();
      const reportContent = data.choices[0].message.content;

      // Crear un elemento temporal para el reporte
      const reportElement = document.createElement('div');
      reportElement.style.padding = '20px';
      reportElement.style.fontFamily = 'Arial, sans-serif';
      reportElement.style.maxWidth = '800px';
      reportElement.style.margin = '0 auto';
      reportElement.style.backgroundColor = 'white';
      
      // Agregar el contenido del reporte
      reportElement.innerHTML = `
        <h1 style="color: #2563eb; text-align: center; margin-bottom: 20px;">Reporte de Sprint ${sprint.id}</h1>
        <div style="margin-bottom: 20px;">
          <p><strong>Fecha de inicio:</strong> ${sprint.start_date}</p>
          <p><strong>Fecha de fin:</strong> ${sprint.end_date}</p>
          <p><strong>Total de tareas:</strong> ${sprintTasks.length}</p>
          <p><strong>Tareas completadas:</strong> ${sprintTasks.filter(t => t.status === 'completed').length}</p>
          <p><strong>Tareas pendientes:</strong> ${sprintTasks.filter(t => t.status === 'pending').length}</p>
          <p><strong>Tareas en progreso:</strong> ${sprintTasks.filter(t => t.status === 'in_progress').length}</p>
        </div>
        <div style="white-space: pre-wrap;">${reportContent}</div>
      `;

      // Agregar el elemento al DOM temporalmente
      document.body.appendChild(reportElement);

      // Generar el PDF
      const canvas = await html2canvas(reportElement, {
        scale: 2,
        useCORS: true,
        logging: false
      });

      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4');
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = pdf.internal.pageSize.getHeight();
      const imgWidth = canvas.width;
      const imgHeight = canvas.height;
      const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight);
      const imgX = (pdfWidth - imgWidth * ratio) / 2;
      const imgY = 30;

      pdf.addImage(imgData, 'PNG', imgX, imgY, imgWidth * ratio, imgHeight * ratio);
      
      // Descargar el PDF
      pdf.save(`reporte-sprint-${sprint.id}.pdf`);

      // Limpiar el elemento temporal
      document.body.removeChild(reportElement);

      toast.success("Reporte generado y descargado exitosamente");
    } catch (error) {
      console.error('Error:', error);
      toast.error("Error al generar el reporte");
    } finally {
      setIsGeneratingReport(false);
    }
  };

  // Prepare data for the first chart (total hours per sprint)
  const sprintHoursData = useMemo(() => {
    const sortedSprints = [...(sprints || [])].sort((a, b) => {
      const dateA = new Date(a.startDate);
      const dateB = new Date(b.startDate);
      return dateA.getTime() - dateB.getTime();
    });

    return sortedSprints.map((sprint, idx) => {
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
        sprint: formatSprintLabel(sprint),
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
    const sortedSprints = [...sprints].sort((a, b) => {
      const dateA = new Date(a.startDate);
      const dateB = new Date(b.startDate);
      return dateA.getTime() - dateB.getTime();
    });

    return sortedSprints.map((sprint, sprintIdx) => {
      const result = sprintTasksResults[sprintIdx];
      const row = { sprint: formatSprintLabel(sprint) };
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
    const sortedSprints = [...sprints].sort((a, b) => {
      const dateA = new Date(a.startDate);
      const dateB = new Date(b.startDate);
      return dateA.getTime() - dateB.getTime();
    });

    return sortedSprints.map((sprint, sprintIdx) => {
      const result = sprintTasksResults[sprintIdx];
      const row = { sprint: formatSprintLabel(sprint) };
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

            {/* Sección de Generación de Reporte con IA */}
            <Card className="mb-8">
              <CardHeader>
                <CardTitle className="text-lg font-medium flex items-center">
                  <FileText className="h-5 w-5 mr-2 text-muted-foreground" />
                  Generar Reporte con IA
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex gap-4">
                    <Select
                      value={selectedSprintId?.toString()}
                      onValueChange={(value) => setSelectedSprintId(Number(value))}
                    >
                      <SelectTrigger className="w-[200px]">
                        <SelectValue placeholder="Seleccionar Sprint" />
                      </SelectTrigger>
                      <SelectContent>
                        {sprints?.map((sprint) => (
                          <SelectItem key={sprint.id} value={sprint.id.toString()}>
                            {formatSprintLabel(sprint)}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>

                    <Button 
                      onClick={generateAIReport}
                      disabled={isGeneratingReport || !selectedSprintId}
                      className="flex items-center gap-2"
                    >
                      {isGeneratingReport ? (
                        <>
                          <Loader2 className="h-4 w-4 animate-spin" />
                          Generando...
                        </>
                      ) : (
                        <>
                          <Download className="h-4 w-4" />
                          Generar PDF
                        </>
                      )}
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>

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
