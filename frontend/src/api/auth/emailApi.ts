import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

// ======== 이메일 중복 확인 ========
export interface CheckDupEmailRequest {
  email: string;
}

export const getCheckDupEmail = async (
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

// ======== 인증 이메일 발송  ========
export interface SendVerificationEmailRequest {
  email: string;
}

export const postSendEmailVerification = async (
  request: SendVerificationEmailRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/email-verification', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 이메일 인증 ========
export interface VerifyEmailRequest {
  token: string;
}

export const getVerifyEmail = async (request: VerifyEmailRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.get('/api/auth/email-verification', {
      params: { token: request.token },
    });

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
