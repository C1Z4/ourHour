import { useQuery } from '@tanstack/react-query';

import { getInvList } from '@/api/org/orgInvApi';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

// ======== 초대 메일 목록 조회 ========
export const useInvListQuery = (orgId: number, isOpen: boolean) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.INV_LIST, orgId],
    queryFn: () => getInvList({ orgId }),
    enabled: !!orgId && isOpen,
  });
