import { useQuery } from '@tanstack/react-query';

import getOrgInfo from '@/api/org/getOrgInfo';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

interface UseOrgInfoParams {
  orgId: number;
  enabled?: boolean;
}

const useOrgInfoQuery = ({ orgId, enabled = true }: UseOrgInfoParams) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.ORG_INFO, orgId],
    queryFn: () => getOrgInfo({ orgId }),
    enabled: enabled && !!orgId,
  });

export default useOrgInfoQuery;
