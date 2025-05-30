
import { useQuery } from '@tanstack/react-query';
import { Task } from '@/types/models';
import { toast } from 'sonner';

interface TaskStoryPointsKPIData {
  completedPoints: number;
  totalPoints: number;
  completionRate: number;
}

export const useTasksStoryPointsKPI = (userId: number) => {
  return useQuery({
    queryKey: ['tasks-storypoints-kpi', userId],
    queryFn: async (): Promise<TaskStoryPointsKPIData> => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/${userId}`);
        
        if (!response.ok) {
          throw new Error('Failed to fetch tasks');
        }
        
        const tasks: Task[] = await response.json();
        
        // Calculate completed story points
        const completedPoints = tasks
          .filter(task => typeof task.status === 'string' ? task.status === "Complete" : task.status === true)
          .reduce((sum, task) => sum + (task.storyPoints || 0), 0);
        
        // Calculate total story points
        const totalPoints = tasks.reduce((sum, task) => sum + (task.storyPoints || 0), 0);
        
        // Calculate completion rate
        const completionRate = totalPoints > 0 
          ? Math.round((completedPoints / totalPoints) * 100)
          : 0;
        
        return {
          completedPoints,
          totalPoints,
          completionRate
        };
      } catch (error) {
        console.error('Error fetching task story points KPI:', error);
        
        toast.error('No se pudo conectar al API. Usando datos de ejemplo.', {
          description: 'Comprueba tu conexión a internet o contacta a soporte.'
        });

        // Return mock data
        return {
          completedPoints: 13,
          totalPoints: 21,
          completionRate: 62
        };
      }
    },
    retry: 1,
    staleTime: 5000,
  });
};
