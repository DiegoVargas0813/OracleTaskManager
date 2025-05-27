import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Sprint } from '@/types/models';
import { toast } from 'sonner';

interface CreateSprintPayload {
  name: string;
  start_date: string;
  end_date: string;
  project_id: number;
}

// Mock data for fallback
const mockSprints: Sprint[] = [
  {
    id: 1,
    name: "Sprint 1 - Initial Setup",
    start_date: "2023-10-01T00:00:00Z",
    end_date: "2023-10-14T23:59:59Z",
  },
  {
    id: 2,
    name: "Sprint 2 - Core Features",
    start_date: "2023-10-15T00:00:00Z",
    end_date: "2023-10-28T23:59:59Z",
  },
  {
    id: 3,
    name: "Sprint 3 - UI Enhancement",
    start_date: "2023-10-29T00:00:00Z",
    end_date: "2023-11-11T23:59:59Z",
  },
  {
    id: 4,
    name: "Sprint 4 - Testing",
    start_date: "2023-11-12T00:00:00Z",
    end_date: "2023-11-25T23:59:59Z",
  }
];

export const useSprints = (projectId?: number) => {
  return useQuery({
    queryKey: ['sprints', projectId],
    queryFn: async (): Promise<Sprint[]> => {
      try {
        const url = projectId 
          ? `${import.meta.env.VITE_API_URL}/sprints?projectId=${projectId}`
          : `${import.meta.env.VITE_API_URL}/sprints`;
          
        const response = await fetch(url);
        
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        return data as Sprint[];
      } catch (error) {
        console.error('Error fetching sprints from API:', error);
        toast.error("Could not connect to the API. Using mock data instead.");
        
        return mockSprints;
      }
    },
  });
};

export const useActiveSprints = (projectId?: number) => {
  return useQuery({
    queryKey: ['active-sprints', projectId],
    queryFn: async (): Promise<Sprint[]> => {
      try {
        const url = projectId 
          ? `${import.meta.env.VITE_API_URL}/api/sprints/active?projectId=${projectId}`
          : `${import.meta.env.VITE_API_URL}/api/sprints/active`;
          
        const response = await fetch(url);
        
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        return data as Sprint[];
      } catch (error) {
        console.error('Error fetching active sprints from API:', error);
        toast.error("Could not connect to the API. Using mock data instead.");
        
        // Filter mock sprints to only include "active" ones
        const now = new Date();
        return mockSprints.filter(sprint => {
          const startDate = new Date(sprint.start_date);
          const endDate = new Date(sprint.end_date);
          return startDate <= now && endDate >= now;
        });
      }
    },
  });
};

export const useSprint = (sprintId: number) => {
  return useQuery({
    queryKey: ['sprint', sprintId],
    queryFn: async (): Promise<Sprint> => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/api/sprints/${sprintId}`);
        
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        return data as Sprint;
      } catch (error) {
        console.error(`Error fetching sprint ${sprintId} from API:`, error);
        toast.error("Could not connect to the API. Using mock data instead.");
        
        const sprint = mockSprints.find(s => s.id === sprintId);
        if (!sprint) {
          throw new Error('Sprint not found');
        }
        
        return sprint;
      }
    },
  });
};

export const useCreateSprint = () => {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (sprint: CreateSprintPayload): Promise<void> => {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/sprints`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(sprint),
      });
      
      if (!response.ok) {
        throw new Error('Failed to create sprint');
      }
      
      const sprintId = response.headers.get('location');
      return Promise.resolve();
    },
    onSuccess: (_, variables) => {
      // Invalidate and refetch sprints for the project
      queryClient.invalidateQueries({ queryKey: ['sprints', variables.project_id] });
      queryClient.invalidateQueries({ queryKey: ['active-sprints', variables.project_id] });
      toast.success('Sprint created successfully');
    },
    onError: (error) => {
      console.error('Error creating sprint:', error);
      toast.error('Failed to create sprint. Please try again.');
    },
  });
};
