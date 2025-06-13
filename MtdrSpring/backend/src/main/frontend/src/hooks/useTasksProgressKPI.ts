
import { useQuery } from '@tanstack/react-query';
import { Task } from '@/types/models';
import { toast } from 'sonner';

interface TaskProgressKPI {
  startedTasks: number;
  totalTasks: number;
  progressRate: number;
}

export const useTasksProgressKPI = (userId: number) => {
  return useQuery({
    queryKey: ['tasks-progress-kpi', userId],
    queryFn: async (): Promise<TaskProgressKPI> => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/user/1`);
        
        if (!response.ok) {
          throw new Error('Failed to fetch tasks');
        }
        
        const tasks: Task[] = await response.json();
        
        const totalTasks = tasks.length;
        const startedTasks = tasks.filter(task => 
          typeof task.status === 'string' 
            ? task.status !== 'Not-Started' 
            : task.status !== false
        ).length;
        
        const progressRate = totalTasks > 0 
          ? Math.round((startedTasks / totalTasks) * 100) 
          : 0;
        
        return {
          startedTasks,
          totalTasks,
          progressRate
        };
      } catch (error) {
        console.error('Error fetching task progress KPI:', error);
        toast.error('No se pudo conectar al API. Usando datos de ejemplo.');
        
        // Return mock data
        return {
          startedTasks: 15,
          totalTasks: 20,
          progressRate: 75
        };
      }
    },
  });
};
