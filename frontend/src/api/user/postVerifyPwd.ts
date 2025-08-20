import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { logError } from '@/utils/auth/errorUtils';

import axiosInstance from '../axiosConfig';

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
