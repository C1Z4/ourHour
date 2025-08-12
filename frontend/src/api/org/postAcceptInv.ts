import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { logError } from '@/utils/auth/errorUtils';

import axiosInstance from '../axiosConfig';

interface PostAcceptInvRequest {
  token: string;
}

const postAcceptInv = async (request: PostAcceptInvRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/organizations/invitation/accept', request);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postAcceptInv;
