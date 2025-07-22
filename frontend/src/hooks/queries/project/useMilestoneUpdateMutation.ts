import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateMilestone, { PutUpdateMilestoneRequest } from '@/api/project/putUpdateMilestone';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showSuccessToast, TOAST_MESSAGES, showErrorToast } from '@/utils/toast';

interface UseMilestoneUpdateMutationParams {
  projectId: number;
  milestoneId: number | null;
}

export const useMilestoneUpdateMutation = ({
  projectId,
  milestoneId,
}: UseMilestoneUpdateMutationParams) =>
  useMutation({
    mutationFn: (request: PutUpdateMilestoneRequest) =>
      putUpdateMilestone({ ...request, milestoneId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(TOAST_MESSAGES.ERROR.SERVER_ERROR);
    },
  });
