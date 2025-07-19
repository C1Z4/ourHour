import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateIssue, { PutUpdateIssueRequest } from '@/api/project/putUpdateIssue';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

interface UseIssueUpdateMutationParams {
  milestoneId: number | null;
  issueId: number;
  projectId: number;
}

export const useIssueUpdateMutation = ({
  milestoneId,
  issueId,
  projectId,
}: UseIssueUpdateMutationParams) =>
  useMutation({
    mutationFn: (request: PutUpdateIssueRequest) => putUpdateIssue(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, issueId],
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, projectId, milestoneId],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
