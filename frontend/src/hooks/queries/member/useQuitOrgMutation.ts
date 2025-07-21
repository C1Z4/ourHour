import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteQuitOrg, { DeleteQuitOrgRequest } from '@/api/member/deleteQuitOrg';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useQuitOrgMutation = () =>
  useMutation({
    mutationFn: (request: DeleteQuitOrgRequest) => deleteQuitOrg(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
