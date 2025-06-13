import React from 'react';
import { BarChart, CheckCircle, PlusCircle, ArrowRight, RefreshCw, CalendarRange, Clock, User } from 'lucide-react';
import { Link } from 'react-router-dom';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { useProjects, useProject } from '@/hooks/useProjects';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { useSprints } from '@/hooks/useSprints';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { toast } from 'sonner';
import TaskCompletionKPI from '@/components/dashboard/TaskCompletionKPI';
import TaskHoursKPI from '@/components/dashboard/TaskHoursKPI';
import TaskProgressKPI from '@/components/dashboard/TaskProgressKPI';
import TaskStoryPointsKPI from '@/components/dashboard/TaskStoryPointsKPI';
import OverallPerformanceKPI from '@/components/dashboard/OverallPerformanceKPI';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
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
  const { data: currentUser, isLoading: userLoading } = useCurrentUser();
  const managerId = currentUser?.id || 1;
  const queryClient = useQueryClient();
  
  const { data: projects, isLoading: projectsLoading } = useProjects(managerId);
  const { data: project, isLoading: projectLoading } = useProject(projects?.[0]?.id || 1);
  const { data: sprints, isLoading: sprintsLoading } = useSprints(projects?.[0]?.id || 1);

  const determineStatus = (): 'planning' | 'in-progress' | 'completed' | 'on-hold' => {
    if (!sprints || sprints.length === 0) return 'planning';
    
    const now = new Date();
    const hasActiveSprints = sprints.some(sprint => {
      const startDate = new Date(sprint.start_date);
      const endDate = new Date(sprint.end_date);
      return startDate <= now && endDate >= now;
    });
    
    const hasCompletedSprints = sprints.some(sprint => {
      const endDate = new Date(sprint.end_date);
      return endDate < now;
    });
    
    if (hasActiveSprints) return 'in-progress';
    if (hasCompletedSprints && !hasActiveSprints) return 'completed';
    return 'in-progress';
  };
  
  const calculateProgress = (): number => {
    let totalTasks = 0;
    let completedTasks = 0;
    
    if (sprints) {
      sprints.forEach(sprint => {
        if (sprint.tasks) {
          totalTasks += sprint.tasks.length;
          completedTasks += sprint.tasks.filter(task => task.status).length;
        }
      });
    }
    
    if (totalTasks === 0) return 0;
    return Math.round(100);
  };
  
  const status = determineStatus();
  const progress = calculateProgress();
  
  const statusColors = {
    'planning': 'bg-blue-100 text-blue-800',
    'in-progress': 'bg-amber-100 text-amber-800',
    'completed': 'bg-green-100 text-green-800',
    'on-hold': 'bg-slate-100 text-slate-800'
  };
  
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Invalid Date';
    return date.toLocaleDateString('en-US', { 
      month: 'long', 
      day: 'numeric', 
      year: 'numeric',
      timeZone: 'UTC'
    });
  };

  const handleCreateTask = () => {
    toast.info("Función de crear tarea próximamente");
  };

  const handleReloadKPIs = () => {
    toast.info("Recargando KPIs...");
    queryClient.invalidateQueries({ queryKey: ['tasks-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-hours-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-storypoints-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['tasks-progress-kpi'] });
    queryClient.invalidateQueries({ queryKey: ['currentUser'] });
    queryClient.invalidateQueries({ queryKey: ['projects'] });
    
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
            </div>

            <div className="mb-6">
              <h2 className="text-xl font-semibold mb-4 text-gray-900">Tu Proyecto</h2>
              {projectsLoading || projectLoading ? (
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
              ) : project ? (
                <Card className="overflow-hidden bg-white shadow-sm hover:shadow-md transition-all duration-200">
                  <CardHeader>
                    <div className="flex justify-between items-start">
                      <div>
                        <CardTitle className="text-xl text-gray-900">{project.name}</CardTitle>
                        <p className="text-sm text-muted-foreground mt-1">{project.description}</p>
                      </div>
                      <Badge className={statusColors[status]}>
                        {status === 'planning' && 'Planificación'}
                        {status === 'in-progress' && 'En Progreso'}
                        {status === 'completed' && 'Completado'}
                        {status === 'on-hold' && 'En Pausa'}
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <div className="flex items-center gap-1">
                          <CalendarRange className="h-4 w-4" />
                          <span>Creado: {formatDate('2025-03-15')}</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <Clock className="h-4 w-4" />
                            <span>Última actualización: {formatDate(project.creationTs)}</span>
                        </div>
                      </div>
                      
                      <div className="space-y-2">
                        <div className="flex justify-between text-sm">
                          <span className="text-muted-foreground">Progreso del Proyecto</span>
                          <span className="font-medium">{progress}%</span>
                        </div>
                        <Progress value={progress} className="h-2" />
                      </div>

                      <div className="flex items-center gap-2">
                        <User className="h-4 w-4 text-muted-foreground" />
                        <span className="text-sm text-muted-foreground">Manager:</span>
                        <div className="flex items-center gap-2">
                          <Avatar className="h-6 w-6">
                            <AvatarFallback>JA</AvatarFallback>
                          </Avatar>
                          <span className="text-sm font-medium">Julieta Arteaga</span>
                        </div>
                      </div>

                      <Button asChild className="w-full justify-between bg-primary hover:bg-primary/90 text-white">
                        <Link to={`/projects/${project.id}`}>
                          Ver Detalles del Proyecto
                          <ArrowRight className="ml-2 h-4 w-4" />
                        </Link>
                      </Button>
                    </div>
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
          </div>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
