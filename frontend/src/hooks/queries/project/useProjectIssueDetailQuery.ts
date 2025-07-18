import { useQuery } from '@tanstack/react-query';

import getProjectIssueDetail from '@/api/project/getProjectIssueDetail';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';

interface UseProjectIssueDetailParams {
  issueId: number;
  enabled?: boolean;
}

const useProjectIssueDetailQuery = ({ issueId, enabled = true }: UseProjectIssueDetailParams) =>
  useQuery({
    queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, issueId],
    queryFn: () => getProjectIssueDetail({ issueId }),
    enabled: enabled && !!issueId,
  });

export default useProjectIssueDetailQuery;
