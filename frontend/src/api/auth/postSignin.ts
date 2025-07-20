import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { SignupRequest } from '@/api/auth/postSignup';
import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';
import { loginUser } from '@/utils/auth/tokenUtils';

export interface SigninRequest extends SignupRequest {
  platform: string;
}

interface SigninResponse {
  accessToken: string;
  refreshToken: string | null;
}

const postSignin = async (request: SigninRequest): Promise<ApiResponse<SigninResponse>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signin', request);

    const accessToken = response.data.accessToken;

    if (accessToken) {
      loginUser(accessToken);
    }

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postSignin;
