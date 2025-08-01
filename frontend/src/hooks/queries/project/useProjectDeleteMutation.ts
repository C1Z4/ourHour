import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteProject, { DeleteProjectRequest } from '@/api/project/deleteProject';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseProjectDeleteMutationParams {
  orgId: number;
}

const useProjectDeleteMutation = ({ orgId }: UseProjectDeleteMutationParams) =>
  useMutation({
    mutationFn: (request: DeleteProjectRequest) => deleteProject({ ...request, orgId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

export default useProjectDeleteMutation;
