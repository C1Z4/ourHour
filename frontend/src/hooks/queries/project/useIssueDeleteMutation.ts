import { useMutation } from '@tanstack/react-query';

import deleteIssue from '@/api/project/deleteIssue';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';

interface UseIssueDeleteMutationParams {
  projectId: number;
  milestoneId: number | null;
  issueId: number;
}

const useIssueDeleteMutation = ({
  projectId,
  milestoneId,
  issueId,
}: UseIssueDeleteMutationParams) =>
  useMutation({
    mutationFn: () => deleteIssue({ issueId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, projectId, milestoneId],
        exact: false,
      });
    },
  });

export default useIssueDeleteMutation;
