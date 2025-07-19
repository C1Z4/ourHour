import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface SendEmailVerificationRequest {
  email: string;
  password: string;
}

const postSendEmailVerification = async (
  request: SendEmailVerificationRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/email-verification', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postSendEmailVerification;
