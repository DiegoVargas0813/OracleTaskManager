import { useQuery } from '@tanstack/react-query';
import { Task } from '@/types/models';
import { toast } from 'sonner';

export async function fetchUserSprintTasks(userId: number, sprintId: number): Promise<Task[]> {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/${userId}/sprint/${sprintId}`);
    if (!response.ok) {
      throw new Error(`API returned ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error fetching tasks for user ${userId} in sprint ${sprintId}:`, error);
    toast.error('No se pudo conectar al API. Usando datos de ejemplo.');
    // Return mock data
    return [
      { id: 1, name: 'Mock Task 1', estimatedHours: 8, actualHours: 7 },
      { id: 2, name: 'Mock Task 2', estimatedHours: 5, actualHours: 6 },
    ] as Task[];
  }
}

export const useUserSprintTasks = (userId: number, sprintId: number) => {
  return useQuery({
    queryKey: ['user-sprint-tasks', userId, sprintId],
    queryFn: () => fetchUserSprintTasks(userId, sprintId),
    staleTime: 10000,
  });
};
