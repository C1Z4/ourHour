import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateProject, { PostCreateProjectRequest } from '@/api/project/postCreateProject';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

interface UseProjectCreateMutationParams {
  orgId: number;
}

export const useProjectCreateMutation = ({ orgId }: UseProjectCreateMutationParams) =>
  useMutation({
    mutationFn: (request: PostCreateProjectRequest) => postCreateProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId.toString()],
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
