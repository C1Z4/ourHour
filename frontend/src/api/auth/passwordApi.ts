import { AxiosError } from 'axios';

import type { ApiResponse } from '@/types/apiTypes';

import axiosInstance from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 비밀번호 재설정 인증 ========
export interface GetPwdResetVerification {
  token: string;
}

export type FailReason = 'expired' | 'invalid' | 'already' | 'server';

export type PwdResetVerificationResult =
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

export async function getPwdResetVerification({
  token,
}: GetPwdResetVerification): Promise<PwdResetVerificationResult> {
  try {
    const resp = await axiosInstance.get<ApiResponse<void>>(
      '/api/auth/password-reset/verification',
      {
        params: { token },
        headers: { skipAuth: 'true', skipRefresh: 'true' },
      },
    );

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
}

// ======== 비밀번호 재설정 ========
export interface PatchPasswordReset {
  token: string;
  newPassword: string;
  newPasswordCheck: string;
}

export const patchPasswordReset = async (
  request: PatchPasswordReset,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.patch('/api/auth/password-reset', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 비밀번호 재설정 인증 요청 ========
export interface PostPasswordVerificationRequest {
  email: string;
}

export const postPasswordVerification = async (
  request: PostPasswordVerificationRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/password-reset/verification', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
