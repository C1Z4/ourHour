import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteMember, { DeleteMemberRequest } from '@/api/org/deleteMember';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useMemberDeleteMutation = () =>
  useMutation({
    mutationFn: (request: DeleteMemberRequest) => deleteMember(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MEMBER_LIST],
        exact: false,
      });
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
