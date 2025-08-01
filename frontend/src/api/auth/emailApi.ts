import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';
import { FailReason } from '@/types/authTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 이메일 중복 확인 ========
export interface CheckDupEmailRequest {
  email: string;
}

export const getCheckEmail = async (
  request: CheckDupEmailRequest,
): Promise<ApiResponse<boolean>> => {
  try {
    const response = await axiosInstance.get('/api/auth/check-email', {
      params: { email: request.email },
    });

    return response.data;
  } catch (error) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 이메일 인증 ========
export interface GetEmailVerificationRequest {
  token: string;
}

export type EmailVerificationResult =
  | { ok: true; status: number; message: string }
  | { ok: false; status: number; reason: FailReason; message: string };

function mapErrorToReason(err: AxiosError<ApiResponse<void>>): FailReason {
  const status = err.response?.status;
  const msg = err.response?.data?.message ?? '';

  if (/만료/i.test(msg)) {
    return 'expired';
  }
  if (status === 400 || /유효하지/i.test(msg)) {
    return 'invalid';
  }
  if (/이미 인증/i.test(msg)) {
    return 'already';
  }
  return 'server';
}

export const getEmailVerification = async ({
  token,
}: GetEmailVerificationRequest): Promise<EmailVerificationResult> => {
  try {
    const resp = await axiosInstance.get<ApiResponse<void>>('/api/auth/email-verification', {
      params: { token },
      headers: { skipAuth: 'true', skipRefresh: 'true' },
    });

    const api = resp.data as ApiResponse<void> | undefined;
    const message = api?.message ?? '이메일 인증에 성공했습니다.';
    const status = resp.status;

    return { ok: true, status, message };
  } catch (error) {
    const err = error as AxiosError<ApiResponse<void>>;
    const api = err.response?.data;
    const status = err.response?.status ?? 500;
    const message = api?.message ?? '이메일 인증에 실패했습니다.';
    const reason = mapErrorToReason(err);

    return { ok: false, status, reason, message };
  }
};

// ======== 이메일 인증 요청 ========
export interface SendEmailVerificationRequest {
  email: string;
  password: string;
}

export const postSendEmailVerification = async (
  request: SendEmailVerificationRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/email-verification', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
