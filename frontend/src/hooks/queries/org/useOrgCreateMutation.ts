import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postCreateOrg, { PostCreateOrgRequest } from '@/api/org/postCreateOrg';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useOrgCreateMutation = () =>
  useMutation({
    mutationFn: (request: PostCreateOrgRequest) => postCreateOrg(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MY_ORG_LIST],
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
