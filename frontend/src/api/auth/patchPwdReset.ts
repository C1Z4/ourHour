import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PatchPasswordReset {
  token: string;
  newPassword: string;
  newPasswordCheck: string;
}

const patchPasswordReset = async (request: PatchPasswordReset): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.patch('/api/auth/password-reset', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default patchPasswordReset;
