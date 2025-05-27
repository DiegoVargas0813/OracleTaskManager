import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useTasksHoursKPI } from '@/hooks/useTasksHoursKPI';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, ResponsiveContainer, Tooltip, Cell } from 'recharts';
import { Timer } from 'lucide-react';

interface TaskHoursKPIProps {
  userId: number;
}

const TaskHoursKPI: React.FC<TaskHoursKPIProps> = ({ userId }) => {
  const { data: kpiData, isLoading } = useTasksHoursKPI(userId);

  if (isLoading || !kpiData) {
    return (
      <Card className="col-span-1 lg:col-span-2">
        <CardHeader>
          <CardTitle>Eficiencia de Horas</CardTitle>
        </CardHeader>
        <CardContent className="h-[300px] flex items-center justify-center">
          <p className="text-muted-foreground">Cargando datos...</p>
        </CardContent>
      </Card>
    );
  }

  const chartData = [
    { name: 'Estimadas', hours: kpiData.estimatedHours, color: '#3B82F6' },
    { name: 'Reales', hours: kpiData.actualHours, color: '#10B981' }
  ];

  const efficiencyRate = kpiData.efficiencyRate;
  const isEfficient = efficiencyRate <= 100;

  return (
    <Card className="col-span-1 lg:col-span-2">
      <CardHeader>
        <CardTitle className="flex flex-col">
          <div className="flex items-center gap-2">
            <Timer className="h-5 w-5 text-muted-foreground" />
            <span>Eficiencia de Horas</span>
          </div>
          <span className="text-sm font-normal text-muted-foreground mt-1">
            Tasa de eficiencia: {efficiencyRate}%
            <span className={isEfficient ? 'text-green-600' : 'text-rose-600'}>
              {isEfficient ? ' (Dentro del estimado)' : ' (Sobre el estimado)'}
            </span>
          </span>
        </CardTitle>
        <p className="text-xs text-muted-foreground">
          Fórmula: (Horas Reales / Horas Estimadas) * 100
        </p>
      </CardHeader>
      <CardContent>
        <div className="h-[300px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis 
                dataKey="name"
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#64748b', fontSize: 12 }}
              />
              <YAxis 
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#64748b', fontSize: 12 }}
                label={{ value: 'Horas', angle: -90, position: 'insideLeft', fill: '#64748b' }}
              />
              <Tooltip
                content={({ active, payload }) => {
                  if (active && payload && payload.length) {
                    return (
                      <div className="bg-white p-2 border border-gray-200 shadow-md rounded-md">
                        <p className="font-medium">Horas {payload[0].payload.name}</p>
                        <p className="text-sm text-muted-foreground">
                          {payload[0].value} horas
                        </p>
                      </div>
                    );
                  }
                  return null;
                }}
              />
              <Bar dataKey="hours" radius={[4, 4, 0, 0]}>
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
};

export default TaskHoursKPI;
