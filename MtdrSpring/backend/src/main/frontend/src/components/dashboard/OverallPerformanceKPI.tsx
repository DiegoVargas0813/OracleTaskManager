
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { useOverallPerformanceKPI } from "@/hooks/useOverallPerformanceKPI";
import { Gauge } from "lucide-react";

interface OverallPerformanceKPIProps {
  userId: number;
}

const OverallPerformanceKPI = ({ userId }: OverallPerformanceKPIProps) => {
  const { performanceScore, isLoading } = useOverallPerformanceKPI(userId);

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

  const getPerformanceColor = (score: number) => {
    if (score >= 80) return "text-green-600";
    if (score >= 60) return "text-amber-600";
    return "text-rose-600";
  };
  
  // Asegurarse de que siempre hay un valor para mostrar, incluso si performanceScore es null
  const scoreToDisplay = performanceScore ?? 0;

  return (
    <Card className="overflow-hidden bg-white shadow-sm transition-all duration-200 hover:shadow-md">
      <CardHeader className="pb-2">
        <div className="flex items-center">
          <Gauge className="h-5 w-5 mr-2 text-muted-foreground" />
          <CardTitle className="text-lg font-medium">Rendimiento General</CardTitle>
        </div>
        <p className="text-xs text-muted-foreground">
          Fórmula: (KPI Tareas * 0.3) + (KPI Horas * 0.3) + (KPI Story Points * 0.2) + (KPI Progreso * 0.2)
        </p>
      </CardHeader>
      <CardContent className="pb-6">
        <div className="flex flex-col space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-muted-foreground">Rendimiento Global</span>
            <span className={`text-2xl font-bold ${getPerformanceColor(scoreToDisplay)}`}>
              {scoreToDisplay}%
            </span>
          </div>
          <Progress value={scoreToDisplay} className="h-2" />
        </div>
      </CardContent>
    </Card>
  );
};

export default OverallPerformanceKPI;
