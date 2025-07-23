import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteOrg, { DeleteOrgRequest } from '@/api/org/deleteOrg';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const useOrgDeleteMutation = () =>
  useMutation({
    mutationFn: (request: DeleteOrgRequest) => deleteOrg(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST],
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
