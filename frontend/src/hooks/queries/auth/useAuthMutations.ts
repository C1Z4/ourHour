import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  OauthExtraInfoRequest,
  postOauthExtraInfo,
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
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ========소셜 로그인 추가 정보 ========
export const useOauthExtraInfoMutation = () =>
  useMutation({
    mutationFn: (request: OauthExtraInfoRequest) => postOauthExtraInfo(request),
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
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.AUTH.SIGNUP_SUCCESS);
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
