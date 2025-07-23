import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteIssue from '@/api/project/deleteIssue';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

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
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

export default useIssueDeleteMutation;
