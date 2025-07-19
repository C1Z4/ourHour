import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PostPasswordVerificationRequest {
  password: string;
}

const postPasswordVerification = async (
  request: PostPasswordVerificationRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/user/password-verification', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postPasswordVerification;
