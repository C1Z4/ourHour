import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';
import { loginUser } from '@/utils/auth/tokenUtils';

export interface SigninRequest {
  email: string;
  password: string;
  platform: string;
}

interface SigninResponse {
  accessToken: string;
  refreshToken: string | null;
}

const postSignin = async (request: SigninRequest): Promise<ApiResponse<SigninResponse>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signin', request);
    loginUser(response.data.accessToken);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postSignin;
