import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import patchPasswordChange, { PatchPasswordChangeRequest } from '@/api/member/patchPasswordChange';
import { getErrorMessage, handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

export const usePasswordUpdateMutation = () =>
  useMutation({
    mutationFn: (request: PatchPasswordChangeRequest) => patchPasswordChange(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.CRUD.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
