import { AxiosError } from 'axios';

import { ApiResponse } from '@/types/apiTypes';

import { axiosInstance } from '@/api/axiosConfig';
import { logError } from '@/utils/auth/errorUtils';

interface GetMyProjectListRequest {
  orgId: number;
}

export interface MyProject {
  projectId: number;
  name: string;
}

const getMyProjectList = async (
  request: GetMyProjectListRequest,
): Promise<ApiResponse<MyProject[]>> => {
  try {
    const response = await axiosInstance.get(`/api/organizations/${request.orgId}/projects`);

    return response.data;
  } catch (error: unknown) {
    logError(error as AxiosError);
    throw error;
  }
};

export default getMyProjectList;
