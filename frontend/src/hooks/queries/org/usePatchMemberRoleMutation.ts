import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import patchMemberRole, { PatchMemberRoleRequest } from '@/api/org/patchMemberRole';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

interface UsePatchMemberRoleMutationParams {
  orgId: number;
}

export const usePatchMemberRoleMutation = ({ orgId }: UsePatchMemberRoleMutationParams) =>
  useMutation({
    mutationFn: (request: PatchMemberRoleRequest) => patchMemberRole(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [ORG_QUERY_KEYS.MEMBER_LIST, orgId],
      });
      showSuccessToast('권한이 변경되었습니다.');
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(TOAST_MESSAGES.ERROR.SERVER_ERROR);
    },
  });
