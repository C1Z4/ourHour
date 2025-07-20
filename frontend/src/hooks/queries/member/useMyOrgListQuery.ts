import { useQuery } from '@tanstack/react-query';

import getMyOrgList from '@/api/org/getMyOrgList';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

interface UseMyOrgListParams {
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useMyOrgListQuery = ({ currentPage = 1, size = 10, enabled = true }: UseMyOrgListParams) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST, currentPage, size],
    queryFn: () =>
      getMyOrgList({
        currentPage,
        size,
      }),
    enabled: enabled,
  });

export default useMyOrgListQuery;
