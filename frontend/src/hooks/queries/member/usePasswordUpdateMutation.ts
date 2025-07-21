import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import patchPasswordChange, { PatchPasswordChangeRequest } from '@/api/member/patchPasswordChange';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const usePasswordUpdateMutation = () =>
  useMutation({
    mutationFn: (request: PatchPasswordChangeRequest) => patchPasswordChange(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
