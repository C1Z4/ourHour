import { useQuery } from '@tanstack/react-query';

import getProjectIssueList from '@/api/project/getProjectIssueList';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectIssueListParams {
  milestoneId: number;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useProjectIssueListQuery = ({
  milestoneId,
  currentPage = 0,
  size = 10,
  enabled = true,
}: UseProjectIssueListParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, milestoneId, currentPage, size],
    queryFn: () =>
      getProjectIssueList({
        milestoneId,
        currentPage,
        size,
      }),
    enabled: enabled && !!milestoneId,
  });

export default useProjectIssueListQuery;
