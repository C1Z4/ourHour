import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface PostCreateOrgRequest {
  memberName: string;
  name: string;
  address: string;
  email: string;
  phone: string;
  businessNumber: string;
  representativeName: string;
  logoImgUrl: string;
}

const postCreateOrg = async (request: PostCreateOrgRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.post('/api/organizations', request);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default postCreateOrg;
