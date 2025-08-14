import { useQuery } from '@tanstack/react-query';

import {
  getDepartmentsByOrg,
  getPositionsByOrg,
  getMembersByDepartment,
  getMembersByPosition,
} from '@/api/org/orgStructureApi';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

// 부서 목록 조회
export const useDepartmentsQuery = (orgId: number) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.DEPARTMENT_LIST, orgId],
    queryFn: () => getDepartmentsByOrg(orgId),
    enabled: !!orgId,
  });

// 직책 목록 조회
export const usePositionsQuery = (orgId: number) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.POSITION_LIST, orgId],
    queryFn: () => getPositionsByOrg(orgId),
    enabled: !!orgId,
  });

// 부서별 구성원 조회
export const useDepartmentMembersQuery = (orgId: number, deptId: number, enabled: boolean = true) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.DEPARTMENT_MEMBERS, orgId, deptId],
    queryFn: () => getMembersByDepartment(orgId, deptId),
    enabled: !!orgId && !!deptId && enabled,
  });

// 직책별 구성원 조회
export const usePositionMembersQuery = (
  orgId: number,
  positionId: number,
  enabled: boolean = true,
) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.POSITION_MEMBERS, orgId, positionId],
    queryFn: () => getMembersByPosition(orgId, positionId),
    enabled: !!orgId && !!positionId && enabled,
  });
