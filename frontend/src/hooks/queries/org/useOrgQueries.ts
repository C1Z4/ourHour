import { useQuery } from '@tanstack/react-query';

import { getOrgInfo } from '@/api/org/orgApi';
import { getOrgMemberList } from '@/api/org/orgMemberApi';
import { getMyProjectList } from '@/api/project/projectApi';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 조직 정보 조회 ========
export const useOrgInfoQuery = (orgId: number) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.ORG_INFO, orgId],
    queryFn: () => getOrgInfo({ orgId }),
    enabled: !!orgId,
  });

// ======== 조직 멤버 목록 조회 ========
export const useOrgMemberListQuery = (orgId: number, currentPage: number = 1, size: number = 10) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.MEMBER_LIST, orgId, currentPage, size],
    queryFn: () => getOrgMemberList({ orgId, currentPage, size }),
    enabled: !!orgId,
  });

// ======== 내 프로젝트 목록 조회 ========
export const useMyProjectListQuery = (orgId: number) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.MY_PROJECT_LIST, orgId],
    queryFn: () => getMyProjectList({ orgId }),
    enabled: !!orgId,
  });
