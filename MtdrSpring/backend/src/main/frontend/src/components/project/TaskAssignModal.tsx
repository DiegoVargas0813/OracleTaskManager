import React from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { useQuery } from '@tanstack/react-query';

interface TaskAssignModalProps {
  open: boolean;
  onClose: () => void;
  projectId: number;
  sprintId: number;
  onAssign: (taskIds: number[]) => void;
}

export const TaskAssignModal: React.FC<TaskAssignModalProps> = ({ open, onClose, projectId, sprintId, onAssign }) => {
  const [selected, setSelected] = React.useState<number[]>([]);

  // Fetch all tasks for all users (user IDs are hardcoded or provided elsewhere)
  const userIds = [1, 2, 3, 4, 5]; // TODO: Replace with dynamic user IDs if needed
  const { data: allTasks, isLoading } = useQuery({
    queryKey: ['all-user-tasks', projectId],
    queryFn: async () => {
      const allTasksArr = await Promise.all(
        userIds.map(async (userId) => {
          const tasksRes = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/${userId}`);
          if (!tasksRes.ok) return [];
          try {
            return await tasksRes.json();
          } catch (e) {
            // If the response is not valid JSON, return an empty array
            return [];
          }
        })
      );
      // Flatten tasks
      return allTasksArr.flat();
    }
  });

  // Only show tasks that are not assigned to any sprint
  const unassignedTasks = allTasks?.filter((task: any) => !task.sprintId);

  const handleToggle = (taskId: number) => {
    setSelected((prev) =>
      prev.includes(taskId) ? prev.filter((id) => id !== taskId) : [...prev, taskId]
    );
  };

  const handleAssign = () => {
    onAssign(selected);
    setSelected([]);
    onClose();
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Select tasks to add to this sprint</DialogTitle>
        </DialogHeader>
        {isLoading ? (
          <div>Loading...</div>
        ) : (
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {unassignedTasks?.length === 0 ? (
              <div className="text-muted-foreground">No available tasks to assign</div>
            ) : (
              unassignedTasks.map((task: any) => (
                <label key={task.id} className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={selected.includes(task.id)}
                    onChange={() => handleToggle(task.id)}
                  />
                  <span>{task.name}</span>
                </label>
              ))
            )}
          </div>
        )}
        <div className="flex justify-end gap-2 mt-4">
          <Button variant="outline" onClick={onClose}>Cancel</Button>
          <Button onClick={handleAssign} disabled={selected.length === 0}>Assign</Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};
