import { useMutation } from '@tanstack/react-query';

import { AxiosError } from 'axios';

import {
  AcceptInvRequest,
  getInvEmailVerification,
  postAcceptInv,
  postInvEmail,
  SendInvEmailRequest,
  VerifyInvEmailRequest,
} from '@/api/org/orgInvApi';
import { ORG_QUERY_KEYS } from '@/constants/queryKeys';
import { queryClient } from '@/main';
import { getErrorMessage } from '@/utils/auth/errorUtils';
import { showErrorToast, showSuccessToast, TOAST_MESSAGES } from '@/utils/toast';

// ======== 초대 메일 발송 ========
export const useSendInvEmailMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: SendInvEmailRequest) => postInvEmail(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.INV.SEND_EMAIL_SUCCESS);
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.INV_LIST, orgId] });
    },
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 초대 메일 인증 ========
export const useVerifyInvEmailMutation = () =>
  useMutation({
    mutationFn: (request: VerifyInvEmailRequest) => getInvEmailVerification(request),
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });

// ======== 초대 수락 ========
export const useAcceptInvMutation = (orgId: number) =>
  useMutation({
    mutationFn: (request: AcceptInvRequest) => postAcceptInv(request),
    onSuccess: () => {
      showSuccessToast(TOAST_MESSAGES.INV.ACCEPT_INV_SUCCESS);
      queryClient.invalidateQueries({ queryKey: [ORG_QUERY_KEYS.INV_LIST, orgId] });
    },
    onError: (error: AxiosError) => {
      showErrorToast(getErrorMessage(error));
    },
  });
