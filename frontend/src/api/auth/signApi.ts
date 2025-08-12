import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { clearAllMemberNames } from '@/stores/memberSlice';
import { clearCurrentProject } from '@/stores/projectSlice';
import { logError } from '@/utils/auth/errorUtils';
import { loginUser, logout } from '@/utils/auth/tokenUtils';

// ======== 로그인 ========
export interface SigninRequest extends SignupRequest {
  platform: string;
}

interface SigninResponse {
  accessToken: string;
  refreshToken: string | null;
}

export const postSignin = async (request: SigninRequest): Promise<ApiResponse<SigninResponse>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signin', request);
    const accessToken = response.data.data.accessToken;

    if (accessToken) {
      console.log(accessToken);
      loginUser(accessToken);
    }

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 회원가입 ========
export interface SignupRequest {
  email: string;
  password: string;
}

export const postSignup = async (request: SignupRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signup', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 로그아웃 ========
export const postSignout = async (): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signout');

    logout();
    clearAllMemberNames();
    clearCurrentProject();

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
