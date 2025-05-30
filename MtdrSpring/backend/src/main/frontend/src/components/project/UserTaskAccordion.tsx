import React, { useState } from 'react';
import { ChevronDown, ChevronRight, CheckCircle2, Circle, Plus } from 'lucide-react';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { User } from '@/types/models';
import { useUserTasks, EnrichedTask } from '@/hooks/useTasks';
import { Skeleton } from '@/components/ui/skeleton';
import TaskFormModal from './TaskFormModal';

interface UserTaskAccordionProps {
  users: User[];
  projectId: number;
}

const UserTaskAccordion: React.FC<UserTaskAccordionProps> = ({ users, projectId }) => {
  const [loadedUsers, setLoadedUsers] = useState<number[]>([]);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  
  const handleUserClick = (userId: number) => {
    if (!loadedUsers.includes(userId)) {
      setLoadedUsers([...loadedUsers, userId]);
    }
  };
  
  const handleAddTask = (userId: number) => {
    setSelectedUserId(userId);
    setIsTaskModalOpen(true);
  };
  
  return (
    <>
      <Accordion type="multiple" className="w-full">
        {users.map((user) => (
          <AccordionItem key={user.id} value={`user-${user.id}`}>
            <AccordionTrigger 
              className="hover:bg-muted px-4 rounded-md" 
              onClick={() => handleUserClick(user.id)}
            >
              <div className="flex items-center gap-3">
                <Avatar className="h-8 w-8">
                  <AvatarFallback className="bg-primary/10 text-primary">
                    {user.name.split(' ').map(n => n[0]).join('')}
                  </AvatarFallback>
                </Avatar>
                <div className="flex flex-col items-start">
                  <span className="font-medium">{user.name}</span>
                  <span className="text-xs text-muted-foreground">{user.role}</span>
                </div>
              </div>
            </AccordionTrigger>
            <AccordionContent className="px-4 pt-2">
              {loadedUsers.includes(user.id) ? (
                <UserTasks 
                  userId={user.id} 
                  onAddTask={() => handleAddTask(user.id)}
                />
              ) : null}
            </AccordionContent>
          </AccordionItem>
        ))}
      </Accordion>
      
      {isTaskModalOpen && (
        <TaskFormModal 
          userId={selectedUserId || undefined}
          projectId={projectId}
          isOpen={isTaskModalOpen}
          onClose={() => setIsTaskModalOpen(false)}
        />
      )}
    </>
  );
};

interface UserTasksProps {
  userId: number;
  onAddTask: () => void;
}

const UserTasks: React.FC<UserTasksProps> = ({ userId, onAddTask }) => {
  const { data: tasks, isLoading, error } = useUserTasks(userId);
  
  if (isLoading) {
    return (
      <div className="space-y-3 py-2">
        {[...Array(3)].map((_, i) => (
          <Skeleton key={i} className="h-20 w-full" />
        ))}
      </div>
    );
  }
  
  return (
    <div>
      <div className="flex justify-between mb-4">
        <h3 className="text-sm font-medium">Tasks</h3>
        <Button variant="outline" size="sm" className="gap-1" onClick={onAddTask}>
          <Plus size={14} />
          <span>Add Task</span>
        </Button>
      </div>
      
      {(!tasks || tasks.length === 0) ? (
        <div className="py-4 text-center text-muted-foreground">
          No tasks assigned to this user
        </div>
      ) : (
        <div className="space-y-3 py-2">
          {tasks.map((task) => (
            <Card key={task.id} className="overflow-hidden">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-3">
                    {task.status ? (
                      <CheckCircle2 className="h-5 w-5 text-green-500 mt-0.5" />
                    ) : (
                      <Circle className="h-5 w-5 text-slate-300 mt-0.5" />
                    )}
                    <div>
                      <h4 className="font-medium">{task.name}</h4>
                      <p className="text-sm text-muted-foreground line-clamp-2">{task.description}</p>
                    </div>
                  </div>
                  
                  {task.priority && (
                    <Badge className={getPriorityColorClass(task.priority)}>
                      {task.priority}
                    </Badge>
                  )}
                </div>
                
                {task.dueDate && (
                  <div className="mt-2 text-xs text-muted-foreground">
                    Due: {new Date(task.dueDate).toLocaleDateString()}
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

function getPriorityColorClass(priority: string): string {
  const priorityColors = {
    low: 'bg-green-100 text-green-800',
    medium: 'bg-amber-100 text-amber-800',
    high: 'bg-red-100 text-red-800'
  };
  
  return priorityColors[priority as keyof typeof priorityColors] || '';
}

export default UserTaskAccordion;
