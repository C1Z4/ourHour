import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteUserRequest {
  password: string;
}

const deleteUser = async (request: DeleteUserRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete('/api/user', {
      data: request,
    });
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteUser;
