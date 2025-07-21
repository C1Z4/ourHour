import { useQuery } from '@tanstack/react-query';

import getMyMemberInfo from '@/api/member/getMyMemberInfo';
import { MEMBER_QUERY_KEYS } from '@/constants/queryKeys';

interface UseMyMemberInfoParams {
  orgId: number;
  enabled?: boolean;
}

const useMyMemberInfoQuery = ({ orgId, enabled = true }: UseMyMemberInfoParams) =>
  useQuery({
    queryKey: [MEMBER_QUERY_KEYS.MY_MEMBER_INFO, orgId],
    queryFn: () => getMyMemberInfo({ orgId }),
    enabled: enabled && !!orgId,
  });

export default useMyMemberInfoQuery;
