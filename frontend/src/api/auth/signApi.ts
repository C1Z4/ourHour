import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { SOCIAL_LOGIN_PLATFORMS } from '@/constants/messages';
import { clearAllMemberNames } from '@/stores/memberSlice';
import { clearCurrentProject } from '@/stores/projectSlice';
import { logError } from '@/utils/auth/errorUtils';
import { loginUser, logout } from '@/utils/auth/tokenUtils';

// 플랫폼 타입 추출
export type SocialPlatform = (typeof SOCIAL_LOGIN_PLATFORMS)[keyof typeof SOCIAL_LOGIN_PLATFORMS];

// ======== 로그인 ========
export interface SigninRequest extends SignupRequest {
  platform: SocialPlatform;
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
      loginUser(accessToken);
      if (import.meta.env.DEV) {
        console.log(accessToken);
      }
    }
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 소셜 로그인 ========
export interface SocialSigninRequest {
  code: string;
  platform: SocialPlatform;
}

interface OauthSigninResponse {
  oauthId: string;
  platform: SocialPlatform;
}

export const postOauthSignin = async (
  request: SocialSigninRequest,
): Promise<ApiResponse<OauthSigninResponse>> => {
  try {
    const response = await axiosInstance.post('/api/auth/oauth-signin', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

// ======== 소셜 로그인 추가 정보 ========
export interface OauthExtraInfoRequest {
  oauthId: string;
  platform: SocialPlatform;
  email: string;
  password: string;
}

interface SocialSigninResponse extends SigninResponse {
  accessToken: string;
  refreshToken: string | null;
}

export const postOauthExtraInfo = async (
  request: OauthExtraInfoRequest,
): Promise<ApiResponse<SocialSigninResponse>> => {
  try {
    const response = await axiosInstance.post('/api/auth/oauth-extra-info', request);
    const accessToken = response.data.data.accessToken;

    if (accessToken) {
      loginUser(accessToken);
      if (import.meta.env.DEV) {
        console.log(accessToken);
      }
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

// ======== SSE 토큰 발급 ========
export const postSseToken = async (): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/sse-token');
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};
