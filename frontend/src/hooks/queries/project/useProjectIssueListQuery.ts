import { useQuery } from '@tanstack/react-query';

import getProjectIssueList from '@/api/project/getProjectIssueList';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectIssueListParams {
  projectId: number;
  milestoneId?: number | null;
  currentPage?: number;
  size?: number;
  enabled?: boolean;
}

const useProjectIssueListQuery = ({
  projectId,
  milestoneId = null,
  currentPage = 1,
  size = 10,
  enabled = true,
}: UseProjectIssueListParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, projectId, milestoneId, currentPage, size],
    queryFn: () =>
      getProjectIssueList({
        projectId,
        milestoneId,
        currentPage,
        size,
      }),
    enabled: enabled && milestoneId !== undefined,
  });

export default useProjectIssueListQuery;
