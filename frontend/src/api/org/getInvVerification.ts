import { AxiosError } from 'axios';

import type { ApiResponse } from '@/types/apiTypes';

import axiosInstance from '@/api/axiosConfig';

export interface GetInvVerification {
  token: string;
}

type FailReason =
  | 'expired'
  | 'invalid'
  | 'already'
  | 'accepted'
  | 'email_mismatch'
  | 'not_verified'
  | 'server'
  | (string & {});
export type InvVerificationResult =
  | { ok: true; status: number; message: string }
  | { ok: false; status: number; reason: FailReason; message: string };

function mapErrorToReason(err: AxiosError<ApiResponse<void>>): FailReason {
  const status = err.response?.status;
  const msg = err.response?.data?.message ?? '';

  if (/만료/i.test(msg)) {
    return 'expired';
  }
  if (/유효하지|잘못된/i.test(msg)) {
    return 'invalid';
  }
  if (/이미.*참여|이미.*인증/i.test(msg)) {
    return 'already';
  }
  if (/불일치|이메일.*다름/i.test(msg)) {
    return 'email_mismatch';
  }
  if (/인증.*안됨|검증.*안됨/i.test(msg)) {
    return 'not_verified';
  }
  return 'server';
}

export async function getInvVerification({
  token,
}: GetInvVerification): Promise<InvVerificationResult> {
  try {
    const resp = await axiosInstance.get<ApiResponse<void>>(
      '/api/organizations/invitation/verify',
      {
        params: { token },
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
