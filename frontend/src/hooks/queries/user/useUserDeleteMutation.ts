import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteUser, { DeleteUserRequest } from '@/api/user/deleteUser';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, TOAST_MESSAGES } from '@/utils/toast';

export const useUserDeleteMutation = () =>
  useMutation({
    mutationFn: (request: DeleteUserRequest) => deleteUser(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
      showErrorToast(TOAST_MESSAGES.ERROR.SERVER_ERROR);
    },
  });
