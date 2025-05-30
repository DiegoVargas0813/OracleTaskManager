
import React, { useState } from 'react';
import { CheckCircle2, Clock, AlertCircle, User } from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { toast } from 'sonner';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Skeleton } from '@/components/ui/skeleton';
import { useProjectTasks, EnrichedTask } from '@/hooks/useTasks';
import { cn } from '@/lib/utils';

interface TaskListProps {
  projectId: number;
}

const TaskList: React.FC<TaskListProps> = ({ projectId }) => {
  const { data: tasksByUser, isLoading, error } = useProjectTasks(projectId);
  
  if (isLoading) {
    return (
      <div className="space-y-4">
        {[...Array(3)].map((_, index) => (
          <Card key={index}>
            <CardContent className="p-0">
              <div className="p-4 border-b">
                <div className="flex items-center gap-3">
                  <Skeleton className="h-10 w-10 rounded-full" />
                  <div>
                    <Skeleton className="h-5 w-32 mb-1" />
                    <Skeleton className="h-4 w-24" />
                  </div>
                </div>
              </div>
              <div className="p-4">
                <Skeleton className="h-16 w-full" />
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-8">
        <p className="text-destructive">Error loading tasks. Please try again later.</p>
      </div>
    );
  }

  // Convert tasksByUser object to array for rendering
  const userEntries = Object.entries(tasksByUser || {}).map(([userId, tasks]) => ({
    userId: parseInt(userId),
    name: tasks.length > 0 && tasks[0].assignee ? tasks[0].assignee.name : `User ${userId}`,
    tasks,
  }));

  return (
    <div className="space-y-4">
      {userEntries.map(({ userId, name, tasks }) => (
        <UserTaskAccordion 
          key={userId}
          userId={userId}
          name={name}
          tasks={tasks}
        />
      ))}
    </div>
  );
};

interface UserTaskAccordionProps {
  userId: number;
  name: string;
  tasks: EnrichedTask[];
}

const UserTaskAccordion: React.FC<UserTaskAccordionProps> = ({ userId, name, tasks }) => {
  const { data: currentUser } = useCurrentUser();
  const [selectedTask, setSelectedTask] = useState<EnrichedTask | null>(null);
  const [actualHours, setActualHours] = useState<string>('');
  const isCurrentUserDeveloper = currentUser?.role === 'Developer';
  
  // Count tasks by status
  const todoCount = tasks.filter(t => !t.status).length;
  const doneCount = tasks.filter(t => t.status).length;
  
  // Get completion percentage
  const completionPercentage = Math.round((doneCount / tasks.length) * 100);
  
  const statusColors = {
    true: 'bg-green-100 text-green-800',
    false: 'bg-slate-100 text-slate-800'
  };
  
  const priorityColors = {
    'low': 'bg-slate-100 text-slate-800',
    'medium': 'bg-amber-100 text-amber-800',
    'high': 'bg-rose-100 text-rose-800'
  };
  
  const formatDate = (dateString?: string) => {
    if (!dateString) return 'No date';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  const handleTaskComplete = (task: EnrichedTask) => {
    setSelectedTask(task);
  };

  const handleSubmitActualHours = () => {
    if (!actualHours || isNaN(Number(actualHours))) {
      toast.error('Please enter a valid number of hours');
      return;
    }

    // Here you would typically update the task in your backend
    // For now, we'll just show a success message
    toast.success('Task marked as complete');
    setSelectedTask(null);
    setActualHours('');
  };

  return (
    <Card className="overflow-hidden">
      <Table>
        <TableHeader className="bg-muted/30">
          <TableRow>
            <TableHead className="w-[35%]">Task</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Priority</TableHead>
            {isCurrentUserDeveloper && (
              <>
                <TableHead>Est. Hours</TableHead>
                <TableHead>Actual Hours</TableHead>
              </>
            )}
            <TableHead>Due Date</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {tasks.map((task) => (
            <TableRow key={task.id} className="group hover:bg-muted/30">
              <TableCell className="font-medium py-3">
                <div>
                  <div>{task.name}</div>
                  <div className="text-sm text-muted-foreground mt-0.5">{task.description}</div>
                </div>
              </TableCell>
              <TableCell>
                {isCurrentUserDeveloper && !task.status ? (
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => handleTaskComplete(task)}
                  >
                    Mark Complete
                  </Button>
                ) : (
                  <Badge className={cn(statusColors[String(task.status) as keyof typeof statusColors])}>
                    {task.status ? 'Done' : 'Todo'}
                  </Badge>
                )}
              </TableCell>
              <TableCell>
                {task.priority && (
                  <Badge className={cn(priorityColors[task.priority as keyof typeof priorityColors])}>
                    {task.priority}
                  </Badge>
                )}
              </TableCell>
              {isCurrentUserDeveloper && (
                <>
                  <TableCell>{task.estimatedHours || 'N/A'}</TableCell>
                  <TableCell>{task.actualHours || 'N/A'}</TableCell>
                </>
              )}
              <TableCell>
                <div className="flex items-center">
                  <Clock className="h-4 w-4 mr-1 text-muted-foreground" />
                  {formatDate(task.dueDate || task.end_date)}
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog open={!!selectedTask} onOpenChange={() => setSelectedTask(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Complete Task</DialogTitle>
            <DialogDescription>
              How many hours did you spend on this task?
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="actual-hours" className="text-right">
                Actual Hours
              </Label>
              <Input
                id="actual-hours"
                type="number"
                className="col-span-3"
                value={actualHours}
                onChange={(e) => setActualHours(e.target.value)}
                placeholder="Enter actual hours spent"
              />
            </div>
          </div>
          <DialogFooter>
            <Button onClick={handleSubmitActualHours}>Submit</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </Card>
  );
};

export default TaskList;
