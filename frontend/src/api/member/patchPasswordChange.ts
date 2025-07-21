import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PatchPasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  newPasswordCheck: string;
}

const patchPasswordChange = async (
  request: PatchPasswordChangeRequest,
): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.patch('/api/user/password', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default patchPasswordChange;
