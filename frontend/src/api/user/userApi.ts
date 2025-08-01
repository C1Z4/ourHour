import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { logError } from '@/utils/auth/errorUtils';

import axiosInstance from '../axiosConfig';

// ======== 비밀번호 검증 ========
export interface PostVerifyPwdRequest {
  password: string;
}

export const postVerifyPwd = async (request: PostVerifyPwdRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/user/password-verification', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 계정 탈퇴 ========
export interface DeleteUserRequest {
  password: string;
}

export const deleteUser = async (request: DeleteUserRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete('/api/user', {
      data: request,
    });
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
