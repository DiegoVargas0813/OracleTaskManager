import { useQuery } from '@tanstack/react-query';
import { Task, Assignment, User } from '@/types/models';
import { toast } from 'sonner';

// Enriched task type for UI purposes
export interface EnrichedTask extends Task {
  assignee?: User;
  start_date?: string;
  end_date?: string;
  dueDate?: string; // For compatibility with existing UI
  priority?: 'low' | 'medium' | 'high'; // For compatibility with existing UI
  actualHours?: number;
}

// Mock data for fallback
const generateMockTasks = (userId: number, projectId?: number): EnrichedTask[] => {
  const userNames = {
    1: "Alex Kim",
    2: "Morgan Smith",
    3: "Taylor Jones",
    4: "Jamie Lee",
    5: "Riley Johnson"
  };
  
  const taskStatuses = [true, false];
  const priorities = ['low', 'medium', 'high'] as const;
  
  // Generate 3-7 tasks per user
  const numberOfTasks = Math.floor(Math.random() * 5) + 3;
  const tasks: EnrichedTask[] = [];
  
  for (let i = 0; i < numberOfTasks; i++) {
    const taskId = (userId * 100) + i;
    
    tasks.push({
      id: taskId,
      name: `Task ${taskId} for ${userNames[userId as keyof typeof userNames]}`,
      description: `This is a detailed description for task ${taskId}.`,
      status: taskStatuses[Math.floor(Math.random() * taskStatuses.length)],
      assignee: {
        id: userId,
        name: userNames[userId as keyof typeof userNames],
        role: "Developer",
        email: `${userNames[userId as keyof typeof userNames].toLowerCase().replace(' ', '.')}@company.com`,
        creation_ts: "2023-01-01T00:00:00Z"
      },
      dueDate: getRandomFutureDate(), // For UI compatibility
      priority: priorities[Math.floor(Math.random() * priorities.length)] // For UI compatibility
    });
  }
  
  return tasks;
};

// Helper function to generate random future dates
function getRandomFutureDate(): string {
  const today = new Date();
  const futureDate = new Date(today);
  futureDate.setDate(today.getDate() + Math.floor(Math.random() * 30) + 1);
  return futureDate.toISOString();
}

export const useUserTasks = (userId: number, projectId?: number) => {
  return useQuery({
    queryKey: ['tasks', userId, projectId],
    queryFn: async (): Promise<EnrichedTask[]> => {
      try {
        // Try to fetch from API first
        const url = projectId 
          ? `${import.meta.env.VITE_API_URL}/tasks/user/${userId}?projectId=${projectId}`
          : `${import.meta.env.VITE_API_URL}/tasks/user/${userId}`;
        
        const response = await fetch(url);
        
        // If response isn't ok, throw an error to trigger fallback
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        // Process API data to match our UI expectations
        const data = await response.json();
        
        // Enrich tasks with assignee info from assignments
        const enrichedTasks: EnrichedTask[] = data.map((task: Task) => {
          const assignment = task.assignments && task.assignments.length > 0 
            ? task.assignments[0] 
            : undefined;
            
          return {
            ...task,
            assignee: assignment?.user,
            start_date: assignment?.start_date,
            end_date: assignment?.end_date,
            dueDate: assignment?.end_date, // For UI compatibility
            priority: determinePriority(assignment?.start_date, assignment?.end_date), // For UI compatibility
            actualHours: task.actualHours
          };
        });
        
        return enrichedTasks;
      } catch (error) {
        console.error(`Error fetching tasks for user ${userId}:`, error);
        toast.error("Could not connect to the API. Using mock data instead.");
        
        // Fall back to mock data
        return generateMockTasks(userId, projectId);
      }
    },
  });
};

export const useProjectTasks = (projectId: number) => {
  return useQuery({
    queryKey: ['project-tasks', projectId],
    queryFn: async (): Promise<{ [userId: number]: EnrichedTask[] }> => {
      try {
        // Try to fetch from API first
        const response = await fetch(`${import.meta.env.VITE_API_URL}/tasks/project/${projectId}`);
        
        // If response isn't ok, throw an error to trigger fallback
        if (!response.ok) {
          throw new Error(`API returned ${response.status}`);
        }
        
        const data = await response.json();
        
        // Group tasks by user
        const tasksByUser: { [userId: number]: EnrichedTask[] } = {};
        
        // Process API data
        data.forEach((task: Task) => {
          if (task.assignments && task.assignments.length > 0) {
            task.assignments.forEach(assignment => {
              if (assignment.user) {
                const userId = assignment.user.id;
                
                if (!tasksByUser[userId]) {
                  tasksByUser[userId] = [];
                }
                
                const enrichedTask: EnrichedTask = {
                  ...task,
                  assignee: assignment.user,
                  start_date: assignment.start_date,
                  end_date: assignment.end_date,
                  dueDate: assignment.end_date, // For UI compatibility
                  priority: determinePriority(assignment.start_date, assignment.end_date) // For UI compatibility
                };
                
                tasksByUser[userId].push(enrichedTask);
              }
            });
          }
        });
        
        return tasksByUser;
      } catch (error) {
        console.error(`Error fetching tasks for project ${projectId}:`, error);
        toast.error("Could not connect to the API. Using mock data instead.");
        
        // Fall back to mock data for multiple users
        const userIds = [1, 2, 3, 4, 5];
        let allTasks: { [userId: number]: EnrichedTask[] } = {};
        
        for (const userId of userIds) {
          allTasks[userId] = generateMockTasks(userId, projectId);
        }
        
        return allTasks;
      }
    },
  });
};

// Helper function to determine priority based on dates
function determinePriority(startDate?: string, endDate?: string): 'low' | 'medium' | 'high' {
  if (!endDate) return 'medium';
  
  const now = new Date();
  const due = new Date(endDate);
  const daysLeft = Math.floor((due.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
  
  if (daysLeft < 3) return 'high';
  if (daysLeft < 7) return 'medium';
  return 'low';
}
