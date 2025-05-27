
import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { useUserTasks } from '@/hooks/useTasks';
import { Button } from '@/components/ui/button';
import { CheckCircle2, AlertCircle, BarChart, Clock, Timer } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { toast } from 'sonner';
import { useTasksStoryPointsKPI } from '@/hooks/useTasksStoryPointsKPI';

const DeveloperDashboard = () => {
  const { data: currentUser, isLoading: userLoading } = useCurrentUser();
  const { data: tasks, isLoading: tasksLoading } = useUserTasks(currentUser?.id || 1);
  const { data: storyPointsKPI } = useTasksStoryPointsKPI(currentUser?.id || 1);

  // Helper function to check if a task is complete
  const isTaskComplete = (status: boolean | string): boolean => {
    return typeof status === 'string' ? status === "Complete" : status === true;
  };

  const totalTasks = tasks?.length || 0;
  const completedTasks = tasks?.filter(task => isTaskComplete(task.status)).length || 0;
  const inProgressTasks = tasks?.filter(task => !isTaskComplete(task.status)).length || 0;
  const completionRate = totalTasks ? Math.round((completedTasks / totalTasks) * 100) : 0;

  const priorityStats = {
    high: tasks?.filter(task => task.priority === 'high').length || 0,
    medium: tasks?.filter(task => task.priority === 'medium').length || 0,
    low: tasks?.filter(task => task.priority === 'low').length || 0,
  };

  const [actualHours, setActualHours] = useState<{ [key: number]: number }>({});

  const handleMarkAsComplete = (taskId: number) => {
    const actualHoursInput = prompt('¿Cuántas horas reales te tomó completar esta tarea?');
    if (actualHoursInput) {
      const hours = parseFloat(actualHoursInput);
      if (!isNaN(hours) && hours >= 0) {
        setActualHours(prev => ({ ...prev, [taskId]: hours }));
        toast.success('Tarea marcada como completada');
      } else {
        toast.error('Por favor ingresa un número válido de horas');
      }
    }
  };

  return (
    <div className="flex h-screen bg-background">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6 bg-slate-50">
          <div className="max-w-7xl mx-auto">
            <div className="mb-8">
              <h1 className="text-3xl font-bold tracking-tight text-gray-900">
                {userLoading ? 'Cargando...' : `Bienvenido, ${currentUser?.name.split(' ')[0]}!`}
              </h1>
              <p className="text-muted-foreground mt-1">
                Panel de Control del Desarrollador
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Total de Tareas</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{totalTasks}</div>
                  <Progress value={completionRate} className="mt-2" />
                  <p className="text-sm text-muted-foreground mt-1">{completionRate}% completado</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">En Progreso</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-amber-600">{inProgressTasks}</div>
                  <div className="flex items-center text-sm text-muted-foreground mt-2">
                    <Clock className="w-4 h-4 mr-1" />
                    <span>Tareas activas</span>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Completadas</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-green-600">{completedTasks}</div>
                  <div className="flex items-center text-sm text-muted-foreground mt-2">
                    <CheckCircle2 className="w-4 h-4 mr-1" />
                    <span>Tareas finalizadas</span>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Alta Prioridad</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-rose-600">{priorityStats.high}</div>
                  <div className="flex items-center text-sm text-muted-foreground mt-2">
                    <AlertCircle className="w-4 h-4 mr-1" />
                    <span>Requieren atención</span>
                  </div>
                </CardContent>
              </Card>
            </div>

            <Tabs defaultValue="tasks" className="space-y-6">
              <TabsList>
                <TabsTrigger value="tasks">Mis Tareas</TabsTrigger>
                <TabsTrigger value="analytics">Análisis</TabsTrigger>
              </TabsList>

              <TabsContent value="tasks">
                <div className="space-y-4">
                  {tasks?.map((task) => (
                    <Card key={task.id} className="overflow-hidden">
                      <CardContent className="p-6">
                        <div className="flex items-start justify-between">
                          <div className="space-y-1">
                            <h3 className="font-medium">{task.name}</h3>
                            <p className="text-sm text-muted-foreground">{task.description}</p>
                            <div className="flex items-center gap-4 text-sm mt-2">
                              <div className="flex items-center">
                                <Clock className="w-4 h-4 mr-1 text-muted-foreground" />
                                <span>Estimado: {task.estimatedHours || 0}h</span>
                              </div>
                              {actualHours[task.id] && (
                                <div className="flex items-center">
                                  <Timer className="w-4 h-4 mr-1 text-muted-foreground" />
                                  <span>Real: {actualHours[task.id]}h</span>
                                </div>
                              )}
                            </div>
                          </div>
                          <Button
                            variant={isTaskComplete(task.status) ? "ghost" : "outline"}
                            size="sm"
                            onClick={() => handleMarkAsComplete(task.id)}
                            className={`${isTaskComplete(task.status) ? 'text-green-600' : ''}`}
                            disabled={isTaskComplete(task.status)}
                          >
                            <CheckCircle2 className="w-4 h-4 mr-2" />
                            {isTaskComplete(task.status) ? 'Completada' : 'Marcar como completada'}
                          </Button>
                        </div>
                        <div className="mt-4 flex items-center justify-between text-sm">
                          <div className="flex items-center gap-4">
                            <span className={`px-2 py-1 rounded-full text-xs font-medium
                              ${task.priority === 'high' ? 'bg-rose-100 text-rose-800' :
                                task.priority === 'medium' ? 'bg-amber-100 text-amber-800' :
                                  'bg-blue-100 text-blue-800'}`}
                            >
                              {task.priority}
                            </span>
                            {task.dueDate && (
                              <span className="text-muted-foreground">
                                Vence: {new Date(task.dueDate).toLocaleDateString()}
                              </span>
                            )}
                          </div>
                          {!isTaskComplete(task.status) && (
                            <Progress 
                              value={65} 
                              className="w-24 h-2" 
                              title="Progreso estimado"
                            />
                          )}
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </TabsContent>

              <TabsContent value="analytics">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <Card>
                    <CardHeader>
                      <CardTitle className="flex items-center gap-2">
                        <BarChart className="w-5 h-5" />
                        Distribución por Prioridad
                      </CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        <div>
                          <div className="flex justify-between text-sm mb-1">
                            <span className="text-rose-600">Alta</span>
                            <span>{priorityStats.high} tareas</span>
                          </div>
                          <Progress value={(priorityStats.high / totalTasks) * 100} className="h-2 bg-rose-100" />
                        </div>
                        <div>
                          <div className="flex justify-between text-sm mb-1">
                            <span className="text-amber-600">Media</span>
                            <span>{priorityStats.medium} tareas</span>
                          </div>
                          <Progress value={(priorityStats.medium / totalTasks) * 100} className="h-2 bg-amber-100" />
                        </div>
                        <div>
                          <div className="flex justify-between text-sm mb-1">
                            <span className="text-blue-600">Baja</span>
                            <span>{priorityStats.low} tareas</span>
                          </div>
                          <Progress value={(priorityStats.low / totalTasks) * 100} className="h-2 bg-blue-100" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>

                  <Card>
                    <CardHeader>
                      <CardTitle>Productividad Semanal</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="h-[200px] flex items-end justify-between gap-2">
                        {['L', 'M', 'X', 'J', 'V', 'S', 'D'].map((day, i) => (
                          <div key={day} className="flex flex-col items-center gap-2">
                            <div 
                              className="w-8 bg-primary/10 rounded-t"
                              style={{ 
                                height: `${Math.random() * 150 + 20}px`,
                                backgroundColor: i === 4 ? 'rgb(var(--primary))' : ''
                              }}
                            />
                            <span className="text-sm text-muted-foreground">{day}</span>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
            </Tabs>
          </div>
        </main>
      </div>
    </div>
  );
};

export default DeveloperDashboard;
