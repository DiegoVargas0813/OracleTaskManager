import React, { useState } from 'react';
import { Button } from './button';
import { useAssignTaskToSprint } from '@/hooks/useAssignTaskToSprint';
import { useSprints } from '@/hooks/useSprints';

interface AssignToSprintButtonProps {
  taskId: number;
  currentSprintId: number;
}

export const AssignToSprintButton: React.FC<AssignToSprintButtonProps> = ({ taskId, currentSprintId }) => {
  const { data: sprints } = useSprints();
  const [selectedSprintId, setSelectedSprintId] = useState<number | null>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const assignMutation = useAssignTaskToSprint();

  const handleAssign = () => {
    if (selectedSprintId) {
      assignMutation.mutate({ taskId, sprintId: selectedSprintId });
      setShowDropdown(false);
    }
  };

  return (
    <div style={{ position: 'relative' }}>
      <Button size="sm" variant="secondary" onClick={() => setShowDropdown((v) => !v)}>
        Assign to Sprint
      </Button>
      {showDropdown && (
        <div className="absolute right-0 z-10 bg-white border rounded shadow p-2 mt-2 min-w-[160px]">
          <select
            className="w-full border p-1 rounded mb-2"
            value={selectedSprintId ?? ''}
            onChange={e => setSelectedSprintId(Number(e.target.value))}
          >
            <option value="" disabled>Select sprint</option>
            {sprints && sprints.filter(s => s.id !== currentSprintId).map(sprint => (
              <option value={sprint.id} key={sprint.id}>{sprint.name}</option>
            ))}
          </select>
          <Button size="sm" onClick={handleAssign} disabled={!selectedSprintId || assignMutation.status === 'pending'}>
            {assignMutation.status === 'pending' ? 'Assigning...' : 'Assign'}
          </Button>
        </div>
      )}
    </div>
  );
};
