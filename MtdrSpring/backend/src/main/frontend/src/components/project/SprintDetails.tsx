import React from 'react';
import { Clock, User, CheckCircle2, AlertCircle } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { AssignToSprintButton } from '@/components/ui/AssignToSprintButton'; // Import AssignToSprintButton
import { Sprint, Task } from '@/types/models';

interface SprintDetailsProps {
  sprint: Sprint;
  onClose: () => void;
}

const SprintDetails: React.FC<SprintDetailsProps> = ({ sprint, onClose }) => {
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

  const calculateSprintProgress = (): number => {
    if (!sprint.tasks || sprint.tasks.length === 0) return 0;
    const completedTasks = sprint.tasks.filter(task => task.status).length;
    return Math.round((completedTasks / sprint.tasks.length) * 100);
  };

  return (
    <div className="fixed inset-0 bg-background/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
      <Card className="w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        <CardContent className="p-6">
          <div className="flex justify-between items-start mb-6">
            <div>
              <h2 className="text-2xl font-medium mb-2">{sprint.name}</h2>
              <div className="flex items-center gap-4 text-sm text-muted-foreground">
                <div className="flex items-center">
                  <Clock className="h-4 w-4 mr-1" />
                  {formatSprintDate(sprint.start_date)} - {formatSprintDate(sprint.end_date)}
                </div>
                <div>
                  <span className="font-medium">{sprint.tasks?.length || 0}</span> tasks
                </div>
              </div>
            </div>
            <button
              onClick={onClose}
              className="text-muted-foreground hover:text-foreground"
            >
              ✕
            </button>
          </div>

          <div className="mb-6">
            <div className="flex justify-between text-sm mb-1">
              <span>Sprint Progress</span>
              <span className="font-medium">{calculateSprintProgress()}%</span>
            </div>
            <div className="h-2 bg-muted rounded-full overflow-hidden">
              <div
                className="h-full bg-primary transition-all"
                style={{ width: `${calculateSprintProgress()}%` }}
              />
            </div>
          </div>

          <div className="space-y-4">
            <h3 className="font-medium text-lg">Tasks</h3>
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
                      {/* Add to Sprint Button */}
                      <AssignToSprintButton taskId={task.id} currentSprintId={sprint.id} />
                    </div>
                  </CardContent>
                </Card>
              ))
            ) : (
              <div className="text-center p-8 text-muted-foreground">
                No tasks assigned to this sprint
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default SprintDetails;
