import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { logout } from '@/stores/authSlice';
import { logError } from '@/utils/auth/errorUtils';

import { axiosInstance } from '../axiosConfig';

const postSignout = async (): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/auth/signout');

    logout();

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postSignout;
