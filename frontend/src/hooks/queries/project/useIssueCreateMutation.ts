import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateIssue, { PostCreateIssueRequest } from '@/api/project/postCreateIssue';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseIssueCreateMutationParams {
  projectId: number;
}

export const useIssueCreateMutation = ({ projectId }: UseIssueCreateMutationParams) =>
  useMutation({
    mutationFn: (request: PostCreateIssueRequest) => postCreateIssue(request),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.ISSUE_LIST, projectId, variables.milestoneId ?? null],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.CREATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
