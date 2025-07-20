import { useQuery } from '@tanstack/react-query';

import getMyProjectList from '@/api/org/getMyProjectList';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';

interface UseMyProjectListParams {
  orgId: number;
}

const useMyProjectListQuery = ({ orgId }: UseMyProjectListParams) =>
  useQuery({
    queryKey: [ORG_QUERY_KEYS.MY_PROJECT_LIST, orgId],
    queryFn: () =>
      getMyProjectList({
        orgId,
      }),
    enabled: !!orgId,
  });

export default useMyProjectListQuery;
