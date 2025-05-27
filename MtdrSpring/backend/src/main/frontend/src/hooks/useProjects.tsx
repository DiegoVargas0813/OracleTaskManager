import { useQuery } from '@tanstack/react-query';
import { Project } from '@/types/models';
import { toast } from 'sonner';

// Mock data to fallback to if API calls fail
const mockProjects: Project[] = [
  {
    id: 1,
    name: "Website Redesign",
    description: "Redesign the company website with new branding",
    creation_ts: "2023-08-10T10:00:00Z",
    assignedTo: {
      id: 1,
      name: "Alex Manager",
      role: "Senior Manager",
      email: "alex@company.com",
      creation_ts: "2023-01-15T09:00:00Z"
    }
  },
  {
    id: 2,
    name: "Mobile App Development",
    description: "Build a new mobile application for customers",
    creation_ts: "2023-09-05T14:30:00Z",
    assignedTo: {
      id: 1,
      name: "Alex Manager", 
      role: "Senior Manager",
      email: "alex@company.com",
      creation_ts: "2023-01-15T09:00:00Z"
    }
  },
  {
    id: 3,
    name: "CRM Integration",
    description: "Integrate our system with the new CRM solution",
    creation_ts: "2023-07-20T11:15:00Z",
    assignedTo: {
      id: 1,
      name: "Alex Manager",
      role: "Senior Manager",
      email: "alex@company.com",
      creation_ts: "2023-01-15T09:00:00Z"
    }
  },
  {
    id: 4,
    name: "Annual Marketing Strategy",
    description: "Develop the marketing strategy for next year",
    creation_ts: "2023-06-12T16:45:00Z",
    assignedTo: {
      id: 1,
      name: "Alex Manager",
      role: "Senior Manager",
      email: "alex@company.com",
      creation_ts: "2023-01-15T09:00:00Z"
    }
  },
  {
    id: 5,
    name: "Product Launch",
    description: "Launch new product line in international markets",
    creation_ts: "2023-10-01T08:20:00Z",
    assignedTo: {
      id: 1,
      name: "Alex Manager",
      role: "Senior Manager",
      email: "alex@company.com",
      creation_ts: "2023-01-15T09:00:00Z"
    }
  }
];

export const useProjects = (managerId: number) => {
  return useQuery({
    queryKey: ['projects', managerId],
    queryFn: async (): Promise<Project[]> => {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/projects/manager/${managerId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        return data as Project[];
      } catch (error) {
        console.error('Error fetching projects from API:', error);
        //toast.error("Could not connect to the API. Using mock data instead.");
        
        return mockProjects;
      }
    },
  });
};

export const useProject = (projectId: number) => {
  return useQuery({
    queryKey: ['project', projectId],
    queryFn: async (): Promise<Project> => {
      try {
        // Try to fetch from API first
        const response = await fetch(`${import.meta.env.VITE_API_URL}/projects/${projectId}`);
        
        // If response isn't ok, throw an error to trigger fallback
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        return data as Project;
      } catch (error) {
        console.error('Error fetching project from API:', error);
        //toast.error("Could not connect to the API. Using mock data instead.");
        
        // Fall back to mock data
        const project = mockProjects.find(p => p.id === projectId);
        
        if (!project) {
          throw new Error('Project not found');
        }
        
        return project;
      }
    },
  });
};
