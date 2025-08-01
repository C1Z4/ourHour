import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteMember, { DeleteMemberRequest } from '@/api/org/deleteMember';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorCode, getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const useMemberDeleteMutation = () =>
  useMutation({
    mutationFn: (request: DeleteMemberRequest) => deleteMember(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MEMBER_LIST],
        exact: false,
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.DELETE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      if (getErrorCode(error) === 10003) {
        showErrorToast(getErrorMessage(error));
      }
    },
  });
