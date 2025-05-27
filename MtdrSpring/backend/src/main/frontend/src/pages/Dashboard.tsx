
import React from 'react';
import { BarChart, CheckCircle, PlusCircle, ArrowRight, RefreshCw } from 'lucide-react';
import { Link } from 'react-router-dom';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { useProjects } from '@/hooks/useProjects';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { toast } from 'sonner';
import TaskCompletionKPI from '@/components/dashboard/TaskCompletionKPI';
import TaskHoursKPI from '@/components/dashboard/TaskHoursKPI';
import TaskProgressKPI from '@/components/dashboard/TaskProgressKPI';
import TaskStoryPointsKPI from '@/components/dashboard/TaskStoryPointsKPI';
import OverallPerformanceKPI from '@/components/dashboard/OverallPerformanceKPI';
import { 
  BarChart as RechartsBarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  Cell, 
  Tooltip, 
  ResponsiveContainer 
} from 'recharts';
import { useQueryClient } from '@tanstack/react-query';

const Dashboard = () => {
  // Asegurémonos de que managerId siempre tenga un valor válido
  const { data: currentUser, isLoading: userLoading } = useCurrentUser();
  const managerId = currentUser?.id || 1;
  const queryClient = useQueryClient();
  
  const { data: projects, isLoading: projectsLoading } = useProjects(managerId);
  
  const completedTasks = 11;
  const totalTasks = 20;
  const completionPercentage = Math.round((completedTasks / totalTasks) * 100);
  
  const statusData = [
    { name: 'To Do', value: 5, color: '#3B82F6' },
    { name: 'In Progress', value: 4, color: '#F59E0B' },
    { name: 'Done', value: 8, color: '#10B981' },
    { name: 'Cancelled', value: 3, color: '#EF4444' },
  ];
  
  const priorityData = [
    { name: 'Low', value: 3, color: '#3B82F6' },
    { name: 'Medium', value: 4, color: '#F59E0B' },
    { name: 'High', value: 2, color: '#EF4444' },
  ];

  const handleCreateTask = () => {
    toast.info("Función de crear tarea próximamente");
  };

  const handleReloadKPIs = () => {
    toast.info("Recargando KPIs...");
    
    // Invalidar todas las consultas de KPI para forzar una recarga
    queryClient.invalidateQueries({ queryKey: ['tasks-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-hours-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-storypoints-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-progress-kpi'] });
    
    // Invalidar consultas adicionales para asegurar datos frescos
    queryClient.invalidateQueries({ queryKey: ['currentUser'] });
    queryClient.invalidateQueries({ queryKey: ['projects'] });
    
    // Mostrar confirmación después de un breve retraso
    setTimeout(() => {
      toast.success("KPIs actualizados correctamente");
    }, 1000);
  };

  return (
    <div className="flex h-screen bg-background">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6 bg-slate-50">
          <div className="max-w-7xl mx-auto">
            <div className="flex justify-between items-center mb-8">
              <div>
                <h1 className="text-3xl font-bold tracking-tight text-gray-900">
                  {userLoading ? 'Cargando...' : `Bienvenida, Julieta Arteaga!`}
                </h1>
                <p className="text-muted-foreground mt-1">
                  Da seguimiento a las distintas tareas de tu equipo
                </p>
              </div>
              <Button 
                onClick={handleCreateTask}
                className="flex items-center gap-1 bg-primary hover:bg-primary/90"
              >
                <PlusCircle size={16} />
                <span>Crear Tarea</span>
              </Button>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
              <OverallPerformanceKPI userId={managerId} />
              <TaskCompletionKPI userId={managerId} />
              <TaskHoursKPI userId={managerId} />
              <TaskProgressKPI userId={managerId} />
              <TaskStoryPointsKPI userId={managerId} />
              
              <Card className="overflow-hidden bg-white shadow-sm col-span-1 transition-all duration-200 hover:shadow-md">
                <CardHeader>
                  <CardTitle className="text-lg font-medium">Estado de Tareas</CardTitle>
                </CardHeader>
                <CardContent className="pt-2">
                  <div className="h-[200px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <RechartsBarChart
                        layout="horizontal"
                        data={statusData}
                        margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
                      >
                        <XAxis 
                          dataKey="name" 
                          axisLine={false}
                          tickLine={false}
                          tick={{ fill: '#64748b', fontSize: 12 }}
                        />
                        <YAxis hide />
                        <Tooltip
                          content={({ active, payload }) => {
                            if (active && payload && payload.length) {
                              return (
                                <div className="bg-white p-2 border border-gray-200 shadow-md rounded-md">
                                  <p className="font-medium">{payload[0].payload.name}</p>
                                  <p className="text-sm text-muted-foreground">
                                    {payload[0].value} tareas
                                  </p>
                                </div>
                              );
                            }
                            return null;
                          }}
                        />
                        <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                          {statusData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Bar>
                      </RechartsBarChart>
                    </ResponsiveContainer>
                  </div>
                </CardContent>
              </Card>
              
              <Card className="overflow-hidden bg-white shadow-sm col-span-1 lg:col-span-3 transition-all duration-200 hover:shadow-md">
                <CardHeader className="pb-2">
                  <div className="flex items-center">
                    <BarChart className="h-5 w-5 mr-2 text-muted-foreground" />
                    <CardTitle className="text-lg font-medium">Prioridad de Tareas</CardTitle>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="h-[200px]">
                    <ResponsiveContainer width="100%" height="100%">
                      <RechartsBarChart
                        data={priorityData}
                        margin={{ top: 20, right: 30, left: 30, bottom: 20 }}
                      >
                        <XAxis 
                          dataKey="name" 
                          axisLine={false}
                          tickLine={false}
                          tick={{ fill: '#64748b', fontSize: 12, fontWeight: 500 }}
                        />
                        <YAxis hide />
                        <Tooltip 
                          content={({ active, payload }) => {
                            if (active && payload && payload.length) {
                              return (
                                <div className="bg-white p-2 border border-gray-200 shadow-md rounded-md">
                                  <p className="font-medium">Prioridad: {payload[0].payload.name}</p>
                                  <p className="text-sm text-muted-foreground">
                                    {payload[0].value} tareas
                                  </p>
                                </div>
                              );
                            }
                            return null;
                          }}
                        />
                        <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                          {priorityData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Bar>
                      </RechartsBarChart>
                    </ResponsiveContainer>
                  </div>
                </CardContent>
              </Card>
            </div>
            
            <div className="mb-6">
              <h2 className="text-xl font-semibold mb-4 text-gray-900">Tu Proyecto</h2>
              {projectsLoading ? (
                <Card className="overflow-hidden bg-white shadow-sm">
                  <CardContent className="h-24 flex items-center justify-center">
                    <div className="animate-pulse flex space-x-4 w-full">
                      <div className="flex-1 space-y-4 py-1">
                        <div className="h-4 bg-slate-200 rounded w-3/4"></div>
                        <div className="h-4 bg-slate-200 rounded w-1/2"></div>
                      </div>
                      <div className="rounded-md bg-slate-200 h-10 w-28"></div>
                    </div>
                  </CardContent>
                </Card>
              ) : projects && projects.length > 0 ? (
                <Card className="overflow-hidden bg-white shadow-sm hover:shadow-md transition-all duration-200">
                  <CardHeader>
                    <CardTitle className="text-xl text-gray-900">{projects[0].name}</CardTitle>
                    <p className="text-sm text-muted-foreground">{projects[0].description}</p>
                  </CardHeader>
                  <CardContent className="pt-0">
                    <Button asChild className="w-full justify-between bg-primary hover:bg-primary/90 text-white">
                      <Link to={`/projects/${projects[0].id}`}>
                        Ver Detalles del Proyecto
                        <ArrowRight className="ml-2 h-4 w-4" />
                      </Link>
                    </Button>
                  </CardContent>
                </Card>
              ) : (
                <Card className="overflow-hidden bg-white shadow-sm">
                  <CardContent className="h-24 flex items-center justify-center">
                    <p className="text-muted-foreground">No se encontraron proyectos</p>
                  </CardContent>
                </Card>
              )}
            </div>
            
            <div className="flex justify-center mt-8 mb-4">
              <Button
                onClick={handleReloadKPIs}
                variant="outline"
                className="flex items-center gap-2"
              >
                <RefreshCw className="h-4 w-4" />
                Recargar KPIs
              </Button>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
