import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';

export function useAssignTaskToSprint() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ taskId, sprintId }: { taskId: number; sprintId: number }) => {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/${taskId}/assign-sprint/${sprintId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
      });
      if (!response.ok) {
        throw new Error('Failed to assign task to sprint');
      }
      return response.json();
    },
    onSuccess: () => {
      toast.success('Task assigned to sprint!');
      queryClient.invalidateQueries();
    },
    onError: (error: any) => {
      toast.error(error.message || 'Error assigning task to sprint');
    },
  });
}
