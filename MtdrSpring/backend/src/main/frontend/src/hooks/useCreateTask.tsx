import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { Task } from '@/types/models';

interface CreateTaskPayload {
  name: string;
  description: string;
  status: boolean;
  estimatedHours: number;
  sprintId?: number;
  priority: 'low' | 'medium' | 'high';
  startDate: string;
  endDate: string;
}

export const useCreateTask = (userId: number) => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (task: CreateTaskPayload): Promise<void> => {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(task),
      });
      
      if (!response.ok) {
        throw new Error('Failed to create task');
      }
      
      const taskId = response.headers.get('location');
      return Promise.resolve();
    },
    onSuccess: () => {
      // Invalidate and refetch user tasks
      queryClient.invalidateQueries({ queryKey: ['tasks', userId] });
      toast.success('Task created successfully');
    },
    onError: (error) => {
      console.error('Error creating task:', error);
      toast.error('Failed to create task. Please try again.');
    }
  });
};
