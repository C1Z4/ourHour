import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteProjectParticipant, {
  DeleteProjectParticipantRequest,
} from '@/api/project/deleteProjectParticipant';
import { PROJECT_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseProjectParticipantDeleteMutationParams {
  orgId: number;
  projectId: number;
}

const useProjectParticipantDeleteMutation = ({
  orgId,
  projectId,
}: UseProjectParticipantDeleteMutationParams) =>
  useMutation({
    mutationFn: (request: DeleteProjectParticipantRequest) =>
      deleteProjectParticipant({ ...request, orgId, projectId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.SUMMARY_LIST, orgId],
        exact: false,
      });
      queryClient.invalidateQueries({
        queryKey: [PROJECT_QUERY_KEYS.PARTICIPANT_LIST, projectId],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

export default useProjectParticipantDeleteMutation;
