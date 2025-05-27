
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { useTasksStoryPointsKPI } from "@/hooks/useTasksStoryPointsKPI";
import { Gauge } from 'lucide-react';

interface TaskStoryPointsKPIProps {
  userId: number;
}

const TaskStoryPointsKPI = ({ userId }: TaskStoryPointsKPIProps) => {
  const { data, isLoading } = useTasksStoryPointsKPI(userId);

  if (isLoading) {
    return (
      <Card className="overflow-hidden bg-white shadow-sm">
        <CardContent className="h-24 flex items-center justify-center">
          <div className="animate-pulse flex space-x-4 w-full">
            <div className="flex-1 space-y-4 py-1">
              <div className="h-4 bg-slate-200 rounded w-3/4"></div>
              <div className="h-4 bg-slate-200 rounded w-1/2"></div>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md">
      <CardHeader className="pb-2">
        <div className="flex items-center">
          <Gauge className="h-5 w-5 mr-2 text-muted-foreground" />
          <CardTitle className="text-lg font-medium">Story Points</CardTitle>
        </div>
        <p className="text-xs text-muted-foreground">
          Fórmula: (Story Points Completados / Total Story Points) * 100
        </p>
      </CardHeader>
      <CardContent className="pb-6">
        <div className="flex flex-col space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-muted-foreground">
              {data?.completedPoints} de {data?.totalPoints} puntos
            </span>
            <span className="font-medium text-primary">{data?.completionRate}%</span>
          </div>
          <Progress value={data?.completionRate} className="h-2" />
        </div>
      </CardContent>
    </Card>
  );
};

export default TaskStoryPointsKPI;
