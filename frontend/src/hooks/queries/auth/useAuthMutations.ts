import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import { postSendEmailVerification, SendEmailVerificationRequest } from '@/api/auth/emailApi';
import {
  postOauthSignin,
  postSignin,
  postSignout,
  postSignup,
  SigninRequest,
  SignupRequest,
  SocialSigninRequest,
} from '@/api/auth/signApi';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { removeAccessToken } from '@/utils/auth/tokenUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

// ======== 로그인 ========
export const useSigninMutation = () =>
  useMutation({
    mutationFn: (request: SigninRequest) => postSignin(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.AUTH.LOGIN_SUCCESS);
    },
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ========소셜 로그인 ========
export const useSocialSigninMutation = () =>
  useMutation({
    mutationFn: (request: SocialSigninRequest) => postOauthSignin(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.AUTH.LOGIN_SUCCESS);
    },
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 회원가입 ========
export const useSignupMutation = () =>
  useMutation({
    mutationFn: (request: SignupRequest) => postSignup(request),
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 이메일 인증 메일 전송 ========
export const useSendEmailVerificationMutation = () =>
  useMutation({
    mutationFn: (request: SendEmailVerificationRequest) => postSendEmailVerification(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.AUTH.SIGNUP_EMAIL_VERIFICATION);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 로그아웃 ========
export const useSignoutMutation = () =>
  useMutation({
    mutationFn: () => postSignout(),
    onMutate: () => {
      removeAccessToken();
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
