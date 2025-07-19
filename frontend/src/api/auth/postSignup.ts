import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface SignupRequest {
  email: string;
  password: string;
}

const postSignup = async (request: SignupRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signup', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postSignup;
