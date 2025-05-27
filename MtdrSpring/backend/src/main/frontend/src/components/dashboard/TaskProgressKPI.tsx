
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { useTasksProgressKPI } from "@/hooks/useTasksProgressKPI";
import { CircleCheck } from "lucide-react";

interface TaskProgressKPIProps {
  userId: number;
}

const TaskProgressKPI = ({ userId }: TaskProgressKPIProps) => {
  const { data: progressKPI } = useTasksProgressKPI(userId);

  return (
    <Card className="overflow-hidden bg-white shadow-sm">
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-lg">
          <CircleCheck className="h-5 w-5 text-muted-foreground" />
          Progreso del Equipo
        </CardTitle>
        <p className="text-xs text-muted-foreground">
          Fórmula: (Tareas Iniciadas / Total de Tareas) * 100
        </p>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-muted-foreground">
              {progressKPI?.startedTasks} de {progressKPI?.totalTasks} tareas iniciadas
            </span>
            <span className="font-medium text-primary">{progressKPI?.progressRate}%</span>
          </div>
          <Progress value={progressKPI?.progressRate || 0} className="h-2" />
        </div>
      </CardContent>
    </Card>
  );
};

export default TaskProgressKPI;
