import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  CheckDupEmailRequest,
  getCheckDupEmail,
  getVerifyEmail,
  postSendEmailVerification,
  SendVerificationEmailRequest,
  VerifyEmailRequest,
} from '@/api/auth/emailApi';
import { getErrorMessage, logError } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

// ======== 이메일 중복 검사 ========
export const useCheckDupEmailMutation = () =>
  useMutation({
    mutationFn: (request: CheckDupEmailRequest) => getCheckDupEmail(request),
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 이메일 인증 메일 전송 ========
export const useSendEmailVerificationMutation = () =>
  useMutation({
    mutationFn: (request: SendVerificationEmailRequest) => postSendEmailVerification(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.AUTH.SEND_EMAIL_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 이메일 인증 ========
export const useVerifyEmail = () =>
  useMutation({
    mutationFn: (request: VerifyEmailRequest) => getVerifyEmail(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.AUTH.EMAIL_VERIFICATION_SUCCESS);
    },
    onError: (error: AxiosError) => {
      logError(error);
      showErrorToast(getErrorMessage(error));
    },
  });
