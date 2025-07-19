import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import deleteSignout from '@/api/auth/deleteSignout';
import postSendEmailVerification, {
  SendEmailVerificationRequest,
} from '@/api/auth/postSendEmailVerification';
import postSignin, { SigninRequest } from '@/api/auth/postSignin';
import postSignup, { SignupRequest } from '@/api/auth/postSignup';
import { handleHttpError, logError } from '@/utils/auth/errorUtils';

export const useSigninMutation = () =>
  useMutation({
    mutationFn: (request: SigninRequest) => postSignin(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });

export const useSignupMutation = () =>
  useMutation({
    mutationFn: (request: SignupRequest) => postSignup(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });

export const useSendEmailVerificationMutation = () =>
  useMutation({
    mutationFn: (request: SendEmailVerificationRequest) => postSendEmailVerification(request),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });

export const useSignoutMutation = () =>
  useMutation({
    mutationFn: () => deleteSignout(),
    onError: (error: AxiosError) => {
      logError(error);
      handleHttpError(error);
    },
  });
