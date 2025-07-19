import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateProject, { PutUpdateProjectRequest } from '@/api/project/putUpdateProject';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

interface UseProjectUpdateMutationParams {
  orgId: number;
  projectId: number;
}

export const useProjectUpdateMutation = ({ orgId, projectId }: UseProjectUpdateMutationParams) =>
  useMutation({
    mutationFn: (request: PutUpdateProjectRequest) => putUpdateProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.PROJECT_INFO, projectId],
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, projectId, orgId],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
