import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  CalendarRange, 
  Clock, 
  Edit, 
  MoreHorizontal, 
  Plus, 
  Share, 
  User, 
  Users,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import Header from '@/components/layout/Header';
import Sidebar from '@/components/layout/Sidebar';
import UserTaskAccordion from '@/components/project/UserTaskAccordion';
import SprintDetails from '@/components/project/SprintDetails';
import { useProject } from '@/hooks/useProjects';
import { useUsers } from '@/hooks/useUsers';
import { useSprints } from '@/hooks/useSprints';
import { useProjectTasks } from '@/hooks/useTasks';
import { Sprint } from '@/types/models';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Card, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Skeleton } from '@/components/ui/skeleton';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';
import TaskFormModal from '@/components/project/TaskFormModal';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import { AssignToSprintButton } from '@/components/ui/AssignToSprintButton';
import { TaskAssignModal } from '@/components/project/TaskAssignModal';
import { useAssignTaskToSprint } from '@/hooks/useAssignTaskToSprint';
import { useQueryClient } from '@tanstack/react-query';
import { useQuery } from '@tanstack/react-query';

const ProjectDetails = () => {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate = useNavigate();
  const { data: project, isLoading: projectLoading, error: projectError } = useProject(Number(projectId));
  const { data: users, isLoading: usersLoading, error: usersError } = useUsers();
  const { data: sprints, isLoading: sprintsLoading } = useSprints(Number(projectId));
  const { data: allProjectTasks } = useProjectTasks(Number(projectId));
  
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [selectedSprint, setSelectedSprint] = useState<Sprint | null>(null);
  const [assignModalSprintId, setAssignModalSprintId] = useState<number | null>(null);

  const assign = useAssignTaskToSprint();
  const queryClient = useQueryClient();

  // Get all users
  const { data: usersData } = useUsers();

  // Fetch all active sprints for all users and aggregate their tasks by sprint
  const { data: sprintsByUser, isLoading: loadingSprintsByUser } = useQuery({
    queryKey: ['active-sprints-all-users'],
    enabled: !!usersData && usersData.length > 0,
    queryFn: async () => {
      const results = await Promise.all(
        usersData.map(async (user) => {
          const res = await fetch(`${import.meta.env.VITE_API_URL}/sprints/active/user/${user.id}`);
          if (!res.ok) return [];
          try {
            return await res.json();
          } catch {
            return [];
          }
        })
      );
      // Flatten and group by sprint id
      const sprintMap = new Map();
      results.flat().forEach((sprint) => {
        if (!sprintMap.has(sprint.id)) {
          sprintMap.set(sprint.id, { ...sprint, tasks: new Set(sprint.tasks) });
        } else {
          // Merge tasks if sprint already exists
          const existing = sprintMap.get(sprint.id);
          sprintMap.set(sprint.id, {
            ...existing,
            tasks: new Set([...existing.tasks, ...sprint.tasks])
          });
        }
      });
      // Convert task sets back to arrays
      return Array.from(sprintMap.values()).map(s => ({ ...s, tasks: Array.from(s.tasks) }));
    }
  });

  const handleAddTask = () => {
    setIsTaskModalOpen(true);
  };

  const handleSprintClick = (sprint: Sprint) => {
    setSelectedSprint(sprint);
  };

  const handleCloseSprintDetails = () => {
    setSelectedSprint(null);
  };
  
  const handleAssignTasksToSprint = async (taskIds: number[], sprintId: number) => {
    for (const taskId of taskIds) {
      await assign.mutateAsync({ taskId, sprintId });
    }
    // Refetch sprints and tasks after assignment
    queryClient.invalidateQueries();
  };

  if (projectLoading || usersLoading || sprintsLoading) {
    return (
      <div className="flex h-screen bg-background">
        <Sidebar />
        <div className="flex-1 flex flex-col overflow-hidden">
          <Header />
          <main className="flex-1 overflow-y-auto p-6">
            <div className="max-w-6xl mx-auto">
              <div className="flex items-center mb-6">
                <Button 
                  variant="ghost" 
                  size="sm" 
                  className="gap-1 mr-4"
                  onClick={() => navigate('/')}
                >
                  <ArrowLeft size={16} />
                  <span>Back</span>
                </Button>
                <Skeleton className="h-8 w-64" />
              </div>
              
              <Card className="mb-8">
                <CardContent className="p-6">
                  <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div className="col-span-2">
                      <Skeleton className="h-6 w-1/3 mb-2" />
                      <Skeleton className="h-4 w-full mb-4" />
                      <Skeleton className="h-4 w-full mb-1" />
                      <Skeleton className="h-4 w-2/3 mb-6" />
                      <div className="flex gap-3 mb-4">
                        <Skeleton className="h-8 w-20" />
                        <Skeleton className="h-8 w-20" />
                      </div>
                    </div>
                    <div>
                      <Skeleton className="h-4 w-full mb-2" />
                      <Skeleton className="h-2 w-full mb-6" />
                      <Skeleton className="h-4 w-3/4 mb-4" />
                      <div className="flex gap-2">
                        <Skeleton className="h-8 w-8 rounded-full" />
                        <Skeleton className="h-8 w-8 rounded-full" />
                        <Skeleton className="h-8 w-8 rounded-full" />
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
              
              <Tabs defaultValue="tasks">
                <TabsList className="mb-6">
                  <TabsTrigger value="tasks">Tasks by User</TabsTrigger>
                  <TabsTrigger value="sprints">Sprints</TabsTrigger>
                  <TabsTrigger value="issues">Issues</TabsTrigger>
                </TabsList>
                <TabsContent value="tasks" className="space-y-4">
                  {[...Array(3)].map((_, i) => (
                    <Skeleton key={i} className="h-24 w-full" />
                  ))}
                </TabsContent>
              </Tabs>
            </div>
          </main>
        </div>
      </div>
    );
  }
  
  if (projectError || !project || usersError || !users) {
    return (
      <div className="flex h-screen bg-background">
        <Sidebar />
        <div className="flex-1 flex flex-col overflow-hidden">
          <Header />
          <main className="flex-1 overflow-y-auto p-6">
            <div className="max-w-6xl mx-auto text-center py-12">
              <h2 className="text-2xl font-medium mb-2">Project not found</h2>
              <p className="text-muted-foreground mb-6">
                The project you're looking for doesn't exist or you don't have permission to view it.
              </p>
              <Button onClick={() => navigate('/')}>
                Return to Dashboard
              </Button>
            </div>
          </main>
        </div>
      </div>
    );
  }
  
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
    return 'planning';
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
    return Math.round((completedTasks / totalTasks) * 100);
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
  
  const formatSprintDate = (dateString: string) => {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Invalid Date';
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      timeZone: 'UTC'
    });
  };
  
  return (
    <div className="flex h-screen bg-background">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6">
          <div className="max-w-6xl mx-auto">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center">
                <Button 
                  variant="ghost" 
                  size="sm" 
                  className="gap-1 mr-4"
                  onClick={() => navigate('/')}
                >
                  <ArrowLeft size={16} />
                  <span>Back</span>
                </Button>
                <h1 className="text-2xl font-medium">{project.name}</h1>
              </div>
              
              <div className="flex items-center gap-2">
                <Button variant="outline" size="sm" className="gap-1">
                  <Share size={14} />
                  <span>Share</span>
                </Button>
                
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" size="icon">
                      <MoreHorizontal size={18} />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem>
                      <Edit className="mr-2 h-4 w-4" />
                      <span>Edit Project</span>
                    </DropdownMenuItem>
                    <DropdownMenuItem>
                      <User className="mr-2 h-4 w-4" />
                      <span>Change Owner</span>
                    </DropdownMenuItem>
                    <DropdownMenuItem>
                      <CalendarRange className="mr-2 h-4 w-4" />
                      <span>Add Sprint</span>
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </div>
            
            <Card className="mb-8 overflow-hidden animate-scale-in">
              <CardContent className="p-6">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                  <div className="col-span-2">
                    <div className="flex items-center gap-2 mb-3">
                      <Badge className={cn(statusColors[status as keyof typeof statusColors])}>
                        {status.replace('-', ' ')}
                      </Badge>
                    </div>
                    
                    <p className="text-muted-foreground mb-6">
                      {project.description}
                    </p>
                    
                    <div className="flex items-center gap-4 text-sm text-muted-foreground mb-4">
                      <div className="flex items-center">
                        <Users className="h-4 w-4 mr-1" />
                        <span>Manager: {project.assignedTo?.name || 'Unassigned'}</span>
                      </div>
                      <div className="flex items-center">
                        <Clock className="h-4 w-4 mr-1" />
                        <span>Created: {formatDate(project.creation_ts)}</span>
                      </div>
                    </div>
                    
                    <Button size="sm" className="gap-1" onClick={() => handleAddTask()}>
                      <Plus size={14} />
                      <span>Add Task</span>
                    </Button>
                  </div>
                  
                  <div>
                    <div className="mb-4">
                      <div className="flex justify-between text-sm mb-1">
                        <span>Project Progress</span>
                        <span className="font-medium">{progress}%</span>
                      </div>
                      <Progress value={progress} className="h-2" />
                    </div>
                    
                    <h3 className="text-sm font-medium mb-2">Sprints</h3>
                    <div className="space-y-2">
                      {sprints && sprints.length > 0 ? (
                        [...sprints].sort((a, b) => a.id - b.id).map((sprint) => (
                          <div key={sprint.id} className="bg-muted rounded-md p-2 text-sm">
                            <div className="font-medium">{sprint.name}</div>
                            <div className="text-xs text-muted-foreground">
                              {formatSprintDate(sprint.start_date)} - {formatSprintDate(sprint.end_date)}
                            </div>
                          </div>
                        ))
                      ) : (
                        <div className="text-sm text-muted-foreground">No sprints created yet</div>
                      )}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Tabs defaultValue="tasks" className="animate-fade-in">
              <TabsList className="mb-6">
                <TabsTrigger value="tasks">Tasks by User</TabsTrigger>
                <TabsTrigger value="sprints">Sprints</TabsTrigger>
                <TabsTrigger value="issues">Issues</TabsTrigger>
              </TabsList>
              <TabsContent value="tasks">
                <Card>
                  <CardContent className="p-6">
                    {users && users.length > 0 ? (
                      <UserTaskAccordion users={users} projectId={Number(projectId)} />
                    ) : (
                      <div className="text-center p-8">
                        <p className="text-muted-foreground mb-4">No users found</p>
                        <Button className="gap-1">
                          <Plus size={14} />
                          <span>Add User</span>
                        </Button>
                      </div>
                    )}
                  </CardContent>
                </Card>
              </TabsContent>
              <TabsContent value="sprints">
                <Card>
                  <CardContent className="p-6">
                    {sprints && sprints.length > 0 ? (
                      <Accordion type="single" collapsible className="w-full">
                        {[...sprints].sort((a, b) => a.id - b.id).map((sprint) => (
                          <AccordionItem key={sprint.id} value={`sprint-${sprint.id}`}>
                            <AccordionTrigger className="hover:no-underline">
                              <div className="flex items-center justify-between w-full pr-4">
                                <div className="flex items-center gap-2">
                                  <h3 className="font-medium text-lg">{sprint.name}</h3>
                                  <Badge variant="outline">
                                    {sprint.tasks?.length || 0} tasks
                                  </Badge>
                                </div>
                                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                                  <div className="flex items-center">
                                    <Clock className="h-4 w-4 mr-1" />
                                    {formatSprintDate(sprint.start_date)} - {formatSprintDate(sprint.end_date)}
                                  </div>
                                  <div className="flex items-center">
                                    <Progress 
                                      value={sprint.tasks ? Math.round((sprint.tasks.filter(task => task.status).length / sprint.tasks.length) * 100) : 0} 
                                      className="w-24 h-2" 
                                    />
                                    <span className="ml-2 text-xs">
                                      {sprint.tasks ? Math.round((sprint.tasks.filter(task => task.status).length / sprint.tasks.length) * 100) : 0}%
                                    </span>
                                  </div>
                                </div>
                              </div>
                            </AccordionTrigger>
                            <AccordionContent>
                              <div className="space-y-4 pt-4">
                                {sprint.tasks && sprint.tasks.length > 0 ? (
                                  sprint.tasks.map((task) => (
                                    <Card key={task.id} className="bg-muted/50">
                                      <CardContent className="p-4">
                                        <div className="flex justify-between items-start mb-2">
                                          <div className="flex items-center gap-2">
                                            {task.status ? (
                                              <CheckCircle2 className="h-4 w-4 text-green-500" />
                                            ) : (
                                              <AlertCircle className="h-4 w-4 text-amber-500" />
                                            )}
                                            <h4 className="font-medium">{task.name}</h4>
                                          </div>
                                          <Badge variant="outline">
                                            {task.estimatedHours} hours
                                          </Badge>
                                        </div>
                                        <p className="text-sm text-muted-foreground mb-3">{task.description}</p>
                                        <div className="flex items-center justify-between">
                                          <div className="flex items-center gap-2">
                                            {task.assignments?.map((assignment) => (
                                              <div key={assignment.id} className="flex items-center gap-1">
                                                <Avatar className="h-6 w-6">
                                                  <AvatarFallback>
                                                    {assignment.user?.name?.charAt(0)}
                                                  </AvatarFallback>
                                                </Avatar>
                                                <span className="text-xs">{assignment.user?.name}</span>
                                              </div>
                                            ))}
                                          </div>
                                          <div className="flex items-center gap-2">
                                            <Badge
                                              className={
                                                task.priority === 'high'
                                                  ? 'bg-rose-100 text-rose-800'
                                                  : task.priority === 'medium'
                                                  ? 'bg-amber-100 text-amber-800'
                                                  : 'bg-blue-100 text-blue-800'
                                              }
                                            >
                                              {task.priority}
                                            </Badge>
                                            <AssignToSprintButton taskId={task.id} currentSprintId={sprint.id} />
                                          </div>
                                        </div>
                                      </CardContent>
                                    </Card>
                                  ))
                                ) : (
                                  <div className="text-center p-4">
                                    <Button variant="outline" size="sm" onClick={() => setAssignModalSprintId(sprint.id)}>
                                      Add tasks to this sprint
                                    </Button>
                                  </div>
                                )}
                              </div>
                            </AccordionContent>
                          </AccordionItem>
                        ))}
                      </Accordion>
                    ) : (
                      <div className="text-center p-8">
                        <p className="text-muted-foreground mb-4">No sprints added yet</p>
                        <Button className="gap-1">
                          <Plus size={14} />
                          <span>Add Sprint</span>
                        </Button>
                      </div>
                    )}
                  </CardContent>
                </Card>
              </TabsContent>
              <TabsContent value="issues">
                <Card>
                  <CardContent className="p-6">
                    {project.sprints?.some(sprint => sprint.issues && sprint.issues.length > 0) ? (
                      <div className="space-y-4">
                        {project.sprints
                          .filter(sprint => sprint.issues && sprint.issues.length > 0)
                          .map((sprint) => (
                            <div key={sprint.id}>
                              <h3 className="font-medium mb-3">Sprint: {sprint.name}</h3>
                              {sprint.issues?.map((issue) => (
                                <Card key={issue.id} className="mb-3">
                                  <CardContent className="p-4">
                                    <div className="flex justify-between items-start">
                                      <div>
                                        <h4 className="font-medium">{issue.name}</h4>
                                        <p className="text-sm text-muted-foreground">{issue.description}</p>
                                      </div>
                                      <Badge className={issue.status ? 'bg-green-100 text-green-800' : 'bg-rose-100 text-rose-800'}>
                                        {issue.status ? 'Resolved' : 'Open'}
                                      </Badge>
                                    </div>
                                    <div className="text-xs text-muted-foreground mt-2">
                                      Assigned to: {issue.assignedTo?.name || 'Unassigned'}
                                    </div>
                                  </CardContent>
                                </Card>
                              ))}
                            </div>
                          ))}
                      </div>
                    ) : (
                      <div className="text-center p-8">
                        <p className="text-muted-foreground mb-4">No issues reported</p>
                        <Button className="gap-1">
                          <Plus size={14} />
                          <span>Report Issue</span>
                        </Button>
                      </div>
                    )}
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>
          </div>
          
          {isTaskModalOpen && (
            <TaskFormModal
              projectId={project.id}
              isOpen={isTaskModalOpen}
              onClose={() => setIsTaskModalOpen(false)}
            />
          )}

          {selectedSprint && (
            <SprintDetails
              sprint={selectedSprint}
              onClose={handleCloseSprintDetails}
            />
          )}
          
          {assignModalSprintId && (
            <TaskAssignModal
              open={!!assignModalSprintId}
              onClose={() => setAssignModalSprintId(null)}
              projectId={Number(projectId)}
              sprintId={assignModalSprintId}
              onAssign={async (taskIds) => {
                await handleAssignTasksToSprint(taskIds, assignModalSprintId);
              }}
            />
          )}
          
          {!loadingSprintsByUser && sprintsByUser && (
            <div>
              <h2>Tasks by Sprint</h2>
              {sprintsByUser.map(sprint => (
                <div key={sprint.id} style={{marginBottom: '2rem'}}>
                  <h3>{sprint.name}</h3>
                  <ul>
                    {sprint.tasks.map(taskId => {
                      // Find the task object by ID from allProjectTasks
                      let taskObj = null;
                      if (allProjectTasks) {
                        // allProjectTasks is { [userId]: EnrichedTask[] }
                        for (const userId in allProjectTasks) {
                          const found = allProjectTasks[userId].find(t => t.id === taskId);
                          if (found) { taskObj = found; break; }
                        }
                      }
                      return (
                        <li key={taskId}>
                          {taskObj ? (
                            <>
                              <strong>{taskObj.name}</strong>{' '}
                              <span style={{color: '#888'}}>({taskObj.description})</span>
                            </>
                          ) : (
                            <>Task ID: {taskId}</>
                          )}
                        </li>
                      );
                    })}
                  </ul>
                </div>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default ProjectDetails;
