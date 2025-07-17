import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateProject, { PostCreateProjectRequest } from '@/api/project/postCreateProject';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useProjectCreateMutation = () =>
  useMutation({
    mutationFn: (request: PostCreateProjectRequest) => postCreateProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projectSummaryList'] });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
