
import { useQuery } from '@tanstack/react-query';
import { Task } from '@/types/models';
import { toast } from 'sonner';

interface TaskHoursKPIData {
  estimatedHours: number;
  actualHours: number;
  efficiencyRate: number;
}

export const useTasksHoursKPI = (userId: number) => {
  return useQuery({
    queryKey: ['tasks-hours-kpi', userId],
    queryFn: async (): Promise<TaskHoursKPIData> => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/${userId}`);
        
        if (!response.ok) {
          throw new Error('Failed to fetch tasks');
        }
        
        const tasks: Task[] = await response.json();
        
        // Calculate total estimated and actual hours
        const estimatedHours = tasks.reduce((sum, task) => sum + (task.estimatedHours || 0), 0);
        const actualHours = tasks.reduce((sum, task) => sum + (task.actualHours || 0), 0);
        
        // Calculate efficiency rate (actual / estimated * 100)
        // If estimated hours is 0, set efficiency rate to 100%
        const efficiencyRate = estimatedHours > 0 
          ? Math.round((actualHours / estimatedHours) * 100) 
          : 100;
        
        return {
          estimatedHours,
          actualHours,
          efficiencyRate
        };
      } catch (error) {
        console.error('Error fetching task hours KPI:', error);
        
        // Show a toast notification about the API connection issue
        toast.error('No se pudo conectar al API. Usando datos de ejemplo.', {
          description: 'Comprueba tu conexión a internet o contacta a soporte.'
        });

        // Return mock data
        return {
          estimatedHours: 16,
          actualHours: 19,
          efficiencyRate: 119
        };
      }
    },
    retry: 1,
    staleTime: 5000,
  });
};
