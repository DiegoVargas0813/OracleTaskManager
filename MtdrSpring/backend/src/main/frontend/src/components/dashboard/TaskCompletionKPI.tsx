
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useTasksKPI } from '@/hooks/useTasksKPI';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';

interface TaskCompletionKPIProps {
  userId: number;
}

const TaskCompletionKPI: React.FC<TaskCompletionKPIProps> = ({ userId }) => {
  const { data: kpiData, isLoading } = useTasksKPI(userId);

  if (isLoading || !kpiData) {
    return (
      <Card className="col-span-1 lg:col-span-2">
        <CardHeader>
          <CardTitle>Progreso de Tareas</CardTitle>
        </CardHeader>
        <CardContent className="h-[300px] flex items-center justify-center">
          <p className="text-muted-foreground">Cargando datos...</p>
        </CardContent>
      </Card>
    );
  }

  const chartData = [
    { name: 'Completadas', value: kpiData.completed, color: '#10B981' },
    { name: 'Pendientes', value: kpiData.incomplete, color: '#F59E0B' }
  ];

  return (
    <Card className="col-span-1 lg:col-span-2">
      <CardHeader>
        <CardTitle className="flex flex-col">
          <span>Progreso de Tareas</span>
          <span className="text-sm font-normal text-muted-foreground mt-1">
            Tasa de completitud: {kpiData.completionRate}%
          </span>
        </CardTitle>
        <p className="text-xs text-muted-foreground">
          Fórmula: (Tareas Completadas / Total de Tareas) * 100
        </p>
      </CardHeader>
      <CardContent>
        <div className="h-[300px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={chartData}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={100}
                paddingAngle={5}
                dataKey="value"
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip
                content={({ active, payload }) => {
                  if (active && payload && payload.length) {
                    return (
                      <div className="bg-white p-2 border border-gray-200 shadow-md rounded-md">
                        <p className="font-medium">{payload[0].name}</p>
                        <p className="text-sm text-muted-foreground">
                          {payload[0].value} tareas
                        </p>
                      </div>
                    );
                  }
                  return null;
                }}
              />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
};

export default TaskCompletionKPI;
