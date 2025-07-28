import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateIssue, { PutUpdateIssueRequest } from '@/api/project/putUpdateIssue';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showSuccessToast, TOAST_MESSAGES, showErrorToast } from '@/utils/toast';

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
    mutationFn: (request: PutUpdateIssueRequest) =>
      putUpdateIssue({ ...request, projectId, issueId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_DETAIL, issueId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, projectId, milestoneId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
