import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateProject, { PutUpdateProjectRequest } from '@/api/project/putUpdateProject';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useProjectUpdateMutation = () =>
  useMutation({
    mutationFn: (request: PutUpdateProjectRequest) => putUpdateProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projectSummaryList'] });
      queryClient.invalidateQueries({ queryKey: ['projectInfo'] });
      queryClient.invalidateQueries({ queryKey: ['projectParticipantList'] });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
