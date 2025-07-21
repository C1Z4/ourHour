import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteQuitOrgRequest {
  orgId: number;
}

const deleteQuitOrg = async (request: DeleteQuitOrgRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/organizations/${request.orgId}/members/me`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteQuitOrg;
