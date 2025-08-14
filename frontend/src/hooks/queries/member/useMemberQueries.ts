import { useQuery } from '@tanstack/react-query';

import { getMyMemberInfo } from '@/api/member/memberApi';
import { getMyOrgList } from '@/api/org/orgApi';
import { MEMBER_QUERY_KEYS, ORG_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 내 멤버 정보 조회 ========
export const useMyMemberInfoQuery = (orgId: number) =>
  useQuery({
    queryKey: [MEMBER_QUERY_KEYS.MY_MEMBER_INFO, orgId],
    queryFn: () => getMyMemberInfo({ orgId }),
    enabled: !!orgId,
  });

// ======== 내 조직 목록 조회 ========
export const useMyOrgListQuery = (currentPage = 1, size = 10) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST, currentPage, size],
    queryFn: () => getMyOrgList({ currentPage, size }),
  });
