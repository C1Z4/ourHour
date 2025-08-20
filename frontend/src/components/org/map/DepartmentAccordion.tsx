import { Member } from '@/types/memberTypes';

import { Department } from '@/api/org/orgStructureApi';
import { AccordionSection } from '@/components/org/map/AccordionSection';
import { useDepartmentMembersQuery } from '@/hooks/queries/org/useOrgStructureQueries';

interface DepartmentAccordionProps {
  department: Department;
  orgId: number;
  isExpanded: boolean;
  onToggle: () => void;
}

export function DepartmentAccordion({
  department,
  orgId,
  isExpanded,
  onToggle,
}: DepartmentAccordionProps) {
  const { data: departmentMembersResponse, isLoading } = useDepartmentMembersQuery(
    orgId,
    department.deptId,
    isExpanded,
  );
  const departmentMembers = departmentMembersResponse as unknown as Member[];

  return (
    <AccordionSection
      title={department.name}
      memberCount={department.memberCount}
      isExpanded={isExpanded}
      onToggle={onToggle}
      members={departmentMembers}
      isLoading={isLoading}
    />
  );
}
