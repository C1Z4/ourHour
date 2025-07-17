import { useQuery } from '@tanstack/react-query';

import getOrgMemberList from '@/api/org/getOrgMemberList';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

interface UseOrgMemberListParams {
  orgId: number;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useOrgMemberListQuery = ({
  orgId,
  currentPage = 1,
  size = 10,
  enabled = true,
}: UseOrgMemberListParams) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.MEMBER_LIST, orgId, currentPage, size],
    queryFn: () =>
      getOrgMemberList({
        orgId,
        currentPage,
        size,
      }),
    enabled: enabled && !!orgId,
  });

export default useOrgMemberListQuery;
