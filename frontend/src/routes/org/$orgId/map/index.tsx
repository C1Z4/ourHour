import { useState } from 'react';

import { createFileRoute } from '@tanstack/react-router';

import { PageResponse } from '@/types/apiTypes';
import { Member } from '@/types/memberTypes';

import { Department, Position } from '@/api/org/orgStructureApi';
import { AllMembersSection } from '@/components/org/map/AllMembersSection';
import { DepartmentsSection } from '@/components/org/map/DepartmentsSection';
import { FilterTabs, FilterType } from '@/components/org/map/FilterTabs';
import { PositionsSection } from '@/components/org/map/PositionsSection';
import { useOrgMemberListQuery } from '@/hooks/queries/org/useOrgQueries';
import { useDepartmentsQuery, usePositionsQuery } from '@/hooks/queries/org/useOrgStructureQueries';

export const Route = createFileRoute('/org/$orgId/map/')({
  component: RouteComponent,
});

function RouteComponent() {
  const { orgId } = Route.useParams();
  const [activeFilter, setActiveFilter] = useState<FilterType>('all');
  const [expandedDepartments, setExpandedDepartments] = useState<Set<number>>(new Set());
  const [expandedPositions, setExpandedPositions] = useState<Set<number>>(new Set());
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  // 전체 구성원 조회 (페이지네이션)
  const { data: memberPageData, isLoading: isLoadingMembers } = useOrgMemberListQuery(
    Number(orgId),
    currentPage,
    pageSize,
  );
  const memberpage = memberPageData as unknown as PageResponse<Member>;
  const members = memberPageData?.data as unknown as Member[];

  // 부서 목록 조회
  const { data: departments, isLoading: isLoadingDepartments } = useDepartmentsQuery(Number(orgId));

  // 직책 목록 조회
  const { data: positions, isLoading: isLoadingPositions } = usePositionsQuery(Number(orgId));

  const handleFilterChange = (filter: FilterType) => {
    setActiveFilter(filter);
    setExpandedDepartments(new Set());
    setExpandedPositions(new Set());
    setCurrentPage(1);
  };

  const toggleDepartment = (deptId: number) => {
    const newExpanded = new Set(expandedDepartments);
    if (newExpanded.has(deptId)) {
      newExpanded.delete(deptId);
    } else {
      newExpanded.add(deptId);
    }
    setExpandedDepartments(newExpanded);
  };

  const togglePosition = (positionId: number) => {
    const newExpanded = new Set(expandedPositions);
    if (newExpanded.has(positionId)) {
      newExpanded.delete(positionId);
    } else {
      newExpanded.add(positionId);
    }
    setExpandedPositions(newExpanded);
  };

  const isLoading = isLoadingMembers || isLoadingDepartments || isLoadingPositions;

  if (isLoading) {
    return <div className="flex justify-center items-center h-64">로딩 중...</div>;
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">조직도</h1>
      </div>

      <FilterTabs activeFilter={activeFilter} onFilterChange={handleFilterChange} />

      <div className="space-y-4">
        {activeFilter === 'all' && (
          <AllMembersSection
            memberPageData={memberpage}
            members={members}
            currentPage={currentPage}
            onPageChange={setCurrentPage}
          />
        )}

        {activeFilter === 'department' && (
          <DepartmentsSection
            departments={departments as unknown as Department[]}
            orgId={Number(orgId)}
            expandedDepartments={expandedDepartments}
            onToggleDepartment={toggleDepartment}
          />
        )}

        {activeFilter === 'position' && (
          <PositionsSection
            positions={positions as unknown as Position[]}
            orgId={Number(orgId)}
            expandedPositions={expandedPositions}
            onTogglePosition={togglePosition}
          />
        )}
      </div>
    </div>
  );
}
