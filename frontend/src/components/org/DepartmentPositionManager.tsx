import { Plus, X } from 'lucide-react';

import { ButtonComponent } from '@/components/common/ButtonComponent';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

interface Department {
  departmentId: number;
  name: string;
}

interface Position {
  positionId: number;
  name: string;
}

interface DepartmentPositionManagerProps {
  departments: Department[];
  positions: Position[];
  onDepartmentsChange: (departments: Department[]) => void;
  onPositionsChange: (positions: Position[]) => void;
}

export function DepartmentPositionManager({
  departments,
  positions,
  onDepartmentsChange,
  onPositionsChange,
}: DepartmentPositionManagerProps) {
  const addDepartment = () => {
    const newId = departments.length + 1;
    onDepartmentsChange([...departments, { departmentId: newId, name: '' }]);
  };

  const removeDepartment = (id: number) => {
    if (departments.length > 1) {
      onDepartmentsChange(departments.filter((dept) => dept.departmentId !== id));
    }
  };

  const updateDepartment = (id: number, value: string) => {
    onDepartmentsChange(
      departments.map((dept) => (dept.departmentId === id ? { ...dept, name: value } : dept)),
    );
  };

  const addPosition = () => {
    const newId = positions.length + 1;
    onPositionsChange([...positions, { positionId: newId, name: '' }]);
  };

  const removePosition = (id: number) => {
    if (positions.length > 1) {
      onPositionsChange(positions.filter((pos) => pos.positionId !== id));
    }
  };

  const updatePosition = (id: number, value: string) => {
    onPositionsChange(
      positions.map((pos) => (pos.positionId === id ? { ...pos, name: value } : pos)),
    );
  };

  return (
    <div className="space-y-6">
      <div className="space-y-4">
        <Label className="text-sm font-medium">부서</Label>
        {departments.map((dept) => (
          <div key={dept.departmentId} className="flex space-x-2">
            <Input
              id={`department-${dept.departmentId}`}
              placeholder="부서를 입력하세요"
              value={dept.name}
              onChange={(e) => updateDepartment(dept.departmentId, e.target.value)}
            />
            {departments.length > 1 && (
              <ButtonComponent
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => removeDepartment(dept.departmentId)}
                className="px-2"
              >
                <X className="w-4 h-4" />
              </ButtonComponent>
            )}
          </div>
        ))}
        <ButtonComponent
          type="button"
          variant="secondary"
          size="sm"
          onClick={addDepartment}
          className="w-full"
        >
          <Plus className="w-4 h-4 mr-2" />
          부서 추가
        </ButtonComponent>
      </div>

      <div className="space-y-4">
        <Label className="text-sm font-medium">직책</Label>
        {positions.map((pos) => (
          <div key={pos.positionId} className="flex space-x-2">
            <Input
              id={`position-${pos.positionId}`}
              placeholder="직책을 입력하세요"
              value={pos.name}
              onChange={(e) => updatePosition(pos.positionId, e.target.value)}
            />
            {positions.length > 1 && (
              <ButtonComponent
                type="button"
                variant="ghost"
                size="sm"
                onClick={() => removePosition(pos.positionId)}
                className="px-2"
              >
                <X className="w-4 h-4" />
              </ButtonComponent>
            )}
          </div>
        ))}
        <ButtonComponent
          type="button"
          variant="secondary"
          size="sm"
          onClick={addPosition}
          className="w-full"
        >
          <Plus className="w-4 h-4 mr-2" />
          직책 추가
        </ButtonComponent>
      </div>
    </div>
  );
}
