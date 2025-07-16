import { useQuery } from '@tanstack/react-query';

import getProjectSummaryList from '@/api/project/getProjectSummaryList';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectSummaryListParams {
  orgId: string;
  participantLimit?: number;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useProjectSummaryListQuery = ({
  orgId,
  participantLimit = 5,
  currentPage = 1,
  size = 10,
  enabled = true,
}: UseProjectSummaryListParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId, participantLimit, currentPage, size],
    queryFn: () =>
      getProjectSummaryList({
        orgId,
        participantLimit,
        currentPage,
        size,
      }),
    enabled: enabled && !!orgId,
  });

export default useProjectSummaryListQuery;
