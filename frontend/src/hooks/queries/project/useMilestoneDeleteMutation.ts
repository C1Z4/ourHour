import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteMilestone from '@/api/project/deleteMilestone';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseMilestoneDeleteMutationParams {
  projectId: number;
  milestoneId: number | null;
}

const useMilestoneDeleteMutation = ({ projectId, milestoneId }: UseMilestoneDeleteMutationParams) =>
  useMutation({
    mutationFn: () => deleteMilestone({ milestoneId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(TOAST_MESSAGES.ERROR.SERVER_ERROR);
    },
  });

export default useMilestoneDeleteMutation;
