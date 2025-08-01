import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateProject, { PostCreateProjectRequest } from '@/api/project/postCreateProject';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseProjectCreateMutationParams {
  orgId: number;
}

export const useProjectCreateMutation = ({ orgId }: UseProjectCreateMutationParams) =>
  useMutation({
    mutationFn: (request: PostCreateProjectRequest) => postCreateProject(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.CREATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
