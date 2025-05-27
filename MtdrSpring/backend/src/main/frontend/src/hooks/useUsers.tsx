import { useQuery } from '@tanstack/react-query';
import { User } from '@/types/models';
import { toast } from 'sonner';

// Mock data for fallback
const mockUsers: User[] = [
  {
    id: 1,
    name: "Alex Ki",
    role: "Developer",
    email: "alex.kim@company.com",
    creation_ts: "2023-01-15T09:00:00Z"
  },
  {
    id: 2,
    name: "Morgan Smith",
    role: "Designer",
    email: "morgan.smith@company.com",
    creation_ts: "2023-02-20T10:30:00Z"
  },
  {
    id: 3,
    name: "Taylor Jones",
    role: "QA Engineer",
    email: "taylor.jones@company.com",
    creation_ts: "2023-03-10T14:15:00Z"
  },
  {
    id: 4,
    name: "Jamie Lee",
    role: "Product Manager",
    email: "jamie.lee@company.com",
    creation_ts: "2023-01-05T08:45:00Z"
  },
  {
    id: 5,
    name: "Riley Johnson",
    role: "DevOps Engineer",
    email: "riley.johnson@company.com",
    creation_ts: "2023-04-25T11:20:00Z"
  }
];

export const useUsers = () => {
  return useQuery({
    queryKey: ['users'],
    queryFn: async (): Promise<User[]> => {
      try {
        // Try to fetch from API first
        const response = await fetch(`${import.meta.env.VITE_API_URL}/users`);
        
        // If response isn't ok, throw an error to trigger fallback
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        return data as User[];
      } catch (error) {
        console.error('Error fetching users from API:', error);
        toast.error("Could not connect to the API. Using mock data instead.");
        
        // Fall back to mock data
        return mockUsers;
      }
    },
  });
};
