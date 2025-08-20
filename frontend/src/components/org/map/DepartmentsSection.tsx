import { Department } from '@/api/org/orgStructureApi';

import { DepartmentAccordion } from './DepartmentAccordion';

interface DepartmentsSectionProps {
  departments: Department[];
  orgId: number;
  expandedDepartments: Set<number>;
  onToggleDepartment: (deptId: number) => void;
}

export function DepartmentsSection({
  departments,
  orgId,
  expandedDepartments,
  onToggleDepartment,
}: DepartmentsSectionProps) {
  return (
    <div className="space-y-4">
      {departments?.map((dept: Department) => (
        <DepartmentAccordion
          key={dept.deptId}
          department={dept}
          orgId={orgId}
          isExpanded={expandedDepartments.has(dept.deptId)}
          onToggle={() => onToggleDepartment(dept.deptId)}
        />
      ))}
    </div>
  );
}
