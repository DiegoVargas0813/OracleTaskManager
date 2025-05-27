
import { useTasksKPI } from './useTasksKPI';
import { useTasksHoursKPI } from './useTasksHoursKPI';
import { useTasksStoryPointsKPI } from './useTasksStoryPointsKPI';
import { useTasksProgressKPI } from './useTasksProgressKPI';
import { useState, useEffect } from 'react';

export const useOverallPerformanceKPI = (userId: number) => {
  const { data: tasksKPI, isLoading: isTasksLoading } = useTasksKPI(userId);
  const { data: hoursKPI, isLoading: isHoursLoading } = useTasksHoursKPI(userId);
  const { data: storyPointsKPI, isLoading: isStoryPointsLoading } = useTasksStoryPointsKPI(userId);
  const { data: progressKPI, isLoading: isProgressLoading } = useTasksProgressKPI(userId);
  
  const [cachedScore, setCachedScore] = useState<number | null>(null);
  
  // Move the calculation to useEffect to avoid setting state during render
  useEffect(() => {
    if (!tasksKPI || !hoursKPI || !storyPointsKPI || !progressKPI) {
      return; // Don't do anything if data is not available
    }

    const taskScore = (tasksKPI.completionRate * 0.3);
    const hoursScore = (hoursKPI.efficiencyRate <= 100 ? hoursKPI.efficiencyRate : 100) * 0.3;
    const storyPointsScore = (storyPointsKPI.completionRate * 0.2);
    const progressScore = (progressKPI.progressRate * 0.2);

    const calculatedScore = Math.round(taskScore + hoursScore + storyPointsScore + progressScore);
    
    // Set the cached score
    setCachedScore(calculatedScore);
  }, [tasksKPI, hoursKPI, storyPointsKPI, progressKPI]);
  
  // Determine if we're still loading
  const isLoading = isTasksLoading || isHoursLoading || isStoryPointsLoading || isProgressLoading;

  return {
    performanceScore: cachedScore,
    isLoading
  };
};
