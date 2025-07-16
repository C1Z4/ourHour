import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import postSignin, { SigninRequest } from '@/api/auth/postSignin';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useSigninMutation = () =>
  useMutation({
    mutationFn: (request: SigninRequest) => postSignin(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
