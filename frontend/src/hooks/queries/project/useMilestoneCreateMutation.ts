import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateMilestone, { PostCreateMilestoneRequest } from '@/api/project/postCreateMilestone';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showSuccessToast, TOAST_MESSAGES, showErrorToast } from '@/utils/toast';

interface UseMilestoneCreateMutationParams {
  projectId: number;
}

export const useMilestoneCreateMutation = ({ projectId }: UseMilestoneCreateMutationParams) =>
  useMutation({
    mutationFn: (request: PostCreateMilestoneRequest) => postCreateMilestone(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.CREATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(TOAST_MESSAGES.ERROR.SERVER_ERROR);
    },
  });
