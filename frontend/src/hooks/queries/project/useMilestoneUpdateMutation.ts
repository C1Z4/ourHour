import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateMilestone, { PutUpdateMilestoneRequest } from '@/api/project/putUpdateMilestone';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

interface UseMilestoneUpdateMutationParams {
  projectId: number;
  milestoneId: number;
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
        queryKey: [PROJECT_QUERY_KEYS.MILESTONE_LIST, projectId.toString()],
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
