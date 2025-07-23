import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { OrgBaseInfo } from '@/api/org/getOrgInfo';
import putUpdateOrg from '@/api/org/putUpdateOrg';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseOrgUpdateMutationParams {
  orgId: number;
}

export const useOrgUpdateMutation = ({ orgId }: UseOrgUpdateMutationParams) =>
  useMutation({
    mutationFn: (request: OrgBaseInfo) => putUpdateOrg({ ...request, orgId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.ORG_INFO, orgId],
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
