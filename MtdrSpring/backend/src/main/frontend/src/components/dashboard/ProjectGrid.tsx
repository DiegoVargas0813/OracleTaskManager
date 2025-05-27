
import React from 'react';
import { useProjects } from '@/hooks/useProjects';
import ProjectCard from './ProjectCard';
import { Skeleton } from '@/components/ui/skeleton';

interface ProjectGridProps {
  managerId: number;
}

const ProjectGrid: React.FC<ProjectGridProps> = ({ managerId }) => {
  const { data: projects, isLoading, error } = useProjects(managerId);

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {[...Array(6)].map((_, index) => (
          <div key={index} className="bg-card rounded-lg border border-border p-5">
            <Skeleton className="h-4 w-24 mb-2" />
            <Skeleton className="h-6 w-3/4 mb-1" />
            <Skeleton className="h-4 w-full mb-4" />
            <Skeleton className="h-2 w-full mb-4" />
            <Skeleton className="h-4 w-32 mb-5" />
            <div className="flex justify-between items-center">
              <div className="flex -space-x-2">
                <Skeleton className="h-8 w-8 rounded-full" />
                <Skeleton className="h-8 w-8 rounded-full" />
                <Skeleton className="h-8 w-8 rounded-full" />
              </div>
              <Skeleton className="h-8 w-24" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-8">
        <p className="text-destructive">Error loading projects. Please try again later.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {projects?.map((project) => (
        <ProjectCard key={project.id} project={project} />
      ))}
    </div>
  );
};

export default ProjectGrid;
