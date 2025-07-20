import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

export interface DeleteOrgRequest {
  orgId: number;
}

const deleteOrg = async (request: DeleteOrgRequest): Promise<ApiResponse<void>> => {
  try {
    const response = await axiosInstance.delete(`/api/organizations/${request.orgId}`);
    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default deleteOrg;
