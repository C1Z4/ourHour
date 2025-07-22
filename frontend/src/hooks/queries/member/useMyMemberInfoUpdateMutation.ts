import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import putUpdateMyMemberInfo, { MemberInfoBase } from '@/api/member/putUpdateMyMemberInfo';
import { MEMBER_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UseMyMemberInfoUpdateMutationParams {
  orgId: number;
}

export const useMyMemberInfoUpdateMutation = ({ orgId }: UseMyMemberInfoUpdateMutationParams) =>
  useMutation({
    mutationFn: (request: MemberInfoBase) => putUpdateMyMemberInfo({ ...request, orgId }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [MEMBER_QUERY_KEYS.MY_MEMBER_INFO, orgId],
      });
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(TOAST_MESSAGES.ERROR.SERVER_ERROR);
    },
  });
