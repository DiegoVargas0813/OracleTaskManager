
import { useQuery } from '@tanstack/react-query';
import { Task } from '@/types/models';
import { toast } from 'sonner';

interface TaskKPIData {
  completed: number;
  incomplete: number;
  completionRate: number;
}

export const useTasksKPI = (userId: number) => {
  return useQuery({
    queryKey: ['tasks-kpi', userId],
    queryFn: async (): Promise<TaskKPIData> => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/1`);
        
        if (!response.ok) {
          throw new Error('Failed to fetch tasks');
        }
        
        const tasks: Task[] = await response.json();
        
        // Filter completed tasks (status can be "Complete" string or true boolean)
        const completed = tasks.filter(task => 
          typeof task.status === 'string' ? task.status === "Complete" : task.status === true
        ).length;
        
        // Filter incomplete tasks (any status that's not "Complete" or true)
        const incomplete = tasks.filter(task => 
          typeof task.status === 'string' ? task.status !== "Complete" : task.status === false
        ).length;
        
        const completionRate = completed + incomplete > 0 
          ? Math.round((completed / (completed + incomplete)) * 100) 
          : 0;
        
        return {
          completed,
          incomplete,
          completionRate
        };
      } catch (error) {
        console.error('Error fetching task KPI:', error);
        
        // Show a toast notification about the API connection issue
        toast.error('No se pudo conectar al API. Usando datos de ejemplo.', {
          description: 'Comprueba tu conexión a internet o contacta a soporte.'
        });

        // Return a more meaningful mock data
        return {
          completed: 8,
          incomplete: 12,
          completionRate: 40
        };
      }
    },
    retry: 1,
    staleTime: 5000,
  });
};
