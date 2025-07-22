import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface CheckDupEmailRequest {
  email: string;
}

/*
 * 이메일 중복 확인 (true = 사용 가능, false = 이미 사용 중)
 */
const getCheckEmail = async (request: CheckDupEmailRequest): Promise<ApiResponse<boolean>> => {
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

export default getCheckEmail;
