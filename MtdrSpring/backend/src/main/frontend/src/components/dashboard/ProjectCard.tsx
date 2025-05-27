
import React from 'react';
import { Link } from 'react-router-dom';
import { Calendar, ArrowRight } from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Project } from '@/types/models';
import { cn } from '@/lib/utils';

interface ProjectCardProps {
  project: Project;
}

const ProjectCard: React.FC<ProjectCardProps> = ({ project }) => {
  const { id, name, description, creation_ts, assignedTo } = project;
  
  // Determine project status based on sprints if available
  const determineStatus = (): 'planning' | 'in-progress' | 'completed' | 'on-hold' => {
    if (!project.sprints || project.sprints.length === 0) return 'planning';
    
    const now = new Date();
    const hasActiveSprints = project.sprints.some(sprint => {
      const startDate = new Date(sprint.start_date);
      const endDate = new Date(sprint.end_date);
      return startDate <= now && endDate >= now;
    });
    
    const hasCompletedSprints = project.sprints.some(sprint => {
      const endDate = new Date(sprint.end_date);
      return endDate < now;
    });
    
    if (hasActiveSprints) return 'in-progress';
    if (hasCompletedSprints && !hasActiveSprints) return 'completed';
    return 'planning';
  };
  
  // Calculate progress based on tasks if available
  const calculateProgress = (): number => {
    let totalTasks = 0;
    let completedTasks = 0;
    
    if (project.sprints) {
      project.sprints.forEach(sprint => {
        if (sprint.tasks) {
          totalTasks += sprint.tasks.length;
          completedTasks += sprint.tasks.filter(task => task.status).length;
        }
      });
    }
    
    if (totalTasks === 0) return 0;
    return Math.round((completedTasks / totalTasks) * 100);
  };
  
  const status = determineStatus();
  const progress = calculateProgress();
  
  const statusColors = {
    'planning': 'bg-blue-100 text-blue-800',
    'in-progress': 'bg-amber-100 text-amber-800',
    'completed': 'bg-green-100 text-green-800',
    'on-hold': 'bg-slate-100 text-slate-800'
  };
  
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };
  
  // Get team members info from the project
  const getTeamMembers = () => {
    const teamMembers = [];
    
    // Add the manager
    if (assignedTo) {
      teamMembers.push({
        id: assignedTo.id,
        name: assignedTo.name,
        role: 'Manager'
      });
    }
    
    // This is a placeholder as we don't have direct team info in the project model
    // In a real implementation, you'd fetch this from the API or related entities
    return teamMembers;
  };
  
  const team = getTeamMembers();
  
  return (
    <div className="bg-card rounded-lg border border-border p-5 card-hover">
      <div className="flex justify-between items-start mb-3">
        <div>
          <Badge className={cn(statusColors[status], "mb-2")}>
            {status.replace('-', ' ')}
          </Badge>
          <h3 className="font-medium text-lg mb-1">{name}</h3>
          <p className="text-muted-foreground text-sm line-clamp-2 mb-4">{description}</p>
        </div>
      </div>
      
      <div className="mb-4">
        <div className="flex justify-between text-sm mb-1">
          <span>Progress</span>
          <span className="font-medium">{progress}%</span>
        </div>
        <div className="h-2 w-full bg-muted rounded-full overflow-hidden">
          <div 
            className="h-full bg-primary rounded-full" 
            style={{ width: `${progress}%` }}
          ></div>
        </div>
      </div>
      
      <div className="flex items-center text-sm text-muted-foreground mb-5">
        <Calendar className="h-4 w-4 mr-1" />
        <span className="mr-4">Created: {formatDate(creation_ts)}</span>
      </div>
      
      <div className="flex justify-between items-center">
        <div className="flex -space-x-2">
          {team.slice(0, 3).map((member) => (
            <Avatar key={member.id} className="border-2 border-card h-8 w-8">
              <AvatarFallback className="text-xs">
                {member.name.split(' ').map(n => n[0]).join('')}
              </AvatarFallback>
            </Avatar>
          ))}
        </div>
        
        <Button asChild variant="ghost" size="sm">
          <Link to={`/projects/${id}`} className="flex items-center group">
            View Details
            <ArrowRight className="ml-1 h-4 w-4 transition-transform duration-200 group-hover:translate-x-1" />
          </Link>
        </Button>
      </div>
    </div>
  );
};

export default ProjectCard;
